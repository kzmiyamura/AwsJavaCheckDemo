package com.logistics.demo.aws;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

/**
 * Phase 3: AWS SDK - S3 検証
 * 
 * 意図的な問題:
 * - リソースリーク（S3Clientをクローズしない）
 * - 認証情報ハードコード
 * - リージョンハードコード
 * - エラーハンドリング不足
 * - リトライ設定なし
 */
public class S3Service {
    
    // ❌ 認証情報ハードコード
    private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // ❌ リージョンハードコード
    private static final Region REGION = Region.US_EAST_1;
    
    // ❌ リソースリーク: S3Clientをクローズしない
    public void uploadFile(String bucketName, String key, String content) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        
        S3Client s3Client = S3Client.builder()
            .region(REGION)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
        
        // ❌ エラーハンドリングなし
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.putObject(request, RequestBody.fromString(content));
        
        // ❌ s3Client.close() がない
    }
    
    // ❌ リソースリーク: 複数のS3Clientを作成
    public void downloadFile(String bucketName, String key) {
        S3Client s3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_1) // リージョンが異なる
            .build();
        
        // ❌ エラーハンドリングなし
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.getObject(request);
        
        // ❌ s3Client.close() がない
    }
    
    // ❌ バケット名をユーザー入力から直接使用（検証なし）
    public void createBucket(String bucketName) {
        S3Client s3Client = S3Client.builder().build();
        
        CreateBucketRequest request = CreateBucketRequest.builder()
            .bucket(bucketName) // 入力検証なし
            .build();
        
        s3Client.createBucket(request);
    }
    
    // ❌ 暗号化設定なし
    public void uploadSensitiveData(String bucketName, String key, String sensitiveData) {
        S3Client s3Client = S3Client.builder().build();
        
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            // serverSideEncryption設定なし
            .build();
        
        s3Client.putObject(request, RequestBody.fromString(sensitiveData));
    }
    
    // ❌ リトライ設定なし
    public void uploadWithoutRetry(String bucketName, String key, String content) {
        S3Client s3Client = S3Client.builder()
            .region(REGION)
            // リトライ設定なし
            .build();
        
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.putObject(request, RequestBody.fromString(content));
    }
}
