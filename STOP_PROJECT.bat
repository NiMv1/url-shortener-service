@echo off
chcp 65001 >nul
title URL Shortener - Остановка
color 0C

echo ╔══════════════════════════════════════════════════════════════╗
echo ║              ОСТАНОВКА URL SHORTENER SERVICE                ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

:: Остановка Java процесса на порту 8090
echo Остановка Java процесса...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING 2^>nul') do (
    echo Останавливаю процесс на порту 8090, PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

:: Остановка Docker контейнеров
echo Остановка контейнеров Docker...
docker-compose down

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    ВСЕ СЕРВИСЫ ОСТАНОВЛЕНЫ                   ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo Нажмите любую клавишу для закрытия окна...
pause >nul
