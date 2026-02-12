package com.logistics.demo.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Phase 2: Spring Cloud AWS 検証
 * 
 * 意図的な問題:
 * - 認証情報ハードコード
 * - リソースリーク（クローズ処理なし）
 * - リージョン設定の問題
 */
@Configuration
public class AwsConfig {
    
    @Bean
    public S3Client s3Client() {
        // 認証情報ハードコード（危険）
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
        );
        
        // リソースリーク: @PreDestroyでクローズ処理なし
        return S3Client.builder()
            .region(Region.US_EAST_1) // リージョンハードコード
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }
    
    // DynamoDBClient, SQSClient等も同様の問題
}
