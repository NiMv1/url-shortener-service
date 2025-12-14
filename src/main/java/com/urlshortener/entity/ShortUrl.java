package com.urlshortener.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Сущность короткой ссылки (MongoDB документ)
 */
@Document(collection = "short_urls")
public class ShortUrl {

    @Id
    private String id;

    @Indexed(unique = true)
    private String shortCode;

    private String originalUrl;

    private String userId;

    private String customAlias;

    private Long clickCount = 0L;

    private boolean active = true;

    private LocalDateTime expiresAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public ShortUrl() {}

    public ShortUrl(String id, String shortCode, String originalUrl, String userId, 
                    String customAlias, Long clickCount, boolean active, 
                    LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.userId = userId;
        this.customAlias = customAlias;
        this.clickCount = clickCount != null ? clickCount : 0L;
        this.active = active;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public String getUserId() { return userId; }
    public String getCustomAlias() { return customAlias; }
    public Long getClickCount() { return clickCount; }
    public boolean isActive() { return active; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }
    public void setActive(boolean active) { this.active = active; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public void incrementClickCount() {
        this.clickCount++;
    }

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String shortCode;
        private String originalUrl;
        private String userId;
        private String customAlias;
        private Long clickCount = 0L;
        private boolean active = true;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder shortCode(String shortCode) { this.shortCode = shortCode; return this; }
        public Builder originalUrl(String originalUrl) { this.originalUrl = originalUrl; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder customAlias(String customAlias) { this.customAlias = customAlias; return this; }
        public Builder clickCount(Long clickCount) { this.clickCount = clickCount; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ShortUrl build() {
            return new ShortUrl(id, shortCode, originalUrl, userId, customAlias, 
                               clickCount, active, expiresAt, createdAt, updatedAt);
        }
    }
}
