#!/bin/bash


./compile-tests.sh

echo ""
echo "=== Запуск тестов ==="

# Запускаем тесты
java -jar lib/junit-platform-console-standalone-1.10.0.jar \
     --class-path "out/main:out/test" \
     --scan-class-path