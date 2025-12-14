@echo off
chcp 65001 >nul
:: Запуск PowerShell скрипта (без Terminate batch job и BUILD FAILURE)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0START_PROJECT.ps1"
