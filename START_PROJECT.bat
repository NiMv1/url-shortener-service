@echo off
chcp 65001 >nul
title URL Shortener Service - Запуск проекта
color 0A

echo ╔══════════════════════════════════════════════════════════════╗
echo ║           URL SHORTENER SERVICE - ЗАПУСК ПРОЕКТА            ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

:: Переход в директорию проекта
cd /d "%~dp0"

:: Настройка Java
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

:: Проверка Java
echo [1/5] Проверка Java...
java -version 2>nul
if errorlevel 1 (
    echo [ОШИБКА] Java не найдена! Установите Java 21.
    pause
    exit /b 1
)
echo [OK] Java найдена
echo.

:: Проверка Docker
echo [2/5] Проверка Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ОШИБКА] Docker не запущен! Запустите Docker Desktop.
    pause
    exit /b 1
)
echo [OK] Docker запущен
echo.

:: Запуск инфраструктуры
echo [3/5] Запуск MongoDB, Redis, RabbitMQ...
docker-compose up -d mongodb redis rabbitmq
if errorlevel 1 (
    echo [ОШИБКА] Не удалось запустить контейнеры!
    pause
    exit /b 1
)
echo [OK] Контейнеры запущены
echo.

:: Ожидание готовности контейнеров
echo [4/5] Ожидание готовности сервисов (15 сек)...
timeout /t 15 /nobreak >nul
echo [OK] Сервисы готовы
echo.

:: Открытие браузера с вкладками
echo [5/5] Открытие браузера...
timeout /t 3 /nobreak >nul
start "" "http://localhost:8090/actuator/health"
timeout /t 1 /nobreak >nul
start "" "http://localhost:8090/swagger-ui.html"
timeout /t 1 /nobreak >nul
start "" "http://localhost:15672"
echo [OK] Вкладки браузера открыты
echo.

echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    ЗАПУСК ПРИЛОЖЕНИЯ                        ║
echo ║                                                              ║
echo ║  Ссылки для проверки:                                       ║
echo ║  • Health:     http://localhost:8090/actuator/health        ║
echo ║  • Swagger:    http://localhost:8090/swagger-ui.html        ║
echo ║  • RabbitMQ:   http://localhost:15672 (guest/guest)         ║
echo ║                                                              ║
echo ║  Для остановки: закройте это окно или нажмите Ctrl+C        ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

:: Установка порта Redis (в docker-compose он на 6380)
set REDIS_PORT=6380

:: Запуск приложения (при закрытии окна - приложение остановится)
call mvn spring-boot:run -q

:: После остановки приложения - остановить контейнеры
echo.
echo Остановка контейнеров...
docker-compose down
echo Готово!
pause
