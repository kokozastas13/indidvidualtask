@echo off
title Swing Bit Counter

echo Компиляция...
javac SwingBitCounter.java

if %errorlevel% neq 0 (
echo Ошибка компиляции!
pause
exit
)

echo Запуск окна...
start javaw SwingBitCounter

exit
