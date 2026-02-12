# Amazon Q カスタムレビュールール
# このファイルは .amazonq/rules/ に配置され、すべてのレビューで自動的に適用されます

## お客様ポリシーの適用

このプロジェクトでは、`.amazonq/customer-policy.yml` に定義されたお客様固有のコーディングポリシーを適用します。

### 業界: 物流（Logistics）

以下の観点で特に注意してコードレビューを実施してください：

---

## 1. セキュリティ要件

### 認証情報管理
- ❌ **ハードコード認証情報は絶対禁止**
- ✅ AWS Secrets Manager または Parameter Store を使用すること
- ✅ 環境変数は開発環境のみ許可

### 暗号化
- ✅ S3バケットは SSE-KMS で暗号化必須
- ✅ DynamoDB テーブルは AWS管理キーで暗号化必須

### SQLインジェクション対策
- ✅ PreparedStatement の使用必須
- ✅ JPA/MyBatis などのORMフレームワーク推奨

---

## 2. AWS リソース管理

### リソースクローズ
- ✅ AWS SDK クライアント（S3Client, DynamoDbClient等）は try-with-resources 必須
- ❌ リソースリークは絶対禁止（ビルド失敗）

例:
```java
// ❌ BAD
DynamoDbClient client = DynamoDbClient.builder().build();
client.putItem(...);
// クローズ漏れ

// ✅ GOOD
try (DynamoDbClient client = DynamoDbClient.builder().build()) {
    client.putItem(...);
}
```

### DynamoDB 使用ポリシー
- ⚠️ Scan操作は制限付き（limit設定必須）
- ✅ 複数アイテム操作時は BatchWriteItem 必須
- ✅ 重要データの読み取りは consistentRead(true) 必須

---

## 3. 並行処理・スレッドセーフティ

### コレクション
- ❌ マルチスレッド環境での HashMap 使用禁止
- ✅ ConcurrentHashMap または CopyOnWriteArrayList を使用

### Executor Framework
- ✅ ExecutorService は shutdown()/shutdownNow() 必須

---

## 4. コーディング規約

### 命名規約
- ✅ 定数は UPPER_SNAKE_CASE 必須
- ❌ マジックナンバー禁止（定数化必須）

### 例外処理
- ❌ 空の catch ブロック絶対禁止
- ✅ 適切なログ出力またはコメント必須

---

## 5. パフォーマンス

### String操作
- ✅ ループ内での String 連結は StringBuilder 必須

### オブジェクト生成
- ✅ 不要なオブジェクト生成を最小化

---

## 6. 依存性注入（DI）

- ✅ Spring Framework を使用
- ❌ ハードコード依存関係禁止（DTO等の単純オブジェクトを除く）

---

## 7. 物流業界固有ルール

### 配送ステータス遷移
- ✅ 配送ステータスは定義された順序でのみ遷移可能
- 例: PENDING → PROCESSING → SHIPPED → DELIVERED

### 在庫管理
- ✅ 在庫数は非負整数（マイナス値禁止）
- ✅ 在庫更新は楽観的ロックまたは条件付き書き込み必須

---

## 8. レビュー重点項目

コードレビュー時は以下を特に重点的にチェック：

1. **スレッドセーフティ**: マルチスレッド環境での安全性
2. **リソース管理**: AWS SDK クライアントの適切なクローズ
3. **セキュリティ**: 認証情報、暗号化、SQLインジェクション
4. **パフォーマンス**: 非効率な処理、不要なオブジェクト生成

---

## 9. 追加指示

- 物流業界のベストプラクティスに従っているか確認
- 配送ルート最適化アルゴリズムの効率性を評価
- マルチテナント対応が適切か確認

---

## 使用方法

このルールは Amazon Q のすべてのコードレビュー（`/review` コマンド）で自動的に適用されます。

追加の質問や確認が必要な場合は、`.amazonq/customer-policy.yml` を参照してください。
