package com.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для статистики по ссылке
 */
public class UrlStatsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private Long clicksToday;
    private Long clicksThisWeek;
    private Long clicksThisMonth;
    private Map<String, Long> clicksByCountry;
    private Map<String, Long> deviceStats;
    private Map<String, Long> browserStats;
    private Map<String, Long> clicksByHour;
    private LocalDateTime createdAt;
    private LocalDateTime lastClickAt;

    public UrlStatsResponse() {}

    // Getters
    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public Long getTotalClicks() { return totalClicks; }
    public Long getClicksToday() { return clicksToday; }
    public Long getClicksThisWeek() { return clicksThisWeek; }
    public Long getClicksThisMonth() { return clicksThisMonth; }
    public Map<String, Long> getClicksByCountry() { return clicksByCountry; }
    public Map<String, Long> getDeviceStats() { return deviceStats; }
    public Map<String, Long> getBrowserStats() { return browserStats; }
    public Map<String, Long> getClicksByHour() { return clicksByHour; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastClickAt() { return lastClickAt; }

    // Setters
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public void setTotalClicks(Long totalClicks) { this.totalClicks = totalClicks; }
    public void setClicksToday(Long clicksToday) { this.clicksToday = clicksToday; }
    public void setClicksThisWeek(Long clicksThisWeek) { this.clicksThisWeek = clicksThisWeek; }
    public void setClicksThisMonth(Long clicksThisMonth) { this.clicksThisMonth = clicksThisMonth; }
    public void setClicksByCountry(Map<String, Long> clicksByCountry) { this.clicksByCountry = clicksByCountry; }
    public void setDeviceStats(Map<String, Long> deviceStats) { this.deviceStats = deviceStats; }
    public void setBrowserStats(Map<String, Long> browserStats) { this.browserStats = browserStats; }
    public void setClicksByHour(Map<String, Long> clicksByHour) { this.clicksByHour = clicksByHour; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLastClickAt(LocalDateTime lastClickAt) { this.lastClickAt = lastClickAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UrlStatsResponse instance = new UrlStatsResponse();

        public Builder shortCode(String shortCode) { instance.shortCode = shortCode; return this; }
        public Builder originalUrl(String originalUrl) { instance.originalUrl = originalUrl; return this; }
        public Builder totalClicks(Long totalClicks) { instance.totalClicks = totalClicks; return this; }
        public Builder clicksToday(Long clicksToday) { instance.clicksToday = clicksToday; return this; }
        public Builder clicksThisWeek(Long clicksThisWeek) { instance.clicksThisWeek = clicksThisWeek; return this; }
        public Builder clicksThisMonth(Long clicksThisMonth) { instance.clicksThisMonth = clicksThisMonth; return this; }
        public Builder clicksByCountry(Map<String, Long> clicksByCountry) { instance.clicksByCountry = clicksByCountry; return this; }
        public Builder deviceStats(Map<String, Long> deviceStats) { instance.deviceStats = deviceStats; return this; }
        public Builder browserStats(Map<String, Long> browserStats) { instance.browserStats = browserStats; return this; }
        public Builder clicksByHour(Map<String, Long> clicksByHour) { instance.clicksByHour = clicksByHour; return this; }
        public Builder createdAt(LocalDateTime createdAt) { instance.createdAt = createdAt; return this; }
        public Builder lastClickAt(LocalDateTime lastClickAt) { instance.lastClickAt = lastClickAt; return this; }

        public UrlStatsResponse build() { return instance; }
    }
}
