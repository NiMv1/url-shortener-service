package com.urlshortener.service;

import com.urlshortener.dto.CreateShortUrlRequest;
import com.urlshortener.dto.ShortUrlResponse;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.exception.ResourceNotFoundException;
import com.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для ShortUrlService
 */
@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private RabbitMQService rabbitMQService;

    private ShortUrlService shortUrlService;

    @BeforeEach
    void setUp() {
        shortUrlService = new ShortUrlService(
                shortUrlRepository,
                redisTemplate,
                rabbitMQService
        );
    }

    @Test
    @DisplayName("Создание короткой ссылки - успешно")
    void createShortUrl_Success() {
        // Given
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://example.com/very/long/url");
        
        ShortUrl savedUrl = ShortUrl.builder()
                .id("123")
                .shortCode("abc1234")
                .originalUrl("https://example.com/very/long/url")
                .userId("user1")
                .clickCount(0L)
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        when(shortUrlRepository.existsByShortCode(anyString())).thenReturn(Mono.just(false));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(Mono.just(savedUrl));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(anyString(), anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        doNothing().when(rabbitMQService).sendUrlCreatedEvent(any(ShortUrl.class));

        // When & Then
        StepVerifier.create(shortUrlService.createShortUrl(request, "user1"))
                .expectNextMatches(response -> 
                        response.getOriginalUrl().equals("https://example.com/very/long/url") &&
                        response.getShortCode().equals("abc1234"))
                .verifyComplete();

        verify(shortUrlRepository).save(any(ShortUrl.class));
    }

    @Test
    @DisplayName("Получение оригинального URL из кэша")
    void getOriginalUrl_FromCache() {
        // Given
        String shortCode = "abc1234";
        String originalUrl = "https://example.com";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + shortCode)).thenReturn(Mono.just(originalUrl));

        // When & Then
        StepVerifier.create(shortUrlService.getOriginalUrl(shortCode))
                .expectNext(originalUrl)
                .verifyComplete();

        verify(shortUrlRepository, never()).findByShortCode(anyString());
    }

    @Test
    @DisplayName("Получение оригинального URL из БД при отсутствии в кэше")
    void getOriginalUrl_FromDatabase() {
        // Given
        String shortCode = "abc1234";
        ShortUrl shortUrl = ShortUrl.builder()
                .shortCode(shortCode)
                .originalUrl("https://example.com")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + shortCode)).thenReturn(Mono.empty());
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Mono.just(shortUrl));
        when(valueOperations.set(anyString(), anyString(), any(Duration.class))).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(shortUrlService.getOriginalUrl(shortCode))
                .expectNext("https://example.com")
                .verifyComplete();

        verify(shortUrlRepository).findByShortCode(shortCode);
    }

    @Test
    @DisplayName("Ошибка при получении несуществующей ссылки")
    void getOriginalUrl_NotFound() {
        // Given
        String shortCode = "notexist";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + shortCode)).thenReturn(Mono.empty());
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(shortUrlService.getOriginalUrl(shortCode))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Деактивация ссылки - успешно")
    void deactivateUrl_Success() {
        // Given
        String shortCode = "abc1234";
        String userId = "user1";
        
        ShortUrl shortUrl = ShortUrl.builder()
                .id("123")
                .shortCode(shortCode)
                .userId(userId)
                .active(true)
                .build();
        
        ShortUrl deactivatedUrl = ShortUrl.builder()
                .id("123")
                .shortCode(shortCode)
                .userId(userId)
                .active(false)
                .build();
        
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Mono.just(shortUrl));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(Mono.just(deactivatedUrl));
        when(redisTemplate.delete("url:" + shortCode)).thenReturn(Mono.just(1L));

        // When & Then
        StepVerifier.create(shortUrlService.deactivateUrl(shortCode, userId))
                .verifyComplete();

        verify(shortUrlRepository).save(any(ShortUrl.class));
    }
}
