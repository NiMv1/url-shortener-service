package com.urlshortener.controller;

import com.urlshortener.dto.ApiResponse;
import com.urlshortener.dto.CreateShortUrlRequest;
import com.urlshortener.dto.ShortUrlResponse;
import com.urlshortener.dto.UrlStatsResponse;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Реактивный REST контроллер для управления короткими ссылками
 */
@RestController
@RequestMapping("/api/v1/urls")
@Tag(name = "URL Shortener", description = "API для сокращения ссылок")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final AnalyticsService analyticsService;

    public ShortUrlController(ShortUrlService shortUrlService, AnalyticsService analyticsService) {
        this.shortUrlService = shortUrlService;
        this.analyticsService = analyticsService;
    }

    @PostMapping
    @Operation(summary = "Создание короткой ссылки")
    public Mono<ResponseEntity<ApiResponse<ShortUrlResponse>>> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        String effectiveUserId = userId != null ? userId : "anonymous";
        
        return shortUrlService.createShortUrl(request, effectiveUserId)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.success(response, "Ссылка создана")));
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Получение информации о ссылке")
    public Mono<ResponseEntity<ApiResponse<ShortUrlResponse>>> getShortUrl(@PathVariable String shortCode) {
        return shortUrlService.getShortUrl(shortCode)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @GetMapping("/{shortCode}/stats")
    @Operation(summary = "Получение статистики по ссылке")
    public Mono<ResponseEntity<ApiResponse<UrlStatsResponse>>> getUrlStats(@PathVariable String shortCode) {
        return analyticsService.getUrlStats(shortCode)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получение всех ссылок пользователя")
    public Flux<ShortUrlResponse> getUserUrls(@PathVariable String userId) {
        return shortUrlService.getUserUrls(userId);
    }

    @DeleteMapping("/{shortCode}")
    @Operation(summary = "Деактивация ссылки")
    public Mono<ResponseEntity<ApiResponse<Void>>> deactivateUrl(
            @PathVariable String shortCode,
            @RequestHeader("X-User-Id") String userId) {
        return shortUrlService.deactivateUrl(shortCode, userId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success(null, "Ссылка деактивирована"))));
    }
}
