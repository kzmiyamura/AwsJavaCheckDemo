#!/bin/bash

echo "=========================================="
echo "アノテーション問題検出デモ"
echo "=========================================="
echo ""

echo "【ステップ1】アノテーション問題を含むコードでビルド"
echo "→ Checkstyle が @SuppressWarnings の誤用を検出します"
echo ""

export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean compile 2>&1 | grep -A 2 "SuppressWarnings\|BUILD FAILURE"

echo ""
echo "=========================================="
echo "検出された問題:"
echo "- @SuppressWarnings(\"all\") は広すぎる"
echo "- @SuppressWarnings(\"unchecked\") が不要"
echo "=========================================="
