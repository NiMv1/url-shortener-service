package com.urlshortener.service;

import com.urlshortener.dto.CreateShortUrlRequest;
import com.urlshortener.dto.ShortUrlResponse;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.exception.BusinessException;
import com.urlshortener.exception.ResourceNotFoundException;
import com.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Сервис для управления короткими ссылками
 */
@Service
public class ShortUrlService {

    private static final Logger log = LoggerFactory.getLogger(ShortUrlService.class);

    private final ShortUrlRepository shortUrlRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RabbitMQService rabbitMQService;

    public ShortUrlService(ShortUrlRepository shortUrlRepository,
                           ReactiveRedisTemplate<String, String> redisTemplate,
                           RabbitMQService rabbitMQService) {
        this.shortUrlRepository = shortUrlRepository;
        this.redisTemplate = redisTemplate;
        this.rabbitMQService = rabbitMQService;
    }

    private static final String CACHE_PREFIX = "url:";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${url-shortener.base-url}")
    private String baseUrl;

    @Value("${url-shortener.short-code-length}")
    private int shortCodeLength;

    @Value("${url-shortener.default-expiration-days}")
    private int defaultExpirationDays;

    /**
     * Создание короткой ссылки
     */
    public Mono<ShortUrlResponse> createShortUrl(CreateShortUrlRequest request, String userId) {
        String shortCode = request.getCustomAlias() != null ? 
                request.getCustomAlias() : generateShortCode();

        return shortUrlRepository.existsByShortCode(shortCode)
                .flatMap(exists -> {
                    if (exists) {
                        if (request.getCustomAlias() != null) {
                            return Mono.error(new BusinessException("Алиас уже занят: " + shortCode));
                        }
                        // Генерируем новый код если случайный уже существует
                        return createShortUrl(request, userId);
                    }

                    int expirationDays = request.getExpirationDays() != null ? 
                            request.getExpirationDays() : defaultExpirationDays;

                    ShortUrl shortUrl = ShortUrl.builder()
                            .shortCode(shortCode)
                            .originalUrl(request.getOriginalUrl())
                            .userId(userId)
                            .customAlias(request.getCustomAlias())
                            .expiresAt(LocalDateTime.now().plusDays(expirationDays))
                            .build();

                    return shortUrlRepository.save(shortUrl)
                            .doOnSuccess(saved -> {
                                // Кэшируем в Redis
                                cacheUrl(saved.getShortCode(), saved.getOriginalUrl()).subscribe();
                                // Отправляем событие в RabbitMQ
                                rabbitMQService.sendUrlCreatedEvent(saved);
                                log.info("Создана короткая ссылка: {} -> {}", saved.getShortCode(), saved.getOriginalUrl());
                            })
                            .map(saved -> ShortUrlResponse.fromEntity(saved, baseUrl));
                });
    }

    /**
     * Получение оригинального URL по короткому коду
     */
    public Mono<String> getOriginalUrl(String shortCode) {
        // Сначала проверяем кэш
        return redisTemplate.opsForValue().get(CACHE_PREFIX + shortCode)
                .switchIfEmpty(
                        // Если нет в кэше, ищем в БД
                        shortUrlRepository.findByShortCode(shortCode)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ссылка не найдена: " + shortCode)))
                                .flatMap(shortUrl -> {
                                    if (!shortUrl.isActive()) {
                                        return Mono.error(new BusinessException("Ссылка деактивирована"));
                                    }
                                    if (shortUrl.isExpired()) {
                                        return Mono.error(new BusinessException("Срок действия ссылки истёк"));
                                    }
                                    // Кэшируем и возвращаем
                                    return cacheUrl(shortCode, shortUrl.getOriginalUrl())
                                            .thenReturn(shortUrl.getOriginalUrl());
                                })
                );
    }

    /**
     * Регистрация клика по ссылке
     */
    public Mono<Void> recordClick(String shortCode, String ipAddress, String userAgent, String referer) {
        return shortUrlRepository.findByShortCode(shortCode)
                .flatMap(shortUrl -> {
                    shortUrl.incrementClickCount();
                    return shortUrlRepository.save(shortUrl);
                })
                .doOnSuccess(shortUrl -> {
                    // Отправляем событие клика в RabbitMQ для асинхронной обработки
                    rabbitMQService.sendClickEvent(shortCode, shortUrl.getId(), ipAddress, userAgent, referer);
                })
                .then();
    }

    /**
     * Получение ссылки по короткому коду
     */
    public Mono<ShortUrlResponse> getShortUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ссылка не найдена: " + shortCode)))
                .map(shortUrl -> ShortUrlResponse.fromEntity(shortUrl, baseUrl));
    }

    /**
     * Получение всех ссылок пользователя
     */
    public Flux<ShortUrlResponse> getUserUrls(String userId) {
        return shortUrlRepository.findByUserId(userId)
                .map(shortUrl -> ShortUrlResponse.fromEntity(shortUrl, baseUrl));
    }

    /**
     * Деактивация ссылки
     */
    public Mono<Void> deactivateUrl(String shortCode, String userId) {
        return shortUrlRepository.findByShortCode(shortCode)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ссылка не найдена: " + shortCode)))
                .flatMap(shortUrl -> {
                    if (!shortUrl.getUserId().equals(userId)) {
                        return Mono.error(new BusinessException("Нет прав на удаление этой ссылки"));
                    }
                    shortUrl.setActive(false);
                    return shortUrlRepository.save(shortUrl);
                })
                .doOnSuccess(shortUrl -> {
                    // Удаляем из кэша
                    redisTemplate.delete(CACHE_PREFIX + shortCode).subscribe();
                    log.info("Деактивирована ссылка: {}", shortCode);
                })
                .then();
    }

    /**
     * Удаление ссылки
     */
    public Mono<Void> deleteUrl(String shortCode, String userId) {
        return shortUrlRepository.findByShortCode(shortCode)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ссылка не найдена: " + shortCode)))
                .flatMap(shortUrl -> {
                    if (!shortUrl.getUserId().equals(userId)) {
                        return Mono.error(new BusinessException("Нет прав на удаление этой ссылки"));
                    }
                    return shortUrlRepository.delete(shortUrl);
                })
                .doOnSuccess(v -> {
                    redisTemplate.delete(CACHE_PREFIX + shortCode).subscribe();
                    log.info("Удалена ссылка: {}", shortCode);
                });
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(shortCodeLength);
        for (int i = 0; i < shortCodeLength; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private Mono<Boolean> cacheUrl(String shortCode, String originalUrl) {
        return redisTemplate.opsForValue()
                .set(CACHE_PREFIX + shortCode, originalUrl, Duration.ofHours(24));
    }
}
