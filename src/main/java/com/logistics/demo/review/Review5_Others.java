package com.logistics.demo.review;

/**
 * Amazon Q レビュー5: その他
 * 
 * 検証項目:
 * - 7. レコード (Records) - Java 14+
 * - 8. シールドクラス (Sealed Classes) - Java 17+
 * - 9. パターンマッチング (Pattern Matching) - Java 17+
 * - 14. 抽象クラス (Abstract Classes)
 * - 52. JITコンパイル
 * - 54. JSON/XML処理
 * - 60. 技術的負債管理
 * 
 * 質問: このコードの設計、パフォーマンス、保守性に問題はありますか？
 */

// 7. レコード: レコードの使い方は適切か？
record CustomerDTO(String name, String email, int age) {
    // バリデーションは？
    // 不変性の利点を活かしているか？
}

// 8. シールドクラス: シールドクラスの設計は適切か？
sealed interface PaymentMethodReview permits CreditCardReview, DebitCardReview, CashReview {
    double processPayment(double amount);
}

final class CreditCardReview implements PaymentMethodReview {
    public double processPayment(double amount) {
        return amount * 1.02; // 手数料
    }
}

final class DebitCardReview implements PaymentMethodReview {
    public double processPayment(double amount) {
        return amount;
    }
}

final class CashReview implements PaymentMethodReview {
    public double processPayment(double amount) {
        return amount;
    }
}

// 9. パターンマッチング: パターンマッチングの使い方は適切か？
class PaymentProcessorReview {
    public String processPayment(Object payment) {
        if (payment instanceof CreditCardReview card) {
            return "Credit card payment";
        } else if (payment instanceof DebitCardReview card) {
            return "Debit card payment";
        } else if (payment instanceof CashReview cash) {
            return "Cash payment";
        }
        return "Unknown payment";
    }
    
    // switch式のパターンマッチングは使えるか？
}

// 14. 抽象クラス: 抽象クラスの設計は適切か？
abstract class AbstractRepositoryReview<T> {
    abstract void save(T entity);
    abstract T findById(Long id);
    
    // 共通処理
    public void logOperation(String operation) {
        System.out.println("Operation: " + operation);
    }
}

class ProductRepositoryImplReview extends AbstractRepositoryReview<ProductReview5> {
    @Override
    void save(ProductReview5 entity) {
        logOperation("save");
    }
    
    @Override
    ProductReview5 findById(Long id) {
        logOperation("findById");
        return null;
    }
}

// 52. JITコンパイル: パフォーマンスを考慮したコードか？
class PerformanceCritical {
    public void hotMethod() {
        // JITコンパイラが最適化しやすいコードか？
        for (int i = 0; i < 1000000; i++) {
            calculate(i);
        }
    }
    
    private int calculate(int n) {
        // メソッドインライン化されるか？
        return n * 2 + 1;
    }
}

// 54. JSON/XML処理: データ処理は適切か？
class DataSerializer {
    public String toJson(ProductReview5 product) {
        // 手動でJSON生成（ライブラリ使うべき？）
        return "{\"name\":\"" + product.getName() + "\",\"price\":" + product.getPrice() + "}";
    }
    
    public ProductReview5 fromJson(String json) {
        // パースエラー処理は？
        return null;
    }
}

class ProductReview5 {
    private String name;
    private double price;
    
    public String getName() { return name; }
    public double getPrice() { return price; }
}

// 60. 技術的負債: このコードベースの技術的負債は？
class LegacyCode {
    // TODO: リファクタリング必要
    public void complexMethod(int a, int b, int c, int d, int e) {
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (d > 0) {
                        if (e > 0) {
                            // 深いネスト
                        }
                    }
                }
            }
        }
    }
    
    // 重複コード
    public void method1() {
        System.out.println("Processing");
        // 処理
    }
    
    public void method2() {
        System.out.println("Processing");
        // 同じ処理
    }
}
