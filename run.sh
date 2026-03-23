#!/bin/bash

# Script para compilar e executar o projeto de Estruturas de Dados

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"

# Criar diretório de saída se não existir
mkdir -p "$OUT_DIR"

echo "Compilando projeto..."
javac -d "$OUT_DIR" "$SRC_DIR"/*.java

if [ $? -ne 0 ]; then
    echo "Erro durante a compilação!"
    exit 1
fi

echo "Compilação concluída!"
echo "Executando aplicação..."

# Executar a aplicação
java -cp "$OUT_DIR" Main

exit $?
