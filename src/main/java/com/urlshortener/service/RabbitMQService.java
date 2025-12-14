package com.urlshortener.service;

import com.urlshortener.entity.ShortUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для отправки сообщений в RabbitMQ
 */
@Service
public class RabbitMQService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQService.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.exchanges.analytics}")
    private String analyticsExchange;

    @Value("${rabbitmq.routing-keys.click}")
    private String clickRoutingKey;

    @Value("${rabbitmq.routing-keys.created}")
    private String createdRoutingKey;

    /**
     * Отправка события клика
     */
    public void sendClickEvent(String shortCode, String shortUrlId, String ipAddress, String userAgent, String referer) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "CLICK");
        event.put("shortCode", shortCode);
        event.put("shortUrlId", shortUrlId);
        event.put("ipAddress", ipAddress);
        event.put("userAgent", userAgent);
        event.put("referer", referer);
        event.put("timestamp", LocalDateTime.now().toString());

        try {
            rabbitTemplate.convertAndSend(analyticsExchange, clickRoutingKey, event);
            log.debug("Отправлено событие клика для: {}", shortCode);
        } catch (Exception e) {
            log.error("Ошибка отправки события клика: {}", e.getMessage());
        }
    }

    /**
     * Отправка события создания ссылки
     */
    public void sendUrlCreatedEvent(ShortUrl shortUrl) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "URL_CREATED");
        event.put("shortCode", shortUrl.getShortCode());
        event.put("originalUrl", shortUrl.getOriginalUrl());
        event.put("userId", shortUrl.getUserId());
        event.put("timestamp", LocalDateTime.now().toString());

        try {
            rabbitTemplate.convertAndSend(analyticsExchange, createdRoutingKey, event);
            log.debug("Отправлено событие создания ссылки: {}", shortUrl.getShortCode());
        } catch (Exception e) {
            log.error("Ошибка отправки события создания: {}", e.getMessage());
        }
    }
}
