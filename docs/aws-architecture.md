# AWS アーキテクチャ・設計

本ドキュメントは、Java アプリを ALB + EC2/Fargate + RDS で運用するクラシック構成の「AWS 構成図」に加え、コンポーネント設計・リクエストフロー・セキュリティ設計・デプロイ観点をまとめます。

---

## 1. AWS 構成図（インフラ）

```mermaid
graph TD
    %% --- スタイル定義 (AWS公式カラーに近い配色) ---
    classDef compute fill:#FF9900,stroke:#232F3E,color:white,stroke-width:0px;
    classDef database fill:#3B48CC,stroke:#232F3E,color:white,stroke-width:0px;
    classDef network fill:#8C4FFF,stroke:#232F3E,color:white,stroke-width:0px;
    classDef storage fill:#3F8624,stroke:#232F3E,color:white,stroke-width:0px;
    classDef security fill:#E05243,stroke:#232F3E,color:white,stroke-width:0px;
    classDef user fill:#ffffff,stroke:#232F3E,color:#232F3E,stroke-width:2px;
    
    %% --- ノード定義 ---
    User((Users)):::user
    
    subgraph AWS_Cloud [AWS Cloud]
        style AWS_Cloud fill:#f2f2f2,stroke:#232F3E,stroke-width:2px,stroke-dasharray: 5 5

        R53[Route 53<br/>DNS]:::network
        CF[CloudFront<br/>CDN]:::network
        S3[S3 Bucket<br/>Static Assets]:::storage
        IGW[Internet Gateway]:::network

        subgraph VPC ["VPC (10.0.0.0/16)"]
            style VPC fill:#ffffff,stroke:#232F3E,stroke-width:2px

            subgraph Public_AZ1 [Availability Zone A]
                style Public_AZ1 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5
                
                subgraph Pub_Sub1 [Public Subnet]
                    style Pub_Sub1 fill:none,stroke:none
                    ALB[ALB<br/>Load Balancer]:::network
                    NAT1[NAT Gateway]:::network
                end
            end
            
            subgraph Public_AZ2 [Availability Zone C]
                style Public_AZ2 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Pub_Sub2 [Public Subnet]
                    style Pub_Sub2 fill:none,stroke:none
                    ALB_Node2[ALB Node]:::network
                    NAT2[NAT Gateway]:::network
                end
            end

            subgraph App_AZ1 [Availability Zone A]
                style App_AZ1 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_App1 ["Private Subnet (App)"]
                    style Priv_App1 fill:none,stroke:none
                    JavaApp1[EC2 / Fargate<br/>Java App]:::compute
                end
            end

            subgraph App_AZ2 [Availability Zone C]
                style App_AZ2 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_App2 ["Private Subnet (App)"]
                    style Priv_App2 fill:none,stroke:none
                    JavaApp2[EC2 / Fargate<br/>Java App]:::compute
                end
            end

            subgraph DB_AZ1 [Availability Zone A]
                style DB_AZ1 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_DB1 ["Private Subnet (DB)"]
                    style Priv_DB1 fill:none,stroke:none
                    RDS_Master[(RDS Master<br/>MySQL/Postgre)]:::database
                    Redis_P[(ElastiCache<br/>Primary)]:::database
                end
            end

            subgraph DB_AZ2 [Availability Zone C]
                style DB_AZ2 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_DB2 ["Private Subnet (DB)"]
                    style Priv_DB2 fill:none,stroke:none
                    RDS_Standby[(RDS Standby)]:::database
                    Redis_R[(ElastiCache<br/>Replica)]:::database
                end
            end
        end
    end

    %% --- 接続線 ---
    User --> R53
    R53 --> CF
    CF --> ALB
    IGW <--> ALB
    
    ALB --> JavaApp1
    ALB --> JavaApp2 --x ALB_Node2
    
    JavaApp1 --> RDS_Master
    JavaApp2 --> RDS_Master
    JavaApp1 <--> Redis_P
    JavaApp2 <--> Redis_P
    
    RDS_Master -.->|Sync| RDS_Standby
    Redis_P -.->|Async| Redis_R
    
    JavaApp1 -.-> S3
    JavaApp1 -.-> NAT1
    JavaApp2 -.-> NAT2
    NAT1 -.-> IGW
    NAT2 -.-> IGW

    %% リンクを見やすくするための調整
    linkStyle default stroke:#333,stroke-width:1px;
```

---

## 2. コンポーネント構成（レイヤー）

アプリケーション側の責務をレイヤーで整理した構成です。

```mermaid
graph TB
    subgraph Client ["クライアント"]
        Browser[ブラウザ / アプリ]
    end

    subgraph Edge ["エッジ・入口"]
        R53_2["Route 53"]
        CF_2["CloudFront"]
        ALB_2["ALB"]
    end

    subgraph App ["Java アプリケーション"]
        Controller["Controller<br/>Spring MVC / 認証"]
        Service["Service<br/>ビジネスロジック"]
        Repository["Repository<br/>JPA / MyBatis"]
    end

    subgraph Data ["データ層"]
        RDS_2[(RDS)]
        Redis_2[(ElastiCache)]
        S3_2["S3"]
    end

    Browser --> R53_2 --> CF_2 --> ALB_2
    ALB_2 --> Controller --> Service --> Repository
    Repository --> RDS_2
    Service --> Redis_2
    Service --> S3_2
```

| レイヤー | 役割 |
|----------|------|
| エッジ・入口 | DNS・キャッシュ・負荷分散・TLS 終端 |
| Controller | HTTP リクエスト受付・入力検証・認可・レスポンス整形 |
| Service | トランザクション境界・キャッシュ利用・他サービス連携 |
| Repository | RDS への永続化・キャッシュ（Redis）の読み書き |
| データ層 | RDS（主データ）、ElastiCache（セッション/キャッシュ）、S3（静的・バッチ出力） |

---

## 3. リクエストフロー（シーケンス）

ユーザーリクエストが ALB → Java アプリ → RDS/Redis に至る流れです。

```mermaid
sequenceDiagram
    participant U as User
    participant CF as CloudFront
    participant ALB as ALB
    participant App as Java App
    participant Redis as ElastiCache
    participant RDS as RDS

    U->>CF: HTTPS
    CF->>ALB: キャッシュミス時
    ALB->>App: ルーティング
    App->>Redis: セッション / キャッシュ参照
    alt キャッシュヒット
        Redis-->>App: 返却
    else キャッシュミス
        App->>RDS: クエリ
        RDS-->>App: 結果
        App->>Redis: キャッシュ格納
    end
    App-->>ALB: レスポンス
    ALB-->>U: HTML / JSON
```

---

## 4. セキュリティ・ネットワーク設計

| 観点 | 設計方針 |
|------|----------|
| **入口** | WAF（必要に応じて）、ALB で TLS 終端、CloudFront で DDoS 軽減 |
| **ネットワーク** | アプリ・DB はプライベートサブネット、DB は 0.0.0.0 開放しない |
| **アウトバウンド** | NAT Gateway 経由で固定。必要なら VPC Endpoint で S3/DynamoDB 等を私的に利用 |
| **認証・認可** | アプリ内でセッション管理 or Cognito 等。IAM は EC2/Fargate のロールで最小権限 |
| **秘密情報** | Secrets Manager / Parameter Store で取得。コード・環境変数に平文で持たない |
| **ログ・監査** | ALB アクセスログ、CloudTrail、VPC フローログを有効化 |

---

## 5. デプロイ・運用の考え方

```mermaid
graph LR
    subgraph CI ["CI"]
        Code[コード]
        Build[ビルド]
        Test[テスト]
        Image[イメージ]
        Code --> Build --> Test --> Image
    end

    subgraph CD ["CD"]
        ECR[ECR]
        ECS[ECS / Fargate]
        EC2[EC2]
        Image --> ECR
        ECR --> ECS
        ECR -.->|AMI 等| EC2
    end

    subgraph Runtime ["ランタイム"]
        ECS --> App2[アプリ]
        EC2 --> App2
    end
```

| 項目 | 方針例 |
|------|--------|
| **ビルド** | Maven/Gradle で JAR 作成、Docker イメージ化して ECR へプッシュ |
| **デプロイ** | ECS なら Rolling / Blue-Green。EC2 なら AMI 更新 + オートスケールグループ |
| **設定** | 環境ごとに Parameter Store / Secrets Manager を参照。イメージは共通化 |
| **監視** | CloudWatch（メトリクス・ログ）、アラーム、ヘルスチェック（ALB ターゲット） |

---

## 6. 関連ドキュメント

- サーバーレス構成（Lambda + API Gateway + DynamoDB）: [aws-architecture-serverless.md](./aws-architecture-serverless.md)