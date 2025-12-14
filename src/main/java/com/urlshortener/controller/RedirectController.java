package com.urlshortener.controller;

import com.urlshortener.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Контроллер для редиректов по коротким ссылкам
 */
@RestController
@Tag(name = "Redirect", description = "Редирект по коротким ссылкам")
public class RedirectController {

    private final ShortUrlService shortUrlService;

    public RedirectController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Редирект на оригинальный URL")
    public Mono<Void> redirect(
            @PathVariable String shortCode,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String referer = request.getHeaders().getFirst("Referer");

        return shortUrlService.getOriginalUrl(shortCode)
                .flatMap(originalUrl -> {
                    // Асинхронно записываем клик
                    shortUrlService.recordClick(shortCode, ipAddress, userAgent, referer).subscribe();
                    
                    // Выполняем редирект
                    response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
                    response.getHeaders().setLocation(URI.create(originalUrl));
                    return response.setComplete();
                });
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
}
