package com.logistics.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * 在庫管理クラス - マルチスレッド環境でHashMapを使用（スレッドセーフではない）
 */
public class InventoryManager {
    // BAD: HashMapはスレッドセーフではない
    private Map<String, Integer> inventory = new HashMap<>();
    
    public void addStock(String itemId, int quantity) {
        Integer current = inventory.get(itemId);
        if (current == null) {
            current = 0;
        }
        // BAD: 競合状態が発生する可能性
        inventory.put(itemId, current + quantity);
    }
    
    public void removeStock(String itemId, int quantity) {
        Integer current = inventory.get(itemId);
        if (current != null && current >= quantity) {
            // BAD: チェックと更新の間に他スレッドが介入する可能性
            inventory.put(itemId, current - quantity);
        }
    }
    
    public int getStock(String itemId) {
        Integer stock = inventory.get(itemId);
        return stock != null ? stock : 0;
    }
    
    // BAD: マルチスレッドで同時実行されると在庫数が不正確になる
    public void processOrders() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                addStock("ITEM001", 10);
                removeStock("ITEM001", 5);
            }).start();
        }
    }
}
