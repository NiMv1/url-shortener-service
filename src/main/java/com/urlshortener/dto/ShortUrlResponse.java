package com.urlshortener.dto;

import com.urlshortener.entity.ShortUrl;
import java.time.LocalDateTime;

/**
 * DTO для ответа с данными короткой ссылки
 */
public class ShortUrlResponse {

    private String id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Long clickCount;
    private boolean active;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public ShortUrlResponse() {}

    public ShortUrlResponse(String id, String shortCode, String shortUrl, String originalUrl,
                            Long clickCount, boolean active, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
        this.active = active;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getShortUrl() { return shortUrl; }
    public String getOriginalUrl() { return originalUrl; }
    public Long getClickCount() { return clickCount; }
    public boolean isActive() { return active; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }
    public void setActive(boolean active) { this.active = active; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ShortUrlResponse fromEntity(ShortUrl shortUrl, String baseUrl) {
        return ShortUrlResponse.builder()
                .id(shortUrl.getId())
                .shortCode(shortUrl.getShortCode())
                .shortUrl(baseUrl + "/" + shortUrl.getShortCode())
                .originalUrl(shortUrl.getOriginalUrl())
                .clickCount(shortUrl.getClickCount())
                .active(shortUrl.isActive())
                .expiresAt(shortUrl.getExpiresAt())
                .createdAt(shortUrl.getCreatedAt())
                .build();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, shortCode, shortUrl, originalUrl;
        private Long clickCount;
        private boolean active;
        private LocalDateTime expiresAt, createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder shortCode(String shortCode) { this.shortCode = shortCode; return this; }
        public Builder shortUrl(String shortUrl) { this.shortUrl = shortUrl; return this; }
        public Builder originalUrl(String originalUrl) { this.originalUrl = originalUrl; return this; }
        public Builder clickCount(Long clickCount) { this.clickCount = clickCount; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ShortUrlResponse build() {
            return new ShortUrlResponse(id, shortCode, shortUrl, originalUrl, clickCount, active, expiresAt, createdAt);
        }
    }
}
