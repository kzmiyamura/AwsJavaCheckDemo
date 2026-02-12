package com.logistics.demo.review;

/**
 * Amazon Q レビュー3: フレームワーク
 * 
 * 検証項目:
 * - 32. Spring Framework
 * - 33. Spring Boot
 * - 34. JPA/Hibernate
 * - 35. Servlet/JSP
 * - 36. JAX-RS/JAX-WS
 * 
 * 質問: このコードのフレームワーク使用方法、設定に問題はありますか？
 * Spring、JPA、REST APIの設計について指摘してください。
 */

// 32-33. Spring Framework / Spring Boot
// @Service
class ProductServiceReview {
    // @Autowired
    private ProductRepositoryReview repository; // DIが適切か？
    
    // @Transactional
    public void createProduct(String name, double price) {
        // トランザクション境界は適切か？
        ProductReview product = new ProductReview(name, price);
        repository.save(product);
        // 外部API呼び出し
        notifyExternalSystem(product);
    }
    
    public ProductReview findById(Long id) {
        return repository.findById(id);
    }
    
    private void notifyExternalSystem(ProductReview product) {
        // 長時間処理がトランザクション内
    }
}

// 34. JPA/Hibernate
// @Entity
class ProductReview {
    // @Id
    // @GeneratedValue
    private Long id;
    private String name;
    private double price;
    
    // @OneToMany
    private java.util.List<OrderItemReview> orderItems; // Lazy loading設定は？
    
    public ProductReview(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
}

// @Entity
class OrderItemReview {
    // @Id
    private Long id;
    
    // @ManyToOne
    private ProductReview product; // N+1問題の可能性
}

// 34. JPA Repository
interface ProductRepositoryReview {
    void save(ProductReview product);
    ProductReview findById(Long id);
    // クエリメソッドの命名は適切か？
    java.util.List<ProductReview> findByNameAndPriceGreaterThan(String name, double price);
}

// 35-36. REST API
// @RestController
// @RequestMapping("/api/products")
class ProductControllerReview {
    private ProductServiceReview productService;
    
    // @GetMapping("/{id}")
    public ProductReview getProduct(Long id) {
        // エラーハンドリングは？
        return productService.findById(id);
    }
    
    // @PostMapping
    public void createProduct(ProductReview product) {
        // バリデーションは？
        // レスポンスステータスは？
        productService.createProduct(product.getName(), product.getPrice());
    }
}

// Spring設定クラス
// @Configuration
class AppConfig {
    // @Bean
    public ProductServiceReview productService() {
        // Bean定義は適切か？
        return new ProductServiceReview();
    }
}
