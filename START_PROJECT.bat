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

:: Остановка предыдущих Java процессов на порту 8090
echo [0/5] Остановка предыдущих процессов...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING 2^>nul') do taskkill /F /PID %%a 2>nul
echo [OK] Порт 8090 освобождён
echo.

:: Настройка Java 17 (требуется для совместимости с Lombok)
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo [INFO] Используется Java 17

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

:: Запуск ВСЕЙ инфраструктуры (включая Prometheus, Grafana)
echo [3/5] Запуск всей инфраструктуры Docker...
docker-compose up -d
if errorlevel 1 (
    echo [ОШИБКА] Не удалось запустить контейнеры!
    pause
    exit /b 1
)
echo [OK] Все контейнеры запущены
echo.

:: Ожидание готовности контейнеров
echo [4/5] Ожидание готовности сервисов (20 сек)...
timeout /t 20 /nobreak >nul
echo [OK] Сервисы готовы
echo.

:: Установка переменных окружения
set SPRING_DATA_REDIS_PORT=6380

echo [5/5] Запуск приложения...
start "URL Shortener (8090)" cmd /k "cd /d "%~dp0" && set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot && set SPRING_DATA_REDIS_PORT=6380 && mvn spring-boot:run -DskipTests"

:: Ожидание запуска приложения
timeout /t 25 /nobreak >nul

:: Открытие браузера с вкладками
echo Открытие браузера...
start "" "http://localhost:8090/swagger-ui.html"
timeout /t 1 /nobreak >nul
start "" "http://localhost:15672"
timeout /t 1 /nobreak >nul
start "" "http://localhost:9091"
timeout /t 1 /nobreak >nul
start "" "http://localhost:3001"
echo [OK] Вкладки браузера открыты
echo.

echo ╔══════════════════════════════════════════════════════════════╗
echo ║              URL SHORTENER SERVICE ЗАПУЩЕН!                 ║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║  API:                                                        ║
echo ║  • URL Shortener API:  http://localhost:8090                ║
echo ║  • Swagger UI:         http://localhost:8090/swagger-ui.html║
echo ║  • Health Check:       http://localhost:8090/actuator/health║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║  Инфраструктура:                                             ║
echo ║  • RabbitMQ:           http://localhost:15672 (guest/guest) ║
echo ║  • MongoDB:            localhost:27017                      ║
echo ║  • Redis:              localhost:6380                       ║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║  Мониторинг:                                                 ║
echo ║  • Prometheus:         http://localhost:9091                ║
echo ║  • Grafana:            http://localhost:3001 (admin/admin)  ║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║  Для остановки: нажмите любую клавишу в этом окне           ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

pause

:: После остановки - остановить всё
echo.
echo Остановка сервисов...
:: Остановка Java процесса на порту 8090
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING 2^>nul') do (
    echo Остановка процесса PID: %%a
    taskkill /F /PID %%a 2>nul
)
:: Закрытие окна URL Shortener
taskkill /FI "WINDOWTITLE eq URL Shortener*" /F 2>nul
echo Остановка контейнеров...
docker-compose down
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    ВСЕ СЕРВИСЫ ОСТАНОВЛЕНЫ                   ║
echo ╚══════════════════════════════════════════════════════════════╝
pause
