package com.logistics.demo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 生成AIが起こしがちなジェネリクス関連のミス
 */
public class GenericsIssues {

    // ❌ ミス1: Raw type の使用（型安全性なし）
    private List items = new ArrayList();
    
    // ❌ ミス2: Raw type の Map
    private Map cache = new HashMap();
    
    // ❌ ミス3: Raw type を返すメソッド
    public List getItems() {
        return items;
    }
    
    // ❌ ミス4: Raw type を引数に取るメソッド
    public void addItems(List newItems) {
        items.addAll(newItems);
    }
    
    // ❌ ミス5: 型パラメータなしのコレクション生成
    public void processData() {
        List data = new ArrayList();
        data.add("string");
        data.add(123); // 異なる型を混在できてしまう
    }
    
    // ❌ ミス6: 不適切なキャスト
    public String getFirstItem() {
        if (!items.isEmpty()) {
            return (String) items.get(0); // ClassCastException のリスク
        }
        return null;
    }
    
    // ❌ ミス7: ジェネリックメソッドの型パラメータ欠如
    public Object findById(Object id) {
        return cache.get(id);
    }
    
    // ✅ 正しい例（比較用）
    private List<String> typedItems = new ArrayList<>();
    private Map<String, Object> typedCache = new HashMap<>();
    
    public List<String> getTypedItems() {
        return typedItems;
    }
    
    public <T> T findByIdTyped(String id, Class<T> type) {
        Object value = typedCache.get(id);
        return type.cast(value);
    }
}
