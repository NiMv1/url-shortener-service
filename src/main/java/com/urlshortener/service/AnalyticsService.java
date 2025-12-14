package com.urlshortener.service;

import com.urlshortener.dto.UrlStatsResponse;
import com.urlshortener.entity.ClickEvent;
import com.urlshortener.exception.ResourceNotFoundException;
import com.urlshortener.repository.ClickEventRepository;
import com.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис аналитики для обработки событий кликов
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private final ClickEventRepository clickEventRepository;
    private final ShortUrlRepository shortUrlRepository;

    public AnalyticsService(ClickEventRepository clickEventRepository, ShortUrlRepository shortUrlRepository) {
        this.clickEventRepository = clickEventRepository;
        this.shortUrlRepository = shortUrlRepository;
    }

    /**
     * Обработка события клика из RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queues.analytics}")
    public void processClickEvent(Map<String, Object> event) {
        if (!"CLICK".equals(event.get("type"))) {
            return;
        }

        String shortCode = (String) event.get("shortCode");
        String shortUrlId = (String) event.get("shortUrlId");
        String ipAddress = (String) event.get("ipAddress");
        String userAgent = (String) event.get("userAgent");
        String referer = (String) event.get("referer");

        // Парсинг User-Agent для определения устройства, браузера и ОС
        String deviceType = parseDeviceType(userAgent);
        String browser = parseBrowser(userAgent);
        String os = parseOS(userAgent);

        ClickEvent clickEvent = ClickEvent.builder()
                .shortCode(shortCode)
                .shortUrlId(shortUrlId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referer(referer)
                .deviceType(deviceType)
                .browser(browser)
                .os(os)
                .clickedAt(LocalDateTime.now())
                .build();

        clickEventRepository.save(clickEvent)
                .doOnSuccess(saved -> log.debug("Сохранено событие клика: {}", shortCode))
                .doOnError(error -> log.error("Ошибка сохранения события клика: {}", error.getMessage()))
                .subscribe();
    }

    /**
     * Получение статистики по ссылке
     */
    public Mono<UrlStatsResponse> getUrlStats(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ссылка не найдена: " + shortCode)))
                .flatMap(shortUrl -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
                    LocalDateTime startOfWeek = now.minusDays(7);
                    LocalDateTime startOfMonth = now.minusDays(30);

                    return Mono.zip(
                            clickEventRepository.countByShortCode(shortCode),
                            clickEventRepository.countByShortCodeAndClickedAtBetween(shortCode, startOfDay, now),
                            clickEventRepository.countByShortCodeAndClickedAtBetween(shortCode, startOfWeek, now),
                            clickEventRepository.countByShortCodeAndClickedAtBetween(shortCode, startOfMonth, now)
                    ).map(tuple -> UrlStatsResponse.builder()
                            .shortCode(shortCode)
                            .originalUrl(shortUrl.getOriginalUrl())
                            .totalClicks(tuple.getT1())
                            .clicksToday(tuple.getT2())
                            .clicksThisWeek(tuple.getT3())
                            .clicksThisMonth(tuple.getT4())
                            .clicksByCountry(new HashMap<>()) // Заполняется отдельным запросом
                            .deviceStats(new HashMap<>())
                            .browserStats(new HashMap<>())
                            .clicksByHour(new HashMap<>())
                            .createdAt(shortUrl.getCreatedAt())
                            .build());
                });
    }

    private String parseDeviceType(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        }
        return "Desktop";
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Opera")) return "Opera";
        return "Other";
    }

    private String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS") || userAgent.contains("iPhone")) return "iOS";
        return "Other";
    }
}
