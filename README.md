# 🔗 URL Shortener Service

> **Pet-проект для портфолио Middle Java Backend Developer**

Полностью реактивный сервис сокращения ссылок с аналитикой кликов, демонстрирующий владение современным стеком технологий.

---

## 📋 Содержание

- [Архитектура](#-архитектура)
- [Технологии](#-технологии)
- [Быстрый старт](#-быстрый-старт)
- [API Документация](#-api-документация)
- [Структура проекта](#-структура-проекта)
- [Тестирование](#-тестирование)
- [Мониторинг](#-мониторинг)

---

## 🏗️ Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                    URL Shortener Service                     │
│                   (Spring WebFlux - Reactive)                │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┬─────────────┐
        │             │             │             │
        ▼             ▼             ▼             ▼
┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐
│  MongoDB  │  │   Redis   │  │ RabbitMQ  │  │Prometheus │
│  (Data)   │  │  (Cache)  │  │ (Events)  │  │(Metrics)  │
└───────────┘  └───────────┘  └───────────┘  └───────────┘
```

### Ключевые особенности архитектуры:
- **Неблокирующий I/O** — Spring WebFlux + Project Reactor
- **Кэширование** — Redis для быстрого доступа к ссылкам (TTL 24ч)
- **Асинхронная аналитика** — события кликов через RabbitMQ
- **Горизонтальное масштабирование** — stateless сервис

---

## 🛠️ Технологии

| Категория | Технологии |
|-----------|------------|
| **Backend** | Java 17, Spring Boot 3.2, Spring WebFlux (Reactive) |
| **База данных** | MongoDB 7.0 (NoSQL, реактивный драйвер) |
| **Кэширование** | Redis 7 (реактивный клиент) |
| **Messaging** | RabbitMQ 3.12 |
| **Безопасность** | Spring Security, JWT |
| **Мониторинг** | Prometheus, Grafana, Micrometer |
| **Контейнеризация** | Docker, Docker Compose |
| **Документация** | OpenAPI/Swagger (WebFlux) |
| **Тестирование** | JUnit 5, Reactor Test, Testcontainers |
| **Resilience** | Resilience4j (Rate Limiter) |

## ✨ Особенности

- **Полностью реактивный** — Spring WebFlux + Reactor
- **Высокая производительность** — неблокирующий I/O
- **Кэширование** — Redis для быстрого доступа к ссылкам
- **Аналитика** — отслеживание кликов, устройств, браузеров
- **Rate Limiting** — защита от злоупотреблений
- **Кастомные алиасы** — возможность задать свой короткий код
- **Срок действия** — автоматическое истечение ссылок

## 🚀 Быстрый старт

### Требования
- Docker & Docker Compose
- Java 17+
- Maven 3.9+

### Запуск с Docker Compose

```bash
# Клонирование репозитория
git clone https://github.com/your-username/url-shortener-service.git
cd url-shortener-service

# Запуск всей инфраструктуры
docker-compose up -d

# Проверка статуса
docker-compose ps
```

### Доступные сервисы

| Сервис | URL |
|--------|-----|
| URL Shortener API | http://localhost:8090 |
| Swagger UI | http://localhost:8090/swagger-ui.html |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| Prometheus | http://localhost:9091 |
| Grafana | http://localhost:3001 (admin/admin) |

## 📝 API Endpoints

### Создание короткой ссылки
```http
POST /api/v1/urls
Content-Type: application/json

{
  "originalUrl": "https://example.com/very/long/url",
  "customAlias": "mylink",      // опционально
  "expirationDays": 30          // опционально
}
```

### Редирект
```http
GET /{shortCode}
→ 301 Redirect to original URL
```

### Получение информации о ссылке
```http
GET /api/v1/urls/{shortCode}
```

### Получение статистики
```http
GET /api/v1/urls/{shortCode}/stats
```

### Получение ссылок пользователя
```http
GET /api/v1/urls/user/{userId}
```

### Деактивация ссылки
```http
DELETE /api/v1/urls/{shortCode}
X-User-Id: user123
```

## 📊 Аналитика

Сервис собирает следующую статистику:
- **Общее количество кликов**
- **Клики по периодам** (сегодня, неделя, месяц)
- **География** (страна, город)
- **Устройства** (Desktop, Mobile, Tablet)
- **Браузеры** (Chrome, Firefox, Safari, etc.)
- **Операционные системы**
- **Источники переходов** (referer)

## 🔧 Конфигурация

### Переменные окружения

```bash
# MongoDB
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DB=url_shortener

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET=your-secret-key

# Application
BASE_URL=http://localhost:8090
```

## 🧪 Тестирование

```bash
# Unit тесты
mvn test

# Integration тесты с Testcontainers
mvn verify -DskipUnitTests

# Все тесты
mvn verify
```

## 📈 Высоконагруженность

Проект демонстрирует паттерны для высоконагруженных систем:

- **Реактивный стек** — неблокирующая обработка тысяч запросов
- **MongoDB** — горизонтальное масштабирование (sharding)
- **Redis кэширование** — снижение нагрузки на БД
- **Асинхронная аналитика** — RabbitMQ для обработки событий
- **Rate Limiting** — защита от DDoS
- **Connection Pooling** — эффективное использование соединений

## 📄 Лицензия

MIT License

## 👤 Автор

Middle Java Backend Developer Portfolio Project
