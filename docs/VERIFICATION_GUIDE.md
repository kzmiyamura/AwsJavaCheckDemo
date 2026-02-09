# 検証ガイド

## 前提条件
- Docker Desktop がインストールされていること

## ステップ1: 環境起動

```bash
docker-compose up -d
```

## ステップ2: 普通にビルド（エラーなし確認）

```bash
docker-compose exec maven mvn clean compile -DskipTests -Dcheckstyle.skip=true -Dspotbugs.skip=true
```

✅ ビルドは成功するはずです（問題があるコードだが、コンパイルは通る）

## ステップ3: Checkstyleで検出

```bash
docker-compose exec maven mvn checkstyle:check
```

❌ スレッドセーフではないHashMapの使用が検出されます

## ステップ4: SpotBugsで検出

```bash
docker-compose exec maven mvn compile spotbugs:check
```

❌ リソースリークやスレッド競合の問題が検出されます

## ステップ5: Checkovでインフラスキャン

```bash
docker run --rm -v $(pwd):/app bridgecrew/checkov -f /app/infrastructure/template.yaml
```

❌ S3暗号化なし、過剰なIAM権限などが検出されます

## ステップ6: SonarQubeで総合分析

SonarQubeにアクセス: http://localhost:9000
初期パスワード: admin/admin

```bash
docker-compose exec maven mvn sonar:sonar \
  -Dsonar.host.url=http://sonarqube:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin
```

## 次のステップ
問題が検出されたら、コードを修正して再度検証します。
