package com.logistics.demo.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Phase 3: AWS SDK - DynamoDB 検証
 * 
 * 意図的な問題:
 * - リソースリーク
 * - パーティションキー設計の問題
 * - エラーハンドリング不足
 * - バッチ処理の非効率
 */
public class DynamoDbService {
    
    // ❌ リソースリーク: DynamoDbClientをクローズしない
    public void putItem(String tableName, String id, String data) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();
        
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
        item.put("data", AttributeValue.builder().s(data).build());
        
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();
        
        // ❌ エラーハンドリングなし
        dynamoDb.putItem(request);
        
        // ❌ dynamoDb.close() がない
    }
    
    // ❌ 非効率: ループ内でputItemを複数回呼び出し（BatchWriteItemを使うべき）
    public void putMultipleItems(String tableName, Map<String, String> items) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder().build();
        
        for (Map.Entry<String, String> entry : items.entrySet()) {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(entry.getKey()).build());
            item.put("data", AttributeValue.builder().s(entry.getValue()).build());
            
            PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
            
            dynamoDb.putItem(request); // 非効率
        }
    }
    
    // ❌ 条件付き書き込みなし（競合の可能性）
    public void updateItem(String tableName, String id, String newData) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder().build();
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());
        
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("data", AttributeValueUpdate.builder()
            .value(AttributeValue.builder().s(newData).build())
            .action(AttributeAction.PUT)
            .build());
        
        UpdateItemRequest request = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .attributeUpdates(updates)
            // conditionExpression なし
            .build();
        
        dynamoDb.updateItem(request);
    }
    
    // ❌ Scanを使用（Queryを使うべき）
    public void scanTable(String tableName) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder().build();
        
        ScanRequest request = ScanRequest.builder()
            .tableName(tableName)
            // limit設定なし（全件取得）
            .build();
        
        ScanResponse response = dynamoDb.scan(request);
        // 大量データの場合、メモリ不足の可能性
    }
    
    // ❌ 一貫性読み取り設定なし
    public void getItem(String tableName, String id) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder().build();
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());
        
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            // consistentRead(true) なし
            .build();
        
        dynamoDb.getItem(request);
    }
}
