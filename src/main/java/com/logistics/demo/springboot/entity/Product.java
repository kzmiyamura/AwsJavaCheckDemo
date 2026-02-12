package com.logistics.demo.springboot.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Phase 2: Spring Data JPA 検証
 * 
 * 意図的な問題:
 * - N+1問題（Lazy loading未設定）
 * - Bidirectional関係の設定ミス
 * - equals/hashCode未実装
 */
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price; // doubleではなくDouble（null許容）
    
    // N+1問題: fetch設定なし
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
    
    public Product() {}
    
    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }
    
    // equals/hashCode未実装（JPAエンティティで問題）
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
