# Phase 3: AWS SDK 検証マトリックス

## 検証対象: 20項目

---

## 🔴 S3（5項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 1 | リソースリーク | S3Service.java | S3Client.close()なし |
| 2 | 認証情報ハードコード | S3Service.java | ACCESS_KEY, SECRET_KEY |
| 3 | リージョンハードコード | S3Service.java | Region.US_EAST_1 |
| 4 | 暗号化設定なし | S3Service.java | serverSideEncryption未設定 |
| 5 | 入力検証なし | S3Service.java | バケット名の検証なし |

---

## 🟠 DynamoDB（5項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 6 | リソースリーク | DynamoDbService.java | DynamoDbClient.close()なし |
| 7 | 非効率なバッチ処理 | DynamoDbService.java | ループ内でputItem |
| 8 | Scan使用 | DynamoDbService.java | Queryを使うべき |
| 9 | 条件付き書き込みなし | DynamoDbService.java | conditionExpression未設定 |
| 10 | 一貫性読み取り設定なし | DynamoDbService.java | consistentRead未設定 |

---

## 🟡 Lambda（5項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 11 | リソースリーク | LambdaService.java | LambdaClient.close()なし |
| 12 | エラーハンドリング不足 | LambdaService.java | 非同期呼び出しでエラーチェックなし |
| 13 | ペイロードサイズチェックなし | LambdaService.java | 6MB制限チェックなし |
| 14 | 関数存在確認なし | LambdaService.java | 関数の存在確認なし |
| 15 | リトライ設定なし | LambdaService.java | リトライポリシー未設定 |

---

## 🟢 SQS（5項目）

| # | 項目 | 問題コード | 期待される検出 |
|---|------|-----------|---------------|
| 16 | リソースリーク | SqsService.java | SqsClient.close()なし |
| 17 | メッセージ削除漏れ | SqsService.java | deleteMessage呼び出しなし |
| 18 | 非効率なバッチ処理 | SqsService.java | ループ内でsendMessage |
| 19 | ロングポーリング設定なし | SqsService.java | waitTimeSeconds未設定 |
| 20 | デッドレターキュー設定なし | SqsService.java | DLQ未設定 |

---

## 検出ツール別の期待値

### aws-java-checker（静的解析）

#### SpotBugs
- ✅ リソースリーク（S3Client, DynamoDbClient等）
- ✅ 認証情報ハードコード
- ✅ エラーハンドリング不足

#### FindSecurityBugs
- ✅ ハードコード認証情報
- ✅ 暗号化設定なし

**期待カバレッジ: 30-40%（6-8項目）**

---

### Amazon Q（コードレビュー）

#### 設計レビュー
- ⚠️ リージョンハードコード
- ⚠️ 非効率なバッチ処理
- ⚠️ Scan使用（Queryを使うべき）
- ⚠️ 条件付き書き込みなし
- ⚠️ ペイロードサイズチェックなし
- ⚠️ メッセージ削除漏れ
- ⚠️ ロングポーリング設定なし
- ⚠️ デッドレターキュー設定なし

**期待カバレッジ: 60-70%（12-14項目）**

---

## 総合カバレッジ目標

- **aws-java-checker**: 30-40%
- **Amazon Q**: 60-70%
- **合計**: 90-95%

---

## 次のステップ

1. ビルド実行（aws-java-checker）
2. 検出結果確認
3. Amazon Q レビュー実行
4. 検出率の測定
5. 最終マトリックス更新
