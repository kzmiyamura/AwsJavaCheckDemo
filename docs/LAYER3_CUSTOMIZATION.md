# Layer 3: 業界・顧客別カスタマイズ

## 概要

Layer 3 では、お客様固有のコーディングポリシーを定義し、Amazon Q のコードレビューに適用します。

## ワークフロー

```
1. 質問リストを提供
   ↓
2. お客様が回答を記入
   ↓
3. customer-policy.yml として保存
   ↓
4. Amazon Q が自動的にポリシーを適用
   ↓
5. カスタマイズされたレビュー実施
```

---

## ステップ1: 質問リストの提供

お客様に以下のテンプレートを提供します：

📄 **[CUSTOMER_POLICY_TEMPLATE.md](../aws-java-checker/docs/CUSTOMER_POLICY_TEMPLATE.md)**

このテンプレートには以下の質問が含まれます：

- 業界分類（金融、医療、物流など）
- 取り扱うデータの種類（PII、PHI、機密情報など）
- セキュリティ要件（認証情報管理、暗号化）
- AWS リソース管理ポリシー
- 並行処理・スレッドセーフティ要件
- コーディング規約
- パフォーマンス要件
- 依存性注入（DI）ポリシー
- テストポリシー
- ログ・監視要件
- カスタムルール（業界固有）

---

## ステップ2: お客様による回答記入

お客様は以下のいずれかの方法で回答します：

### 方法A: Markdown テンプレートに直接記入

```bash
# テンプレートをコピー
cp docs/CUSTOMER_POLICY_TEMPLATE.md .amazonq/customer-policy-draft.md

# チェックボックスにチェックを入れる
# - [x] 金融（銀行・証券・保険）
# - [ ] 医療・ヘルスケア
# - [x] 物流・運輸
```

### 方法B: YAML形式で直接作成

```bash
# YAML形式で作成
vi .amazonq/customer-policy.yml
```

```yaml
industry: "logistics"
data_types:
  - "pii"
  - "confidential"
security:
  credentials:
    hardcoded: "forbidden"
    allowed_methods:
      - "secrets_manager"
# ... 以下続く
```

---

## ステップ3: 設定ファイルの配置

お客様が記入した設定を以下の場所に配置します：

```
AwsJavaCheckDemo/
└── .amazonq/
    ├── customer-policy.yml          # お客様ポリシー設定（YAML）
    └── rules/
        └── customer-coding-policy.md # Amazon Q 用ルール（自動生成）
```

### 配置方法

```bash
# プロジェクトルートで実行
cd AwsJavaCheckDemo

# .amazonq ディレクトリを作成
mkdir -p .amazonq/rules

# お客様が記入したファイルを配置
cp /path/to/filled-policy.yml .amazonq/customer-policy.yml
```

---

## ステップ4: Amazon Q ルールの自動適用

`.amazonq/rules/` に配置されたファイルは、Amazon Q のすべてのレビューで自動的に適用されます。

### 確認方法

```bash
# ファイルが正しく配置されているか確認
ls -la .amazonq/
ls -la .amazonq/rules/
```

出力例：
```
.amazonq/
├── customer-policy.yml
└── rules/
    └── customer-coding-policy.md
```

---

## ステップ5: カスタマイズされたレビュー実施

### Amazon Q でレビュー

```bash
# Amazon Q チャットで実行
/review @DynamoDbService.java
```

Amazon Q は以下を自動的に参照します：

1. `.amazonq/rules/customer-coding-policy.md` （お客様ポリシー）
2. `.amazonq/customer-policy.yml` （詳細設定）

### レビュー内容

お客様ポリシーに基づいて、以下がチェックされます：

#### 物流業界の例

- ✅ DynamoDbClient のリソースクローズ（try-with-resources 必須）
- ✅ Scan操作の制限（limit設定必須）
- ✅ BatchWriteItem の使用（複数アイテム操作時）
- ✅ consistentRead の設定（重要データ読み取り時）
- ✅ 配送ステータス遷移の妥当性
- ✅ 在庫数の非負整数チェック

---

## 設定例

### 物流業界の設定例

```yaml
# .amazonq/customer-policy.yml
industry: "logistics"
data_types:
  - "pii"
  - "confidential"

security:
  credentials:
    hardcoded: "forbidden"
    allowed_methods:
      - "secrets_manager"
      - "parameter_store"
  
  encryption:
    s3: "required_sse_kms"
    dynamodb: "required_aws_managed"

aws_resources:
  resource_close:
    sdk_clients: "try_with_resources_required"
    leak_tolerance: "forbidden"
  
  dynamodb:
    scan_operation: "limited"
    batch_processing: "required"
    consistent_read: "required_for_critical"

concurrency:
  collections:
    hashmap_in_multithread: "forbidden"

custom_rules:
  industry_specific:
    - name: "配送ステータス遷移チェック"
      severity: "error"
      pattern: "DeliveryStatus"
    
    - name: "在庫数は非負整数"
      severity: "error"
      pattern: "inventory.*set.*"

amazon_q_review:
  enabled: true
  additional_instructions: |
    - 物流業界のベストプラクティスに従っているか確認
    - 配送ルート最適化アルゴリズムの効率性を評価
    - マルチテナント対応が適切か確認
  
  focus_areas:
    - "thread_safety"
    - "resource_management"
    - "security"
    - "performance"
```

---

## 他業界の設定例

### 金融業界

```yaml
industry: "finance"
data_types:
  - "pci_dss"
  - "financial_data"

security:
  credentials:
    hardcoded: "forbidden"
  encryption:
    s3: "required_sse_kms"
    dynamodb: "required_customer_managed"

custom_rules:
  industry_specific:
    - name: "金額計算にBigDecimal必須"
      description: "float/double禁止"
      severity: "critical"
      pattern: "amount.*double|float"
    
    - name: "トランザクション整合性"
      description: "金融取引は ACID 特性必須"
      severity: "critical"
```

### 医療業界

```yaml
industry: "healthcare"
data_types:
  - "phi"
  - "hipaa"

security:
  credentials:
    hardcoded: "forbidden"
  encryption:
    s3: "required_sse_kms"
    dynamodb: "required_customer_managed"

custom_rules:
  industry_specific:
    - name: "患者ID暗号化"
      description: "患者IDは必ず暗号化"
      severity: "critical"
      pattern: "patientId"
    
    - name: "監査ログ必須"
      description: "すべての患者データアクセスをログ記録"
      severity: "critical"
```

---

## メリット

### 1. お客様固有の要件を反映
- 業界特有のルールを適用
- 社内コーディング規約を統合

### 2. 自動化
- `.amazonq/rules/` に配置するだけで自動適用
- 手動でルールを指定する必要なし

### 3. 一貫性
- すべてのレビューで同じポリシーを適用
- チーム全体で統一された基準

### 4. 柔軟性
- YAML形式で簡単にカスタマイズ
- 業界・プロジェクトごとに設定変更可能

---

## トラブルシューティング

### Q: Amazon Q がポリシーを認識しない

```bash
# ファイルパスを確認
ls -la .amazonq/rules/customer-coding-policy.md

# ファイル内容を確認
cat .amazonq/rules/customer-coding-policy.md
```

### Q: ポリシーを更新したい

```bash
# customer-policy.yml を編集
vi .amazonq/customer-policy.yml

# Amazon Q ルールも更新（必要に応じて）
vi .amazonq/rules/customer-coding-policy.md
```

### Q: 複数のポリシーを使い分けたい

```bash
# プロジェクトごとに異なる設定を配置
project-a/.amazonq/customer-policy.yml  # 物流用
project-b/.amazonq/customer-policy.yml  # 金融用
```

---

## まとめ

Layer 3 のカスタマイズにより、以下が実現できます：

1. ✅ お客様固有のコーディングポリシーを定義
2. ✅ 質問リストで簡単に要件収集
3. ✅ YAML形式で設定を管理
4. ✅ Amazon Q が自動的にポリシーを適用
5. ✅ 業界・顧客別のカスタマイズされたレビュー

これにより、**3層のガードレール**が完成します：

- **Layer 1**: Checkstyle/SpotBugs（静的解析）
- **Layer 2**: Amazon Q（AI レビュー）
- **Layer 3**: お客様ポリシー（業界・顧客別カスタマイズ）← 今ここ
