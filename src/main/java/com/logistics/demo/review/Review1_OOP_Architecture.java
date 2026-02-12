package com.logistics.demo.review;

/**
 * Amazon Q レビュー1: OOP + アーキテクチャ
 * 
 * 検証項目:
 * - 10. 継承 (Inheritance)
 * - 11. インターフェース (Interfaces)
 * - 12. ポリモーフィズム (Polymorphism)
 * - 27. AOP (Aspect-Oriented Programming)
 * - 29. IoC (Inversion of Control)
 * - 31. MVC/MVVM
 * 
 * 質問: このコードの設計に問題はありますか？
 * OOP原則、アーキテクチャパターン、保守性の観点からレビューしてください。
 */

// 10. 継承: 継承階層が適切か？
class Vehicle {
    protected String brand;
    
    public void start() {
        System.out.println("Vehicle starting");
    }
}

class Car extends Vehicle {
    private int doors;
    
    @Override
    public void start() {
        System.out.println("Car starting");
    }
}

// 11. インターフェース: インターフェース設計が適切か？
interface PaymentProcessor {
    void processPayment(double amount);
    void refund(double amount);
}

class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        // 実装
    }
    
    @Override
    public void refund(double amount) {
        // 実装
    }
}

// 12. ポリモーフィズム: ポリモーフィズムの使い方は適切か？
class OrderService {
    public void processOrder(PaymentProcessor processor, double amount) {
        processor.processPayment(amount);
    }
}

// 27. AOP: 横断的関心事の分離は適切か？
class UserService {
    public void createUser(String username) {
        // ロギング、トランザクション、セキュリティチェックが混在
        System.out.println("LOG: Creating user");
        // セキュリティチェック
        if (username == null) throw new IllegalArgumentException();
        // ビジネスロジック
        System.out.println("User created: " + username);
        // トランザクションコミット
    }
}

// 29. IoC: 依存関係の制御が適切か？
class OrderController {
    private OrderService orderService = new OrderService(); // ハードコード
    
    public void placeOrder() {
        orderService.processOrder(new CreditCardProcessor(), 100.0);
    }
}

// 31. MVC: レイヤー分離が適切か？
class ProductController {
    public void displayProduct(int id) {
        // コントローラーにビジネスロジックとビュー処理が混在
        String productName = "Product-" + id;
        double price = id * 100.0;
        System.out.println("Product: " + productName + ", Price: " + price);
    }
}
