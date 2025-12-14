package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * DTO для создания короткой ссылки
 */
public class CreateShortUrlRequest {

    @NotBlank(message = "URL обязателен")
    @URL(message = "Некорректный формат URL")
    private String originalUrl;

    @Size(max = 20, message = "Кастомный алиас не должен превышать 20 символов")
    private String customAlias;

    private Integer expirationDays;

    public CreateShortUrlRequest() {}

    public CreateShortUrlRequest(String originalUrl, String customAlias, Integer expirationDays) {
        this.originalUrl = originalUrl;
        this.customAlias = customAlias;
        this.expirationDays = expirationDays;
    }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }
    public Integer getExpirationDays() { return expirationDays; }
    public void setExpirationDays(Integer expirationDays) { this.expirationDays = expirationDays; }
}
