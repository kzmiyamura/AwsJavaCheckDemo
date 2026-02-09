# AwsJavaCheckDemo

## プロジェクト概要
物流（Logistics）における配送ルート・在庫最適化エンジンのデモプロジェクト。
生成AIが作成したJavaコードに対して、品質とセキュリティの観点から「自動ガードレール」を構築します。

## 構成

### 1. Bad Code（意図的な問題を含むコード）

- **InventoryManager.java**: HashMapをマルチスレッドで使用（スレッド競合）
- **RouteOptimizer.java**: 再帰呼び出しでStackOverflowErrorのリスク
- **S3ResultUploader.java**: S3Clientをクローズせずリソースリーク

### 2. Guardrail（ガードレール設定）

- **pom.xml**: Checkstyle/SpotBugsプラグイン設定
- **checkstyle.xml**: コーディング規約とスレッドセーフチェック

### 3. Infrastructure（インフラ定義）

- **template.yaml**: Lambda + S3のCloudFormation（セキュリティ問題を含む）

## 検証手順

### ビルド時チェック
```bash
mvn clean compile
```

### Checkovでインフラスキャン
```bash
pip install checkov
checkov -f infrastructure/template.yaml
```

### Amazon Qでコードレビュー
IDEでAmazon Q Code Reviewを実行

## 検出される問題

### Java
- スレッドセーフではないHashMapの使用
- リソースリークの可能性
- StackOverflowのリスク

### Infrastructure
- S3バケットの暗号化なし
- 過剰なIAM権限（s3:*）
- ハードコードされたAPI Key
