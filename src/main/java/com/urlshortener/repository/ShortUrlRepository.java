package com.urlshortener.repository;

import com.urlshortener.entity.ShortUrl;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Реактивный репозиторий для работы с короткими ссылками
 */
@Repository
public interface ShortUrlRepository extends ReactiveMongoRepository<ShortUrl, String> {

    Mono<ShortUrl> findByShortCode(String shortCode);

    Mono<Boolean> existsByShortCode(String shortCode);

    Flux<ShortUrl> findByUserId(String userId);

    Flux<ShortUrl> findByUserIdAndActive(String userId, boolean active);

    Mono<Long> countByUserId(String userId);

    Flux<ShortUrl> findByExpiresAtBeforeAndActive(LocalDateTime dateTime, boolean active);

    Mono<Void> deleteByShortCode(String shortCode);
}
