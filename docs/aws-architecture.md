# AWS Architecture Diagram

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

        subgraph VPC [VPC (10.0.0.0/16)]
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

                subgraph Priv_App1 [Private Subnet (App)]
                    style Priv_App1 fill:none,stroke:none
                    JavaApp1[EC2 / Fargate<br/>Java App]:::compute
                end
            end

            subgraph App_AZ2 [Availability Zone C]
                style App_AZ2 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_App2 [Private Subnet (App)]
                    style Priv_App2 fill:none,stroke:none
                    JavaApp2[EC2 / Fargate<br/>Java App]:::compute
                end
            end

            subgraph DB_AZ1 [Availability Zone A]
                style DB_AZ1 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_DB1 [Private Subnet (DB)]
                    style Priv_DB1 fill:none,stroke:none
                    RDS_Master[(RDS Master<br/>MySQL/Postgre)]:::database
                    Redis_P[(ElastiCache<br/>Primary)]:::database
                end
            end

            subgraph DB_AZ2 [Availability Zone C]
                style DB_AZ2 fill:#e6f7ff,stroke:#0073bb,stroke-dasharray: 5 5

                subgraph Priv_DB2 [Private Subnet (DB)]
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