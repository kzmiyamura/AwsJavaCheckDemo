package com.logistics.demo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

/**
 * 統合検証: 4-26番の問題（コンパイラ警告を修正済み）
 */
public class IntegratedIssues {

    // ✅ 4. Stream API - Raw type修正済み
    public void streamWithGenerics(List<String> items) {
        items.stream().filter(x -> x != null).count();
    }
    
    // ❌ 5. リフレクション - セキュリティリスク（SpotBugsで検出期待）
    public void accessPrivateField(Object obj) throws Exception {
        Field field = obj.getClass().getDeclaredField("password");
        field.setAccessible(true);
        String password = (String) field.get(obj);
    }
    
    // ❌ 空catch（SpotBugsで検出期待）
    public void emptyCatch() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // 空catch
        }
    }
    
    // ❌ 16. 同期化なし（SpotBugsで検出期待）
    private int counter = 0;
    
    public void unsafeIncrement() {
        counter++;
    }
    
    // ❌ 22. メモリリーク可能性（SpotBugsで検出期待）
    private static Map<String, Object> staticCache = new HashMap<>();
    
    public void addToStaticCache(String key, Object value) {
        staticCache.put(key, value);
    }
    
    // ❌ 26. 広すぎる例外キャッチ（SpotBugsで検出期待）
    public void broadExceptionCatch() {
        try {
            riskyOperation();
        } catch (Throwable t) {
            // Throwableをキャッチ
        }
    }
    
    private void riskyOperation() throws Exception {
        throw new Exception("error");
    }
}
