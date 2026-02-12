package com.logistics.demo.springboot.service;

import com.logistics.demo.springboot.entity.Product;
import com.logistics.demo.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Phase 2: Spring Service 検証
 * 
 * 意図的な問題:
 * - トランザクション境界の問題
 * - フィールドインジェクション
 * - 外部API呼び出しがトランザクション内
 */
@Service
public class ProductService {
    
    // フィールドインジェクション（コンストラクタ推奨）
    @Autowired
    private ProductRepository productRepository;
    
    // トランザクション境界の問題: 外部API呼び出しを含む
    @Transactional
    public Product createProduct(String name, Double price) {
        Product product = new Product(name, price);
        productRepository.save(product);
        
        // 長時間処理がトランザクション内
        notifyExternalSystem(product);
        
        return product;
    }
    
    // トランザクションなし: 読み取り専用でも@Transactional(readOnly=true)推奨
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // N+1問題発生
    }
    
    // トランザクション伝播設定なし
    @Transactional
    public void updatePrice(Long id, Double newPrice) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setPrice(newPrice);
        // saveを呼ばなくてもDirty Checkingで更新されるが明示的でない
    }
    
    private void notifyExternalSystem(Product product) {
        // 外部API呼び出しのシミュレーション
        try {
            Thread.sleep(1000); // トランザクションが長時間保持される
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
