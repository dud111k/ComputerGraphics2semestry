#!/bin/bash

echo "=== Компиляция проекта ==="

# Создаем папки для скомпилированных файлов
mkdir -p out/main
mkdir -p out/test

# Компилируем основной код
echo "Компиляция основного кода..."
find src -name "*.java" > sources.txt
javac -d out/main @sources.txt
rm sources.txt

# Компилируем тесты
echo "Компиляция тестов..."
find test -name "*.java" > test-sources.txt
javac -d out/test -cp "out/main:lib/junit-platform-console-standalone-1.10.0.jar" @test-sources.txt
rm test-sources.txt

echo "Готово!"