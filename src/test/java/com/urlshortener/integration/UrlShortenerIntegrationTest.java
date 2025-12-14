package com.urlshortener.integration;

import com.urlshortener.dto.CreateShortUrlRequest;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты с Testcontainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
class UrlShortenerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @BeforeEach
    void setUp() {
        shortUrlRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Полный цикл: создание, получение, редирект")
    void fullCycle_CreateGetRedirect() {
        // 1. Создание короткой ссылки
        String responseBody = webTestClient.post()
                .uri("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originalUrl\": \"https://google.com\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("google.com");
        assertThat(responseBody).contains("success\":true");

        // 2. Проверяем что ссылка сохранена в БД
        Long count = shortUrlRepository.count().block();
        assertThat(count).isEqualTo(1L);

        // 3. Получаем shortCode из ответа
        ShortUrl savedUrl = shortUrlRepository.findAll().blockFirst();
        assertThat(savedUrl).isNotNull();
        String shortCode = savedUrl.getShortCode();

        // 4. Получаем информацию о ссылке
        webTestClient.get()
                .uri("/api/v1/urls/" + shortCode)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.originalUrl").isEqualTo("https://google.com")
                .jsonPath("$.data.shortCode").isEqualTo(shortCode);

        // 5. Проверяем редирект
        webTestClient.get()
                .uri("/" + shortCode)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "https://google.com");
    }

    @Test
    @DisplayName("Создание ссылки с кастомным алиасом")
    void createWithCustomAlias() {
        // Given
        String customAlias = "mylink";

        // When & Then
        webTestClient.post()
                .uri("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originalUrl\": \"https://github.com\", \"customAlias\": \"" + customAlias + "\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.shortCode").isEqualTo(customAlias);

        // Проверяем в БД
        ShortUrl savedUrl = shortUrlRepository.findByShortCode(customAlias).block();
        assertThat(savedUrl).isNotNull();
        assertThat(savedUrl.getOriginalUrl()).isEqualTo("https://github.com");
    }

    @Test
    @DisplayName("Ошибка при дублировании алиаса")
    void duplicateAlias_Error() {
        // Given - создаём первую ссылку
        shortUrlRepository.save(ShortUrl.builder()
                .shortCode("taken")
                .originalUrl("https://example.com")
                .userId("user1")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build()).block();

        // When & Then - пытаемся создать с тем же алиасом
        webTestClient.post()
                .uri("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originalUrl\": \"https://other.com\", \"customAlias\": \"taken\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(msg -> assertThat((String) msg).contains("занят"));
    }

    @Test
    @DisplayName("Получение несуществующей ссылки - 404")
    void getNonExistent_NotFound() {
        webTestClient.get()
                .uri("/api/v1/urls/notexist")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Деактивация ссылки")
    void deactivateUrl() {
        // Given
        ShortUrl url = shortUrlRepository.save(ShortUrl.builder()
                .shortCode("todelete")
                .originalUrl("https://example.com")
                .userId("user1")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build()).block();

        // When
        webTestClient.delete()
                .uri("/api/v1/urls/todelete")
                .header("X-User-Id", "user1")
                .exchange()
                .expectStatus().isOk();

        // Then
        ShortUrl deactivated = shortUrlRepository.findByShortCode("todelete").block();
        assertThat(deactivated).isNotNull();
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @DisplayName("Получение ссылок пользователя")
    void getUserUrls() {
        // Given
        shortUrlRepository.save(ShortUrl.builder()
                .shortCode("url1")
                .originalUrl("https://example1.com")
                .userId("testuser")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build()).block();

        shortUrlRepository.save(ShortUrl.builder()
                .shortCode("url2")
                .originalUrl("https://example2.com")
                .userId("testuser")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build()).block();

        shortUrlRepository.save(ShortUrl.builder()
                .shortCode("url3")
                .originalUrl("https://example3.com")
                .userId("otheruser")
                .active(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build()).block();

        // When & Then
        webTestClient.get()
                .uri("/api/v1/urls/user/testuser")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2);
    }
}
