package com.logistics.demo.springboot.repository;

import com.logistics.demo.springboot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Phase 2: Spring Data JPA Repository 検証
 * 
 * 意図的な問題:
 * - N+1問題を引き起こすクエリ
 * - ネイティブクエリでSQLインジェクション
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // N+1問題: JOIN FETCH なし
    List<Product> findAll();
    
    // SQLインジェクション: ネイティブクエリで文字列連結
    @Query(value = "SELECT * FROM products WHERE name = ?1", nativeQuery = true)
    List<Product> findByNameUnsafe(String name);
    
    // クエリメソッド命名が長すぎる
    List<Product> findByNameAndPriceGreaterThanAndPriceLessThanOrderByNameAsc(
        String name, Double minPrice, Double maxPrice);
}
