package com.logistics.demo;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 生成AIが起こしがちなラムダ式関連のミス
 */
public class LambdaIssues {

    // ❌ ミス1: Raw typeとラムダ式の組み合わせ
    public void processItems() {
        List items = new ArrayList();
        items.add("item1");
        items.add("item2");
        
        items.forEach(item -> {
            System.out.println(item);
        });
    }
    
    // ❌ ミス2: 型推論できない複雑なラムダ
    public void complexLambda() {
        List data = new ArrayList();
        data.stream()
            .filter(item -> item != null)
            .map(item -> item.toString())
            .collect(Collectors.toList());
    }
    
    // ❌ ミス3: 例外処理が不適切なラムダ
    public void unsafeOperation(List<String> items) {
        items.forEach(item -> {
            Integer.parseInt(item); // NumberFormatException の可能性
        });
    }
    
    // ❌ ミス4: 副作用のあるラムダ（外部変数の変更）
    private int counter = 0;
    
    public void sideEffectLambda(List<String> items) {
        items.forEach(item -> {
            counter++; // 副作用
            System.out.println(item);
        });
    }
    
    // ✅ 正しい例
    public void correctLambda(List<String> items) {
        items.forEach(item -> System.out.println(item));
        
        long count = items.stream()
            .filter(item -> item != null)
            .count();
    }
}
