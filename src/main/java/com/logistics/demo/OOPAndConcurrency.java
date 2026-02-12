package com.logistics.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 10. 継承 (Inheritance)
 */
class InheritanceIssuesOOP extends Object {
    // ❌ @Override付け忘れ（1番で検出済み）
    public String toString() {
        return "InheritanceIssues";
    }
}

/**
 * 11. インターフェース (Interfaces)
 */
interface PaymentProcessor {
    void process(double amount);
}

class CreditCardProcessor implements PaymentProcessor {
    // ❌ @Override付け忘れ（1番で検出済み）
    public void process(double amount) {
        System.out.println("Processing: " + amount);
    }
}

/**
 * 12. ポリモーフィズム (Polymorphism)
 */
class PolymorphismIssues {
    // 静的解析では設計の妥当性は判断不可
    public void processPayment(Object payment) {
        if (payment instanceof String) {
            // 型チェックでポリモーフィズムを回避
        }
    }
}

/**
 * 13. カプセル化 (Encapsulation)
 */
class EncapsulationIssues {
    // ❌ publicフィールド（カプセル化違反）
    public String password;
    public int balance;
    
    // ✅ 正しいカプセル化
    private String username;
    public String getUsername() { return username; }
}

/**
 * 14. 抽象クラス (Abstract Classes)
 */
abstract class AbstractProcessor {
    abstract void process();
}

/**
 * 15. マルチスレッド - 既存のInventoryManager.javaで検証済み
 */

/**
 * 16. 同期化 - 既存のIntegratedIssues.javaで検証済み
 */

/**
 * 17. 並行コレクション
 */
class ConcurrentCollectionIssues {
    // Checkstyleで検出済み（HashMap使用）
}

/**
 * 18. Executor Framework
 */
class ExecutorIssues {
    // ❌ ExecutorServiceをクローズしない（リソースリーク）
    public void runTasks() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.submit(() -> System.out.println("Task"));
        // executor.shutdown() がない
    }
}

/**
 * 19. CompletableFuture
 */
class CompletableFutureIssues {
    // 静的解析では非同期処理の妥当性は判断不可
}

/**
 * 20. Virtual Threads (Java 21+)
 */
class VirtualThreadIssues {
    // Java 21+の機能、静的解析では判断不可
}
