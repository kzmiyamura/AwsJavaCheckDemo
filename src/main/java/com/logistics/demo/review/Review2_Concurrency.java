package com.logistics.demo.review;

import java.util.concurrent.*;
import java.util.*;

/**
 * Amazon Q レビュー2: 並行処理
 * 
 * 検証項目:
 * - 15. マルチスレッド (Multithreading) - 複雑な競合状態
 * - 17. 並行コレクション (Concurrent Collections)
 * - 19. CompletableFuture
 * - 20. Virtual Threads (Java 21+)
 * - 21. GC (Garbage Collection)
 * 
 * 質問: このコードのスレッドセーフ性、並行処理の設計に問題はありますか？
 */

// 15. マルチスレッド: 複雑な競合状態
class InventorySystem {
    private Map<String, Integer> inventory = new ConcurrentHashMap<>();
    private int totalValue = 0; // 競合状態の可能性
    
    public void addItem(String item, int quantity, int price) {
        inventory.put(item, quantity);
        totalValue += quantity * price; // 非アトミック操作
    }
    
    public int getTotalValue() {
        return totalValue;
    }
}

// 17. 並行コレクション: 適切な並行コレクションの選択
class OrderQueue {
    private List<String> orders = Collections.synchronizedList(new ArrayList<>());
    
    public void processOrders() {
        // イテレーション中の同期化が不足
        for (String order : orders) {
            System.out.println("Processing: " + order);
        }
    }
}

// 19. CompletableFuture: 非同期処理の設計
class AsyncOrderService {
    public CompletableFuture<String> placeOrder(String orderId) {
        return CompletableFuture.supplyAsync(() -> {
            // 例外処理が不足
            return "Order placed: " + orderId;
        });
    }
    
    public void processMultipleOrders(List<String> orderIds) {
        // CompletableFutureの組み合わせ方は適切か？
        orderIds.forEach(id -> placeOrder(id));
    }
}

// 20. Virtual Threads: Virtual Threadsの使用は適切か？
class VirtualThreadExample {
    public void processRequests(List<String> requests) {
        // Java 21+ Virtual Threads (このプロジェクトはJava 17なのでコメントアウト)
        // ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // Java 17での代替実装
        ExecutorService executor = Executors.newFixedThreadPool(10);
        requests.forEach(req -> executor.submit(() -> process(req)));
        // executor.shutdown() が呼ばれていない問題もある
    }
    
    private void process(String request) {
        System.out.println("Processing: " + request);
    }
}

// 21. GC: メモリ管理は適切か？
class DataProcessor {
    private List<byte[]> largeDataCache = new ArrayList<>();
    
    public void processLargeData() {
        for (int i = 0; i < 10000; i++) {
            byte[] data = new byte[1024 * 1024]; // 1MB
            largeDataCache.add(data); // GC圧迫
        }
    }
}
