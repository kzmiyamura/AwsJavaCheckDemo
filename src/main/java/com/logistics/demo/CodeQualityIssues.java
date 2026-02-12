package com.logistics.demo;

/**
 * コード品質問題を含むクラス - Checkstyleで検出可能
 */
public class CodeQualityIssues {
    
    // BAD: 定数名が小文字
    private static final int max_items = 100;
    
    // BAD: 変数名が1文字
    private int x;
    private int y;
    
    // BAD: マジックナンバー
    public double calculateDiscount(double price) {
        if (price > 10000) {
            return price * 0.15;
        } else if (price > 5000) {
            return price * 0.10;
        } else if (price > 1000) {
            return price * 0.05;
        }
        return 0;
    }
    
    // BAD: 複雑度が高い（ネストが深い）
    public String processOrder(int status, boolean isPremium, int quantity) {
        if (status == 1) {
            if (isPremium) {
                if (quantity > 10) {
                    if (quantity > 50) {
                        return "bulk-premium";
                    } else {
                        return "medium-premium";
                    }
                } else {
                    return "small-premium";
                }
            } else {
                if (quantity > 10) {
                    return "bulk-regular";
                } else {
                    return "small-regular";
                }
            }
        }
        return "unknown";
    }
    
    // BAD: メソッドが長すぎる
    public void longMethod() {
        System.out.println("Line 1");
        System.out.println("Line 2");
        System.out.println("Line 3");
        System.out.println("Line 4");
        System.out.println("Line 5");
        System.out.println("Line 6");
        System.out.println("Line 7");
        System.out.println("Line 8");
        System.out.println("Line 9");
        System.out.println("Line 10");
        System.out.println("Line 11");
        System.out.println("Line 12");
        System.out.println("Line 13");
        System.out.println("Line 14");
        System.out.println("Line 15");
        System.out.println("Line 16");
        System.out.println("Line 17");
        System.out.println("Line 18");
        System.out.println("Line 19");
        System.out.println("Line 20");
    }
    
    // BAD: パラメータが多すぎる
    public void createOrder(String id, String name, String address, String phone, 
                           String email, int quantity, double price, String status) {
        // 処理
    }
}
