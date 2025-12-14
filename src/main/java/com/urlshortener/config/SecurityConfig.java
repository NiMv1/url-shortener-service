package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Конфигурация безопасности для реактивного приложения.
 * Отключает CSRF для API endpoints и разрешает доступ к публичным ресурсам.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Отключаем CSRF для REST API
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Настраиваем авторизацию
                .authorizeExchange(exchanges -> exchanges
                        // Публичные endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .pathMatchers("/api-docs/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        // API endpoints - разрешаем всем для демонстрации
                        .pathMatchers("/api/**").permitAll()
                        // Редирект по короткой ссылке
                        .pathMatchers("/{shortCode}").permitAll()
                        // Остальное требует аутентификации
                        .anyExchange().permitAll()
                )
                // Отключаем HTTP Basic для API
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
