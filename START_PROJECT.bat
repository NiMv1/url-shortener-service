@echo off
chcp 65001 >nul
title URL Shortener Service
color 0A

:: Обработка закрытия консоли (Ctrl+C или закрытие окна)
if "%~1"=="cleanup" goto :cleanup

echo ╔══════════════════════════════════════════════════════════════╗
echo ║           URL SHORTENER SERVICE - ЗАПУСК ПРОЕКТА            ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

:: Переход в директорию проекта
cd /d "%~dp0"

:: Остановка предыдущих Java процессов на порту 8090
echo [0/5] Остановка предыдущих процессов...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING 2^>nul') do (
    echo Останавливаю процесс на порту 8090, PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)
:: Ожидание освобождения порта
timeout /t 3 /nobreak >nul
echo [OK] Порт 8090 освобождён
echo.

:: Настройка Java 17
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [INFO] Используется Java 17

:: Проверка Java
echo [1/5] Проверка Java...
java -version 2>nul
if errorlevel 1 (
    echo [ОШИБКА] Java не найдена!
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
echo [3/5] Запуск инфраструктуры Docker...
docker-compose up -d
if errorlevel 1 (
    echo [ОШИБКА] Не удалось запустить контейнеры!
    pause
    exit /b 1
)
echo [OK] Контейнеры запущены
echo.

:: Ожидание готовности
echo [4/5] Ожидание готовности сервисов (15 сек)...
timeout /t 15 /nobreak >nul
echo [OK] Сервисы готовы
echo.

:: Установка переменных окружения
set SPRING_DATA_REDIS_PORT=6380

echo [5/5] Запуск приложения (в этом окне)...
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║  Swagger UI:  http://localhost:8090/swagger-ui.html         ║
echo ║  RabbitMQ:    http://localhost:15672 (guest/guest)          ║
echo ║  Prometheus:  http://localhost:9091                         ║
echo ║  Grafana:     http://localhost:3001 (admin/admin)           ║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║  Для остановки: нажмите Ctrl+C                              ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

:: Открытие браузера
start "" "http://localhost:8090/swagger-ui.html"

:: Запуск приложения в текущем окне (блокирующий режим)
mvn spring-boot:run -DskipTests

:cleanup
:: После остановки (Ctrl+C или закрытие окна) - остановить Docker
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║              ОСТАНОВКА СЕРВИСОВ...                          ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo Остановка контейнеров Docker...
cd /d "%~dp0"
docker-compose down
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    ВСЕ СЕРВИСЫ ОСТАНОВЛЕНЫ                   ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo Нажмите любую клавишу для закрытия окна...
pause >nul
