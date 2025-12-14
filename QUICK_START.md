# 🚀 Быстрый старт — URL Shortener Service

Эта инструкция поможет вам запустить проект на своём компьютере.

---

## 📋 Требования

| Компонент | Версия | Ссылка |
|-----------|--------|--------|
| **Java** | 17+ (рекомендуется 21) | [Eclipse Temurin](https://adoptium.net/) |
| **Maven** | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| **Docker** | 20+ | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| **Docker Compose** | 2.0+ | Включён в Docker Desktop |

---

## 🐳 Способ 1: Запуск через Docker Compose (рекомендуется)

Самый простой способ — запустить всё через Docker:

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/NiMv1/url-shortener-service.git
cd url-shortener-service

# 2. Запустите все сервисы
docker-compose up -d

# 3. Проверьте статус
docker-compose ps
```

### Доступные сервисы:

| Сервис | URL | Описание |
|--------|-----|----------|
| **API** | http://localhost:8080 | Основной API |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Документация API |
| **MongoDB** | localhost:27017 | База данных |
| **Redis** | localhost:6379 | Кэш |
| **RabbitMQ** | http://localhost:15672 | Очередь сообщений (guest/guest) |
| **Prometheus** | http://localhost:9090 | Метрики |
| **Grafana** | http://localhost:3000 | Дашборды (admin/admin) |

### Остановка:

```bash
docker-compose down
```

---

## 💻 Способ 2: Локальный запуск (для разработки)

### Шаг 1: Запустите инфраструктуру

```bash
# Запустите только MongoDB, Redis и RabbitMQ
docker-compose up -d mongodb redis rabbitmq
```

### Шаг 2: Соберите проект

```bash
# Сборка без тестов (быстрее)
mvn clean package -DskipTests

# Или с тестами
mvn clean package
```

### Шаг 3: Запустите приложение

```bash
# Через Maven
mvn spring-boot:run

# Или через JAR
java -jar target/url-shortener-service-1.0.0.jar
```

---

## 🧪 Проверка работоспособности

### 1. Health Check

```bash
curl http://localhost:8080/actuator/health
```

Ожидаемый ответ:
```json
{"status":"UP"}
```

### 2. Создание короткой ссылки

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://github.com/NiMv1"}'
```

Ожидаемый ответ:
```json
{
  "success": true,
  "data": {
    "shortCode": "abc123",
    "shortUrl": "http://localhost:8080/abc123",
    "originalUrl": "https://github.com/NiMv1"
  }
}
```

### 3. Редирект по короткой ссылке

Откройте в браузере: `http://localhost:8080/abc123`

Вас перенаправит на оригинальный URL.

### 4. Получение статистики

```bash
curl http://localhost:8080/api/v1/urls/abc123/stats
```

---

## 📖 API Endpoints

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `POST` | `/api/v1/urls` | Создать короткую ссылку |
| `GET` | `/api/v1/urls/{shortCode}` | Получить информацию о ссылке |
| `GET` | `/api/v1/urls/{shortCode}/stats` | Статистика по ссылке |
| `GET` | `/api/v1/urls/user/{userId}` | Все ссылки пользователя |
| `DELETE` | `/api/v1/urls/{shortCode}` | Деактивировать ссылку |
| `GET` | `/{shortCode}` | Редирект на оригинальный URL |

---

## 🔧 Конфигурация

Основные настройки в `src/main/resources/application.yml`:

```yaml
url-shortener:
  base-url: http://localhost:8080  # Базовый URL для коротких ссылок
  short-code-length: 6             # Длина короткого кода
  default-expiration-days: 30      # Срок действия ссылки (дни)
```

---

## ❓ Частые проблемы

### Docker не запускается
```bash
# Проверьте, что Docker Desktop запущен
docker info
```

### Порт занят
```bash
# Найдите процесс на порту 8080
netstat -ano | findstr :8080

# Или измените порт в application.yml
server:
  port: 8081
```

### MongoDB не подключается
```bash
# Проверьте, что контейнер запущен
docker ps | grep mongo
```

---

## 📚 Дополнительно

- **Swagger UI**: http://localhost:8080/swagger-ui.html — интерактивная документация
- **Actuator**: http://localhost:8080/actuator — метрики и health checks
- **Prometheus**: http://localhost:9090 — мониторинг метрик

---

**Автор**: [NiMv1](https://github.com/NiMv1)
