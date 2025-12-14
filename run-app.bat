@echo off
:: Вспомогательный скрипт для запуска приложения
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set SPRING_DATA_REDIS_PORT=6380
cd /d "%~dp0"
mvn spring-boot:run -DskipTests
