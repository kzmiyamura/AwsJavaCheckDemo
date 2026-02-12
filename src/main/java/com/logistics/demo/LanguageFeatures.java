package com.logistics.demo;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

/**
 * 4. Stream API - 生成AIが起こしがちなミス
 */
class StreamIssues {
    // ❌ Raw typeとStream
    public void rawStream() {
        List items = new ArrayList();
        items.stream().filter(x -> x != null).count();
    }
    
    // ❌ 副作用のあるStream操作
    private int counter = 0;
    public void sideEffectStream(List<String> items) {
        items.stream().forEach(item -> counter++);
    }
}

/**
 * 5. リフレクション (Reflection) - 実行時型情報
 */
class ReflectionIssues {
    // ❌ セキュリティリスク: private フィールドへのアクセス
    public void accessPrivateField(Object obj) throws Exception {
        Field field = obj.getClass().getDeclaredField("password");
        field.setAccessible(true);
        String password = (String) field.get(obj);
    }
    
    // ❌ 例外処理が不適切
    public void unsafeReflection(String className) {
        try {
            Class.forName(className).newInstance();
        } catch (Exception e) {
            // 空catch
        }
    }
}

/**
 * 6. 列挙型 (Enum) - 定数定義
 */
enum Status {
    ACTIVE, INACTIVE;
    
    // ❌ enumにpublicフィールド（不変性違反）
    public String description;
}

/**
 * 7. レコード (Records) - Java 14+
 */
record UserRecord(String name, int age) {
    // ✅ レコードは自動的に不変
}

/**
 * 8. シールドクラス (Sealed Classes) - Java 17+
 */
sealed class Shape permits Circle, Rectangle {
}

final class Circle extends Shape {
    private double radius;
}

final class Rectangle extends Shape {
    private double width;
    private double height;
}

/**
 * 9. パターンマッチング (Pattern Matching)
 */
class PatternMatchingIssues {
    // ✅ Java 17+ のパターンマッチング
    public void checkType(Object obj) {
        if (obj instanceof String s) {
            System.out.println(s.toUpperCase());
        }
    }
}

/**
 * 10. 継承 (Inheritance)
 */
class InheritanceIssues {
    // ❌ @Override付け忘れ（1番で検出済み）
    public String toString() {
        return "InheritanceIssues";
    }
}
