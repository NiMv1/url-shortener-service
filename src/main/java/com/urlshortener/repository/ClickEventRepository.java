package com.urlshortener.repository;

import com.urlshortener.entity.ClickEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Реактивный репозиторий для работы с событиями кликов
 */
@Repository
public interface ClickEventRepository extends ReactiveMongoRepository<ClickEvent, String> {

    Flux<ClickEvent> findByShortCode(String shortCode);

    Flux<ClickEvent> findByShortCodeAndClickedAtBetween(String shortCode, LocalDateTime start, LocalDateTime end);

    Mono<Long> countByShortCode(String shortCode);

    Mono<Long> countByShortCodeAndClickedAtBetween(String shortCode, LocalDateTime start, LocalDateTime end);

    Flux<ClickEvent> findByShortUrlId(String shortUrlId);
}
