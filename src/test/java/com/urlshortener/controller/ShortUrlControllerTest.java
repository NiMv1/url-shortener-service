package com.urlshortener.controller;

import com.urlshortener.dto.ApiResponse;
import com.urlshortener.dto.CreateShortUrlRequest;
import com.urlshortener.dto.ShortUrlResponse;
import com.urlshortener.dto.UrlStatsResponse;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.ShortUrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Тесты контроллера ShortUrlController
 */
@WebFluxTest(ShortUrlController.class)
class ShortUrlControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ShortUrlService shortUrlService;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("POST /api/v1/urls - создание короткой ссылки")
    void createShortUrl_Success() {
        // Given
        ShortUrlResponse response = ShortUrlResponse.builder()
                .id("123")
                .shortCode("abc1234")
                .shortUrl("http://localhost:8090/abc1234")
                .originalUrl("https://example.com/long/url")
                .clickCount(0L)
                .active(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        when(shortUrlService.createShortUrl(any(CreateShortUrlRequest.class), anyString()))
                .thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originalUrl\": \"https://example.com/long/url\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.shortCode").isEqualTo("abc1234")
                .jsonPath("$.data.originalUrl").isEqualTo("https://example.com/long/url");
    }

    @Test
    @DisplayName("GET /api/v1/urls/{shortCode} - получение информации о ссылке")
    void getShortUrl_Success() {
        // Given
        ShortUrlResponse response = ShortUrlResponse.builder()
                .id("123")
                .shortCode("abc1234")
                .shortUrl("http://localhost:8090/abc1234")
                .originalUrl("https://example.com")
                .clickCount(10L)
                .active(true)
                .build();

        when(shortUrlService.getShortUrl("abc1234")).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/urls/abc1234")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.shortCode").isEqualTo("abc1234")
                .jsonPath("$.data.clickCount").isEqualTo(10);
    }

    @Test
    @DisplayName("GET /api/v1/urls/{shortCode}/stats - получение статистики")
    void getUrlStats_Success() {
        // Given
        UrlStatsResponse stats = UrlStatsResponse.builder()
                .shortCode("abc1234")
                .originalUrl("https://example.com")
                .totalClicks(100L)
                .clicksToday(10L)
                .clicksThisWeek(50L)
                .clicksThisMonth(100L)
                .deviceStats(Map.of("Desktop", 60L, "Mobile", 40L))
                .browserStats(Map.of("Chrome", 70L, "Firefox", 30L))
                .build();

        when(analyticsService.getUrlStats("abc1234")).thenReturn(Mono.just(stats));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/urls/abc1234/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.totalClicks").isEqualTo(100)
                .jsonPath("$.data.clicksToday").isEqualTo(10);
    }

    @Test
    @DisplayName("GET /api/v1/urls/user/{userId} - получение ссылок пользователя")
    void getUserUrls_Success() {
        // Given
        ShortUrlResponse url1 = ShortUrlResponse.builder()
                .shortCode("abc1234")
                .originalUrl("https://example1.com")
                .build();
        ShortUrlResponse url2 = ShortUrlResponse.builder()
                .shortCode("xyz5678")
                .originalUrl("https://example2.com")
                .build();

        when(shortUrlService.getUserUrls("user1")).thenReturn(Flux.just(url1, url2));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/urls/user/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ShortUrlResponse.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("DELETE /api/v1/urls/{shortCode} - деактивация ссылки")
    void deactivateUrl_Success() {
        // Given
        when(shortUrlService.deactivateUrl("abc1234", "user1")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/urls/abc1234")
                .header("X-User-Id", "user1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Ссылка деактивирована");
    }

    @Test
    @DisplayName("POST /api/v1/urls - ошибка валидации (пустой URL)")
    void createShortUrl_ValidationError() {
        // When & Then
        webTestClient.post()
                .uri("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originalUrl\": \"\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
