# AWS Architecture Diagram（サーバーレス・Java Lambda）

API Gateway + Lambda（Java）+ DynamoDB のサーバーレス構成例。既存の [aws-architecture.md](./aws-architecture.md) は ALB + EC2/Fargate + RDS のクラシック構成です。

```mermaid
graph LR
    %% --- スタイル定義 (AWS公式カラーに近い配色) ---
    classDef compute fill:#FF9900,stroke:#232F3E,color:white,stroke-width:0px;
    classDef database fill:#3B48CC,stroke:#232F3E,color:white,stroke-width:0px;
    classDef network fill:#8C4FFF,stroke:#232F3E,color:white,stroke-width:0px;
    classDef storage fill:#3F8624,stroke:#232F3E,color:white,stroke-width:0px;
    classDef security fill:#E05243,stroke:#232F3E,color:white,stroke-width:0px;
    classDef user fill:#ffffff,stroke:#232F3E,color:#232F3E,stroke-width:2px;

    User((Users)):::user

    subgraph AWS ["AWS Cloud"]
        style AWS fill:#f2f2f2,stroke:#232F3E,stroke-width:2px,stroke-dasharray: 5 5

        R53["Route 53<br/>DNS"]:::network
        CF["CloudFront<br/>Optional"]:::network
        APIGW["API Gateway<br/>REST / HTTP API"]:::network
        Lambda["Lambda<br/>Java 17+"]:::compute
        DDB[(DynamoDB<br/>Table)]:::database
        S3["S3<br/>Assets / Export"]:::storage
        SQS["SQS<br/>Queue"]:::network
        EventBridge["EventBridge<br/>Events"]:::network
    end

    User --> R53
    R53 --> CF
    CF --> APIGW
    APIGW --> Lambda
    Lambda --> DDB
    Lambda --> S3
    Lambda --> SQS
    EventBridge --> Lambda

    linkStyle default stroke:#333,stroke-width:1px;
```

---

## 構成の特徴

| 項目 | 説明 |
|------|------|
| **API Gateway** | 認証・スロットリング・キャッシュ。Lambda を HTTP で公開。 |
| **Lambda (Java)** | ビジネスロジック。Corretto 17 等。Cold Start を考慮した設計。 |
| **DynamoDB** | サーバーレス向け NoSQL。オートスケール・従量課金。 |
| **SQS / EventBridge** | 非同期処理・イベント駆動。Lambda のキック元。 |

---

## 非同期フロー（オプション）

```mermaid
sequenceDiagram
    participant U as User
    participant API as API Gateway
    participant L as Lambda
    participant DDB as DynamoDB
    participant Q as SQS
    participant L2 as Lambda Worker

    U->>API: POST /orders
    API->>L: Invoke
    L->>DDB: PutItem
    L->>Q: SendMessage
    L-->>API: 202 Accepted
    API-->>U: Order ID

    Q->>L2: Poll
    L2->>DDB: UpdateItem
    L2->>L2: 通知・集計など
```

---

## 既存図との使い分け

| 構成図 | ファイル | 向いているケース |
|--------|----------|------------------|
| クラシック 3 層 | [aws-architecture.md](./aws-architecture.md) | 常時稼働・セッション・RDS/ElastiCache 利用 |
| サーバーレス | 本ファイル | スパイク対応・従量・API 中心・イベント駆動 |
