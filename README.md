# AwsJavaCheckDemo

## プロジェクト概要
物流（Logistics）における配送ルート・在庫最適化エンジンのデモプロジェクト。
生成AIが作成したJavaコードに対して、品質とセキュリティの観点から「自動ガードレール」を構築します。

## 特徴

✨ **Checkstyle/SpotBugs を意識する必要なし**

このプロジェクトは `aws-java-checker-maven` を親 POM として使用しているため、
**何も設定しなくても自動的にコード品質チェックが実行されます**。

```xml
<parent>
    <groupId>com.kzmiyamura</groupId>
    <artifactId>aws-java-checker-maven</artifactId>
    <version>1.0.0</version>
</parent>
```

これだけで、以下が自動的に適用されます：
- ✅ Checkstyle によるコーディング規約チェック
- ✅ SpotBugs によるバグパターン検出
- ✅ FindSecurityBugs によるセキュリティチェック

## ビルド方法

```bash
mvn clean compile
```

**たったこれだけ！** Checkstyle/SpotBugs の設定は一切不要です。

## 構成

### pom.xml

```xml
<parent>
    <groupId>com.kzmiyamura</groupId>
    <artifactId>aws-java-checker-maven</artifactId>
    <version>1.0.0</version>
</parent>
```

親 POM として `aws-java-checker-maven` を指定するだけで、
以下のプラグインが自動的に継承されます：

- **Checkstyle**: コーディング規約チェック
- **SpotBugs**: バグパターン検出
- **FindSecurityBugs**: セキュリティ脆弱性検出

### コード（意図的な問題を含む）

- **AnnotationIssues.java**: @SuppressWarningsの誤用
- **GenericsIssues.java**: Raw typeの使用
- **LambdaIssues.java**: Raw typeとラムダ式
- **IntegratedIssues.java**: リフレクション、例外処理
- **OOPAndConcurrency.java**: OOP、並行処理
- **ArchitectureAndPerformance.java**: アーキテクチャ、パフォーマンス
- **DataAndQuality.java**: キャッシング、シリアライゼーション、NIO
- **SecurityIssues.java**: SQLインジェクション、認証情報
- **CodeQualityIssues.java**: 命名規約、マジックナンバー
- **InventoryManager.java**: HashMapをマルチスレッドで使用
- **S3ResultUploader.java**: S3Clientをクローズせずリソースリーク

## 検証手順

### 1. aws-java-checker をインストール

```bash
cd ../aws-java-checker/maven
mvn clean install
```

### 2. ビルド時チェック

```bash
cd AwsJavaCheckDemo
mvn clean compile
```

→ Checkstyle が **2件のアノテーション問題**を検出し、ビルドが失敗します。

```
[ERROR] src/main/java/com/logistics/demo/AnnotationIssues.java:[47,23] (annotation) SuppressWarnings: この場所で、警告 'all' を抑制することはできません。
[ERROR] src/main/java/com/logistics/demo/AnnotationIssues.java:[55,23] (annotation) SuppressWarnings: この場所で、警告 'unchecked' を抑制することはできません。
```

### 3. アノテーション問題を修正してビルド成功

```bash
# AnnotationIssues.java の @SuppressWarnings を修正
# - @SuppressWarnings("all") → @SuppressWarnings("rawtypes")
# - 不要な @SuppressWarnings("unchecked") を削除

mvn clean compile
```

→ SpotBugs が **16件の問題を検出**し、ビルドが失敗します（他のファイルの問題）。

## 検出される問題

### 静的解析で検出可能（34項目 / 57%）

#### Compiler警告（-Werror）
- ✅ ジェネリクス: Raw type使用
- ✅ ラムダ式: Raw typeとラムダ
- ✅ Stream API: Raw typeとStream

#### Checkstyle
- ✅ アノテーション: @Override付け忘れ、@SuppressWarnings誤用
- ✅ マルチスレッド: HashMap使用
- ✅ コーディング規約: 命名規約、未使用インポート

#### SpotBugs
- ✅ リフレクション: setAccessible呼び出し
- ✅ カプセル化: publicフィールド
- ✅ 例外処理: 空catch、例外無視
- ✅ リソース管理: try-with-resources未使用
- ✅ 同期化: 同期化漏れ
- ✅ Executor Framework: ExecutorService未クローズ
- ✅ DI: ハードコード依存関係
- ✅ デザインパターン: スレッドセーフではないSingleton
- ✅ パフォーマンス: 非効率なString連結
- ✅ 最適化: 不要なオブジェクト生成
- ✅ キャッシング: staticキャッシュ（メモリリーク）
- ✅ シリアライゼーション: serialVersionUID欠如
- ✅ NIO: ストリーム未クローズ

#### FindSecurityBugs
- ✅ SQLインジェクション
- ✅ ハードコード認証情報
- ✅ パストラバーサル
- ✅ リソースリーク

### Amazon Qで補完（26項目 / 43%）
- ⚠️ 複雑なスレッド競合
- ⚠️ アーキテクチャ設計
- ⚠️ ビジネスロジック妥当性
- ⚠️ フレームワーク統合

詳細は [DETECTION_MATRIX_FINAL.md](docs/DETECTION_MATRIX_FINAL.md) を参照。

## まとめ

このプロジェクトは、**親 POM を追加するだけ**で、
自動的にコード品質チェックが有効になることを示しています。

開発者は Checkstyle/SpotBugs を意識する必要がなく、
`mvn compile` するだけで自動的にガードレールが機能します。
