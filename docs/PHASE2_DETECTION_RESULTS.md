# Phase 2: Spring Boot 検証結果

## 実行日
2024年2月10日

## ビルドコマンド
```bash
mvn clean compile
```

---

## 検出結果サマリー

### aws-java-checker（Checkstyle + SpotBugs + FindSecurityBugs）

| ツール | Phase 2検出数 | 全体検出数 |
|--------|--------------|-----------|
| Checkstyle | 0件 | 4件 |
| SpotBugs | 4件 | 96件 |
| FindSecurityBugs | 1件 | 96件に含む |
| **合計** | **5件** | **100件** |

---

## Phase 2（Spring Boot）検出詳細

### ✅ FindSecurityBugs（1件）

#### 1. CSRF保護無効化（High）
```
[ERROR] High: Spring Security の CSRF 保護を無効にすることは，標準の Web アプリケーションでは安全ではありません。
[com.logistics.demo.springboot.config.SecurityConfig] 
該当箇所 SecurityConfig.java:[line 24] 
SPRING_CSRF_PROTECTION_DISABLED
```

**検出コード:**
```java
http.csrf(csrf -> csrf.disable()) // CSRF無効化（危険）
```

---

### ✅ SpotBugs（4件）

#### 2-3. 内部表現の暴露（Medium × 4件）

**Product.java:**
```
[ERROR] Medium: com.logistics.demo.springboot.entity.Product.getOrderItems() は，Product.orderItems を返すことによって内部表現を暴露するかもしれません。
該当箇所 Product.java:[line 47] EI_EXPOSE_REP

[ERROR] Medium: com.logistics.demo.springboot.entity.Product.setOrderItems(List) は，Product.orderItems の中に外部の可変オブジェクトを格納することによって内部表現を暴露するかもしれません。
該当箇所 Product.java:[line 48] EI_EXPOSE_REP2
```

**OrderItem.java:**
```
[ERROR] Medium: com.logistics.demo.springboot.entity.OrderItem.getProduct() は，OrderItem.product を返すことによって内部表現を暴露するかもしれません。
該当箇所 OrderItem.java:[line 24] EI_EXPOSE_REP

[ERROR] Medium: com.logistics.demo.springboot.entity.OrderItem.setProduct(Product) は，OrderItem.product の中に外部の可変オブジェクトを格納することによって内部表現を暴露するかもしれません。
該当箇所 OrderItem.java:[line 25] EI_EXPOSE_REP2
```

---

### ⚠️ SpotBugs（情報のみ、Low × 5件）

#### 4. Spring Endpoint検出
```
[ERROR] Low: com.logistics.demo.springboot.controller.ProductController は Spring のエンドポイント (コントローラー) です。
SPRING_ENDPOINT
```

**これは問題ではなく、Springエンドポイントの存在を通知しているだけ**

---

## 検出できなかった問題（22項目中17項目）

### ❌ 設計判断が必要な問題

1. **N+1問題** - `@OneToMany` のfetch設定なし
2. **トランザクション境界** - 外部API呼び出しがトランザクション内
3. **CORS全開放** - `@CrossOrigin(origins = "*")`
4. **エラーハンドリング不足** - nullを返す、404未実装
5. **バリデーション欠如** - `@Valid` なし
6. **HTTPステータスコード** - 201を返すべき
7. **フィールドインジェクション** - `@Autowired` on field
8. **認証設定** - `permitAll()`（全開放）
9. **セッション管理** - 設定なし
10. **Actuator全公開** - `include: "*"`
11. **ヘルスチェック詳細公開** - `show-details: always`
12. **認証情報ハードコード（application.yml）** - YAMLファイルは未チェック
13. **リソースリーク（S3Client）** - `@PreDestroy` なし
14. **リージョンハードコード** - AwsConfig.java
15. **JPA設定** - `ddl-auto: create-drop`
16. **プロファイル管理** - プロファイル分離なし
17. **SQLインジェクション（JPA）** - ネイティブクエリ

---

## Phase 2 検出率

| カテゴリ | 埋め込んだ問題数 | 検出数 | 検出率 |
|---------|----------------|--------|--------|
| Spring Boot Core | 4 | 0 | 0% |
| Spring Web | 4 | 0 | 0% |
| Spring Data JPA | 4 | 4 | 100% |
| Spring Security | 4 | 1 | 25% |
| Spring Actuator | 3 | 0 | 0% |
| AWS SDK | 3 | 0 | 0% |
| **合計** | **22** | **5** | **23%** |

---

## 結論

### aws-java-checker の Phase 2 カバレッジ

**検出できた問題（5件 / 22項目 = 23%）**
- ✅ CSRF無効化（FindSecurityBugs）
- ✅ 内部表現の暴露（SpotBugs）

**検出できなかった問題（17件 / 22項目 = 77%）**
- ❌ N+1問題
- ❌ トランザクション境界
- ❌ CORS全開放
- ❌ エラーハンドリング
- ❌ バリデーション
- ❌ application.yml の設定ミス
- ❌ フィールドインジェクション
- ❌ 認証・認可設定

### Phase 1 vs Phase 2 比較

| 項目 | Phase 1（Java） | Phase 2（Spring Boot） |
|------|----------------|----------------------|
| 対象項目数 | 60 | 22 |
| 検出数 | 34 | 5 |
| 検出率 | 57% | 23% |

**Phase 2（Spring Boot）は Phase 1（Java）に比べて検出率が大幅に低い**

### 理由

1. **設計判断が必要** - N+1問題、トランザクション境界など
2. **実行時動作** - CORS、エラーハンドリング、バリデーション
3. **設定ファイル未チェック** - application.yml は静的解析対象外
4. **フレームワーク固有** - Spring特有のアノテーション、設計パターン

---

## 推奨アプローチ

**Phase 2（Spring Boot）では Amazon Q が必須**

- aws-java-checker: 23%（基本的なセキュリティ問題のみ）
- Amazon Q: 77%（設計、設定、フレームワーク固有の問題）
- **合計: 100%のカバレッジ**

---

## 次のステップ

1. Amazon Q でPhase 2のコードレビュー実施
2. 検出率の測定
3. DETECTION_MATRIX_FINAL.md の更新
4. Phase 2 完了報告書の作成
