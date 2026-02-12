package com.logistics.demo.springboot.controller;

import com.logistics.demo.springboot.entity.Product;
import com.logistics.demo.springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Phase 2: Spring Web (REST API) 検証
 * 
 * 意図的な問題:
 * - CORS設定が全開放
 * - エラーハンドリング不足
 * - バリデーション欠如
 * - HTTPステータスコード不適切
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // 全開放（セキュリティリスク）
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // エラーハンドリングなし
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getAllProducts().stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null); // nullを返す（404を返すべき）
    }
    
    // バリデーションなし
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        // 入力検証なし
        // HTTPステータス201を返すべき
        return productService.createProduct(product.getName(), product.getPrice());
    }
    
    // 全件取得（ページネーションなし）
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts(); // N+1問題
    }
    
    // DELETEなのにvoidを返す（204を返すべき）
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        // 実装なし
    }
    
    // パスパラメータでSQLインジェクション可能
    @GetMapping("/search")
    public List<Product> searchByName(@RequestParam String name) {
        // 入力検証なし
        return productService.getAllProducts().stream()
            .filter(p -> p.getName().contains(name))
            .toList();
    }
}
