package com.logistics.demo.aws;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

/**
 * Phase 3: AWS SDK - Lambda 検証
 * 
 * 意図的な問題:
 * - リソースリーク
 * - タイムアウト設定なし
 * - エラーハンドリング不足
 * - ペイロードサイズチェックなし
 */
public class LambdaService {
    
    // ❌ リソースリーク
    public void invokeFunction(String functionName, String payload) {
        LambdaClient lambda = LambdaClient.builder()
            .region(Region.US_EAST_1)
            .build();
        
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(SdkBytes.fromUtf8String(payload))
            // invocationType設定なし（デフォルトはRequestResponse）
            .build();
        
        // ❌ エラーハンドリングなし
        InvokeResponse response = lambda.invoke(request);
        
        // ❌ lambda.close() がない
    }
    
    // ❌ 非同期呼び出しでエラーチェックなし
    public void invokeAsync(String functionName, String payload) {
        LambdaClient lambda = LambdaClient.builder().build();
        
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(SdkBytes.fromUtf8String(payload))
            .invocationType(InvocationType.EVENT) // 非同期
            .build();
        
        lambda.invoke(request);
        // 非同期なのでエラーが返らない
    }
    
    // ❌ ペイロードサイズチェックなし（6MBまで）
    public void invokeLargePayload(String functionName, String largePayload) {
        LambdaClient lambda = LambdaClient.builder().build();
        
        // ペイロードサイズチェックなし
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(SdkBytes.fromUtf8String(largePayload))
            .build();
        
        lambda.invoke(request);
    }
    
    // ❌ Lambda関数の存在確認なし
    public void invokeFunctionWithoutCheck(String functionName) {
        LambdaClient lambda = LambdaClient.builder().build();
        
        // 関数の存在確認なし
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(SdkBytes.fromUtf8String("{}"))
            .build();
        
        lambda.invoke(request);
    }
    
    // ❌ リトライ設定なし
    public void invokeWithoutRetry(String functionName, String payload) {
        LambdaClient lambda = LambdaClient.builder()
            .region(Region.US_EAST_1)
            // リトライ設定なし
            .build();
        
        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .payload(SdkBytes.fromUtf8String(payload))
            .build();
        
        lambda.invoke(request);
    }
}
