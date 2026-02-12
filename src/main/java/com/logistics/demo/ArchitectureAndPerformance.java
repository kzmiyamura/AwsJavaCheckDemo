package com.logistics.demo;

/**
 * 27. AOP (Aspect-Oriented Programming)
 * 静的解析では検出不可 - 実行時の動作
 */

/**
 * 28. DI (Dependency Injection)
 */
class DIIssues {
    // ❌ ハードコードされた依存関係
    private DatabaseConnection db = new DatabaseConnection();
    
    // ✅ DIを使うべき
    // private DatabaseConnection db;
    // public DIIssues(DatabaseConnection db) { this.db = db; }
}

class DatabaseConnection {
    public void connect() {}
}

/**
 * 29. IoC (Inversion of Control)
 * 静的解析では検出不可 - 設計パターン
 */

/**
 * 30. デザインパターン
 */
class SingletonIssues {
    // ❌ スレッドセーフではないSingleton
    private static SingletonIssues instance;
    
    public static SingletonIssues getInstance() {
        if (instance == null) {
            instance = new SingletonIssues();
        }
        return instance;
    }
}

/**
 * 31. MVC/MVVM
 * 静的解析では検出不可 - アーキテクチャパターン
 */

/**
 * 32-36. フレームワーク (Spring, JPA等)
 * 静的解析では検出不可 - 実行時動作
 */

/**
 * 37-40. ビルド・依存管理 (Maven, Gradle)
 * ビルドツール固有 - pom.xml/build.gradleで管理
 */

/**
 * 41-44. テスト (JUnit, Mockito)
 * テスト実行が必要 - 静的解析では不可
 */

/**
 * 45-48. セキュリティ
 * FindSecurityBugsで検出済み
 */

/**
 * 49-52. パフォーマンス
 */
class PerformanceIssues {
    // ❌ 非効率なString連結
    public String inefficientConcat(String[] items) {
        String result = "";
        for (String item : items) {
            result += item; // StringBuilder使うべき
        }
        return result;
    }
    
    // ❌ 不要なオブジェクト生成
    public void unnecessaryObjectCreation() {
        for (int i = 0; i < 1000; i++) {
            String s = new String("test"); // リテラル使うべき
        }
    }
}

/**
 * 53-56. データ処理 (Serialization, JSON, XML, NIO)
 * 実行時動作 - 静的解析では限界あり
 */

/**
 * 57-60. コード品質
 * Checkstyle/SpotBugsで検出済み
 */
