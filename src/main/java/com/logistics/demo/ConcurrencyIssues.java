package com.logistics.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * 15. マルチスレッド (Multithreading) - 既存のInventoryManager.javaで検出済み
 */

/**
 * 16. 同期化 (Synchronization)
 */
class SynchronizationIssues {
    private int counter = 0;
    
    // ❌ 同期化なしで共有変数を変更
    public void increment() {
        counter++;
    }
    
    // ✅ 正しい同期化
    public synchronized void safeIncrement() {
        counter++;
    }
}

/**
 * 17. 並行コレクション (Concurrent Collections) - 既存で検出済み
 */

/**
 * 21. ガベージコレクション (GC) - 静的解析では検出不可
 */

/**
 * 22. 参照型 (Reference Types)
 */
class ReferenceIssues {
    // ❌ メモリリークの可能性
    private static Map<String, Object> cache = new HashMap<>();
    
    public void addToCache(String key, Object value) {
        cache.put(key, value); // 永遠に保持される
    }
}

/**
 * 24. 例外処理 (Exceptions) - 既存のSecurityIssues.javaで検出済み
 */

/**
 * 25. try-with-resources - 既存のS3ResultUploader.javaで検出済み
 */

/**
 * 26. チェック例外 vs 非チェック例外
 */
class ExceptionTypeIssues {
    // ❌ 広すぎる例外キャッチ
    public void broadCatch() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // すべての例外をキャッチ
        }
    }
    
    private void riskyOperation() throws Exception {
        throw new Exception("error");
    }
}
