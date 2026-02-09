package com.logistics.demo;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3結果アップローダー - S3Clientをクローズせずリソースリーク
 */
public class S3ResultUploader {
    private String bucketName;
    
    public S3ResultUploader(String bucketName) {
        this.bucketName = bucketName;
    }
    
    // BAD: S3Clientをクローズしない（リソースリーク）
    public void uploadResult(String key, String content) {
        S3Client s3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_1)
            .build();
        
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.putObject(request, RequestBody.fromString(content));
        
        // BAD: s3Client.close() を呼んでいない
    }
    
    // BAD: 複数回呼ばれるとS3Clientインスタンスが大量に作られる
    public void uploadMultipleResults(String[] keys, String[] contents) {
        for (int i = 0; i < keys.length; i++) {
            uploadResult(keys[i], contents[i]);
        }
    }
    
    // BAD: 例外発生時もクローズされない
    public void uploadWithoutErrorHandling(String key, String content) {
        S3Client s3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_1)
            .build();
        
        s3Client.putObject(
            PutObjectRequest.builder().bucket(bucketName).key(key).build(),
            RequestBody.fromString(content)
        );
    }
}
