# Phase 2: Spring Boot 検証マトリックス

## 検証対象: 22項目

---

## 🔴 Spring Boot Core（4項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 1 | 自動設定 | DemoApplication.java | @SpringBootApplication の適切性 |
| 2 | application.yml | application.yml | 認証情報ハードコード、本番設定 |
| 3 | プロファイル管理 | application.yml | プロファイル分離なし |
| 4 | 起動・シャットダウン | AwsConfig.java | リソースクローズ処理なし |

---

## 🟠 Spring Web（4項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 5 | @RestController | ProductController.java | エラーハンドリング不足 |
| 6 | エラーハンドリング | ProductController.java | nullを返す、404未実装 |
| 7 | CORS設定 | ProductController.java | @CrossOrigin(origins = "*") |
| 8 | バリデーション | ProductController.java | @Valid/@Validated なし |

---

## 🟡 Spring Data JPA（4項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 9 | @Repository | ProductRepository.java | SQLインジェクション |
| 10 | JPA設定 | application.yml | ddl-auto: create-drop |
| 11 | トランザクション | ProductService.java | 外部API呼び出しがトランザクション内 |
| 12 | N+1問題 | Product.java, ProductRepository.java | JOIN FETCH なし |

---

## 🟢 Spring Security（4項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 13 | 認証設定 | SecurityConfig.java | UserDetailsService なし |
| 14 | 認可設定 | SecurityConfig.java | permitAll()（全開放） |
| 15 | CSRF | SecurityConfig.java | csrf().disable() |
| 16 | セッション管理 | SecurityConfig.java | セッション設定なし |

---

## 🔵 Spring Actuator（3項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 17 | エンドポイント公開 | application.yml | include: "*" |
| 18 | ヘルスチェック | application.yml | show-details: always |
| 19 | メトリクス | application.yml | 機密情報漏洩リスク |

---

## 🟣 Spring Cloud AWS（3項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 20 | S3Client | AwsConfig.java | 認証情報ハードコード |
| 21 | リソースリーク | AwsConfig.java | @PreDestroy なし |
| 22 | リージョン設定 | AwsConfig.java | リージョンハードコード |

---

## 検出ツール別の期待値

### aws-java-checker（静的解析）

#### Checkstyle
- ✅ フィールドインジェクション（@Autowired）
- ✅ 命名規約違反

#### SpotBugs
- ✅ nullを返す
- ✅ リソースリーク
- ✅ Thread.sleep in transaction

#### FindSecurityBugs
- ✅ ハードコード認証情報（application.yml, AwsConfig.java）
- ✅ SQLインジェクション（ネイティブクエリ）
- ✅ CSRF無効化

**期待カバレッジ: 30-40%**

---

### Amazon Q（コードレビュー）

#### 設計レビュー
- ⚠️ N+1問題
- ⚠️ トランザクション境界
- ⚠️ CORS設定
- ⚠️ エラーハンドリング
- ⚠️ バリデーション欠如
- ⚠️ HTTPステータスコード
- ⚠️ Actuator設定

**期待カバレッジ: 60-70%**

---

## 総合カバレッジ目標

- **aws-java-checker**: 30-40%
- **Amazon Q**: 60-70%
- **合計**: 90-95%

---

## 次のステップ

1. ビルド実行
2. aws-java-checker の検出結果確認
3. Amazon Q レビュー実行
4. 検出率の測定
5. マトリックス更新
