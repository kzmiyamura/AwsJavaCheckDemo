package com.logistics.demo.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

/**
 * Phase 3: AWS SDK - SQS 検証
 * 
 * 意図的な問題:
 * - リソースリーク
 * - メッセージ削除漏れ
 * - バッチ処理の非効率
 * - 可視性タイムアウト設定なし
 */
public class SqsService {
    
    // ❌ リソースリーク
    public void sendMessage(String queueUrl, String message) {
        SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .build();
        
        SendMessageRequest request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(message)
            // messageGroupId設定なし（FIFOキューの場合必要）
            .build();
        
        sqs.sendMessage(request);
        
        // ❌ sqs.close() がない
    }
    
    // ❌ メッセージ削除漏れ
    public void receiveMessage(String queueUrl) {
        SqsClient sqs = SqsClient.builder().build();
        
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            // visibilityTimeout設定なし
            .build();
        
        ReceiveMessageResponse response = sqs.receiveMessage(request);
        
        for (Message message : response.messages()) {
            // メッセージ処理
            System.out.println(message.body());
            
            // ❌ メッセージ削除なし（再処理される）
        }
    }
    
    // ❌ 非効率: ループ内でsendMessageを複数回呼び出し
    public void sendMultipleMessages(String queueUrl, List<String> messages) {
        SqsClient sqs = SqsClient.builder().build();
        
        for (String message : messages) {
            SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
            
            sqs.sendMessage(request); // 非効率（SendMessageBatchを使うべき）
        }
    }
    
    // ❌ デッドレターキュー設定なし
    public void createQueue(String queueName) {
        SqsClient sqs = SqsClient.builder().build();
        
        CreateQueueRequest request = CreateQueueRequest.builder()
            .queueName(queueName)
            // デッドレターキュー設定なし
            .build();
        
        sqs.createQueue(request);
    }
    
    // ❌ ロングポーリング設定なし
    public void receiveWithShortPolling(String queueUrl) {
        SqsClient sqs = SqsClient.builder().build();
        
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            // waitTimeSeconds設定なし（ショートポーリング）
            .build();
        
        sqs.receiveMessage(request);
    }
    
    // ❌ エラーハンドリングなし
    public void deleteMessage(String queueUrl, String receiptHandle) {
        SqsClient sqs = SqsClient.builder().build();
        
        DeleteMessageRequest request = DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(receiptHandle)
            .build();
        
        // エラーハンドリングなし
        sqs.deleteMessage(request);
    }
}
