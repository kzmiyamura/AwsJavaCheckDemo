package com.logistics.demo;

import java.util.List;
import java.util.ArrayList;

/**
 * 生成AIが起こしがちなアノテーション関連のミス集
 * 
 * このファイルは、生成AIがコードを生成する際に起こしがちな
 * アノテーション関連のミスを示しています。
 * 
 * aws-java-checker の Checkstyle 設定により、これらの問題が
 * 自動的に検出され、ビルドが失敗します。
 */
public class AnnotationIssues {

    // ✅ 正しい: @Override を付けている
    @Override
    public String toString() {
        return "AnnotationIssues";
    }

    // ✅ 正しい: @Override を付けている
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    // ✅ 正しい: @Override を付けている
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // ❌ ミス: @Deprecatedを付けているが、Javadocに代替メソッドの説明がない
    @Deprecated
    public void oldMethod() {
        System.out.println("This is deprecated");
    }

    // ❌ ミス: 非推奨メソッドを呼び出している
    public void callDeprecatedMethod() {
        oldMethod();
    }

    // ❌ ミス: @SuppressWarnings("rawtypes") を使うべき（Checkstyleが検出）
    @SuppressWarnings("rawtypes")
    public List<String> getItems() {
        List items = new ArrayList(); // raw type
        items.add("item1");
        return items;
    }

    // ❌ ミス: 不要な@SuppressWarnings（削除）
    public String getSimpleString() {
        return "Hello";
    }

    // ❌ ミス: nullを返す可能性があるのに @Nullable アノテーションがない
    public String findItem(String id) {
        if (id == null) {
            return null;
        }
        return "item-" + id;
    }

    // ❌ ミス: nullチェックが必要なのに @NonNull アノテーションがない
    public int getLength(String text) {
        return text.length(); // NPE の可能性
    }

    // ✅ 正しい: インターフェースのメソッドに @Override を付けている
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Running");
        }
    }

    // ✅ 正しい: 抽象メソッドのオーバーライドに @Override を付けている
    static abstract class BaseProcessor {
        abstract void process();
    }

    static class ConcreteProcessor extends BaseProcessor {
        @Override
        void process() {
            System.out.println("Processing");
        }
    }
}
