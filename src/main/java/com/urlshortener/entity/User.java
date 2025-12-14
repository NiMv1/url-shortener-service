package com.urlshortener.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность пользователя (MongoDB документ)
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private Set<String> roles = new HashSet<>(Set.of("ROLE_USER"));
    private boolean enabled = true;
    private String apiKey;
    private Long urlsCreated = 0L;
    private Long totalClicks = 0L;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User() {}

    public User(String id, String username, String email, String password, Set<String> roles,
                boolean enabled, String apiKey, Long urlsCreated, Long totalClicks,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? roles : new HashSet<>(Set.of("ROLE_USER"));
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.urlsCreated = urlsCreated != null ? urlsCreated : 0L;
        this.totalClicks = totalClicks != null ? totalClicks : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Set<String> getRoles() { return roles; }
    public boolean isEnabled() { return enabled; }
    public String getApiKey() { return apiKey; }
    public Long getUrlsCreated() { return urlsCreated; }
    public Long getTotalClicks() { return totalClicks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public void setUrlsCreated(Long urlsCreated) { this.urlsCreated = urlsCreated; }
    public void setTotalClicks(Long totalClicks) { this.totalClicks = totalClicks; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, username, email, password, apiKey;
        private Set<String> roles = new HashSet<>(Set.of("ROLE_USER"));
        private boolean enabled = true;
        private Long urlsCreated = 0L, totalClicks = 0L;
        private LocalDateTime createdAt, updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder urlsCreated(Long urlsCreated) { this.urlsCreated = urlsCreated; return this; }
        public Builder totalClicks(Long totalClicks) { this.totalClicks = totalClicks; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            return new User(id, username, email, password, roles, enabled, apiKey,
                           urlsCreated, totalClicks, createdAt, updatedAt);
        }
    }
}
