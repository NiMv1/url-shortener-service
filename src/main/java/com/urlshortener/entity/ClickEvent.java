package com.urlshortener.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Сущность события клика для аналитики
 */
@Document(collection = "click_events")
public class ClickEvent {

    @Id
    private String id;

    @Indexed
    private String shortCode;
    private String shortUrlId;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private String country;
    private String city;
    private String deviceType;
    private String browser;
    private String os;

    @CreatedDate
    @Indexed
    private LocalDateTime clickedAt;

    public ClickEvent() {}

    public ClickEvent(String id, String shortCode, String shortUrlId, String ipAddress,
                      String userAgent, String referer, String country, String city,
                      String deviceType, String browser, String os, LocalDateTime clickedAt) {
        this.id = id;
        this.shortCode = shortCode;
        this.shortUrlId = shortUrlId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.country = country;
        this.city = city;
        this.deviceType = deviceType;
        this.browser = browser;
        this.os = os;
        this.clickedAt = clickedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getShortUrlId() { return shortUrlId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getReferer() { return referer; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getDeviceType() { return deviceType; }
    public String getBrowser() { return browser; }
    public String getOs() { return os; }
    public LocalDateTime getClickedAt() { return clickedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void setShortUrlId(String shortUrlId) { this.shortUrlId = shortUrlId; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public void setReferer(String referer) { this.referer = referer; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public void setBrowser(String browser) { this.browser = browser; }
    public void setOs(String os) { this.os = os; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, shortCode, shortUrlId, ipAddress, userAgent, referer;
        private String country, city, deviceType, browser, os;
        private LocalDateTime clickedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder shortCode(String shortCode) { this.shortCode = shortCode; return this; }
        public Builder shortUrlId(String shortUrlId) { this.shortUrlId = shortUrlId; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public Builder referer(String referer) { this.referer = referer; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder deviceType(String deviceType) { this.deviceType = deviceType; return this; }
        public Builder browser(String browser) { this.browser = browser; return this; }
        public Builder os(String os) { this.os = os; return this; }
        public Builder clickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; return this; }

        public ClickEvent build() {
            return new ClickEvent(id, shortCode, shortUrlId, ipAddress, userAgent, referer,
                                  country, city, deviceType, browser, os, clickedAt);
        }
    }
}
