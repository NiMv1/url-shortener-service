# 📦 Инструкция по публикации в GitHub

## Шаг 1: Создание репозитория на GitHub

1. Перейдите на https://github.com/NiMv1
2. Нажмите **New repository**
3. Название: `url-shortener-service`
4. Описание: `Реактивный сервис сокращения ссылок (Spring WebFlux, MongoDB, Redis, RabbitMQ)`
5. **НЕ** добавляйте README, .gitignore или лицензию (они уже есть)
6. Нажмите **Create repository**

## Шаг 2: Инициализация локального репозитория

Откройте терминал в папке проекта и выполните:

```powershell
cd C:\Users\bnex4\CascadeProjects\url-shortener-service

# Инициализация Git
git init

# Добавление remote
git remote add origin https://github.com/NiMv1/url-shortener-service.git
```

## Шаг 3: Последовательные коммиты (для красивой истории)

```powershell
# Коммит 1: Базовая структура проекта
git add pom.xml README.md .gitignore
git commit -m "feat: инициализация проекта с Maven и базовой конфигурацией"

# Коммит 2: Конфигурация приложения
git add src/main/resources/
git commit -m "feat: добавлена конфигурация приложения (MongoDB, Redis, RabbitMQ)"

# Коммит 3: Сущности (Entities)
git add src/main/java/com/urlshortener/entity/
git commit -m "feat: добавлены MongoDB сущности (ShortUrl, ClickEvent, User)"

# Коммит 4: Репозитории
git add src/main/java/com/urlshortener/repository/
git commit -m "feat: добавлены реактивные репозитории для MongoDB"

# Коммит 5: DTO
git add src/main/java/com/urlshortener/dto/
git commit -m "feat: добавлены DTO классы для API"

# Коммит 6: Исключения
git add src/main/java/com/urlshortener/exception/
git commit -m "feat: добавлена обработка исключений"

# Коммит 7: Сервисы
git add src/main/java/com/urlshortener/service/
git commit -m "feat: добавлены бизнес-сервисы (ShortUrl, Analytics, RabbitMQ)"

# Коммит 8: Контроллеры
git add src/main/java/com/urlshortener/controller/
git commit -m "feat: добавлены REST контроллеры"

# Коммит 9: Конфигурация
git add src/main/java/com/urlshortener/config/
git commit -m "feat: добавлена конфигурация RabbitMQ"

# Коммит 10: Главный класс приложения
git add src/main/java/com/urlshortener/UrlShortenerApplication.java
git commit -m "feat: добавлен главный класс приложения"

# Коммит 11: Docker
git add Dockerfile docker-compose.yml
git commit -m "feat: добавлена Docker конфигурация"

# Коммит 12: Мониторинг
git add monitoring/
git commit -m "feat: добавлена конфигурация Prometheus"

# Коммит 13: CI/CD
git add .github/
git commit -m "ci: добавлен GitHub Actions pipeline"

# Коммит 14: Тесты
git add src/test/
git commit -m "test: добавлены unit и интеграционные тесты"
```

## Шаг 4: Публикация

```powershell
# Переименование ветки в main (если нужно)
git branch -M main

# Пуш в GitHub
git push -u origin main
```

## Шаг 5: Добавление тегов (опционально)

```powershell
git tag -a v1.0.0 -m "Первый релиз URL Shortener Service"
git push origin v1.0.0
```

---

## 🎯 Результат

После выполнения всех шагов у вас будет:
- ✅ Репозиторий с чистой историей коммитов
- ✅ Понятная структура изменений
- ✅ Профессиональный вид проекта для портфолио
