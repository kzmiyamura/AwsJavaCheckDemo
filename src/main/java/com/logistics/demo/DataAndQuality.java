package com.logistics.demo;

import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.HashMap;

/**
 * 51. キャッシング (Caching)
 */
class CachingIssues {
    // ❌ メモリリーク: staticキャッシュが永遠に保持される
    private static Map<String, byte[]> cache = new HashMap<>();
    
    public void addToCache(String key, byte[] data) {
        cache.put(key, data); // クリアされない
    }
    
    // ❌ キャッシュサイズ制限なし
    private Map<String, Object> unboundedCache = new HashMap<>();
    
    public void cacheData(String key, Object value) {
        unboundedCache.put(key, value); // 無限に増える
    }
}

/**
 * 52. JITコンパイル
 * 静的解析では検出不可 - 実行時最適化
 */

/**
 * 53. シリアライゼーション (Serialization)
 */
class SerializationIssues implements Serializable {
    // ❌ serialVersionUID がない
    private String username;
    private transient String password;
    
    // ❌ Serializableだが適切に実装されていない
    private Object nonSerializableField = new Object();
}

/**
 * 54. JSON/XML処理
 * 静的解析では検出不可 - ライブラリ依存
 */

/**
 * 55. ストリーム処理
 * 4番（Stream API）で検出済み
 */

/**
 * 56. NIO (Non-blocking I/O)
 */
class NIOIssues {
    // ❌ FileChannelをクローズしない
    public void readFile(String path) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(path));
        // channel.close() がない
    }
    
    // ❌ Pathsの不適切な使用
    public void unsafePath(String userInput) {
        Path path = Paths.get("/data/" + userInput); // パストラバーサル
        // ファイル操作
    }
    
    // ❌ BufferedReaderをクローズしない
    public String readFileContent(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.readLine();
        // reader.close() がない
    }
}

/**
 * 57. 静的解析 (Static Analysis)
 * Checkstyleで検出済み
 */

/**
 * 58. コーディング規約
 * Checkstyleで検出済み
 */

/**
 * 59. リファクタリング
 * 静的解析では検出不可 - 設計判断が必要
 */
class RefactoringNeeded {
    // 複雑なメソッド（リファクタリング推奨だが、静的解析では判断困難）
    public void complexMethod(int x, int y, int z) {
        if (x > 0) {
            if (y > 0) {
                if (z > 0) {
                    // 深いネスト
                }
            }
        }
    }
}

/**
 * 60. 技術的負債管理
 * 静的解析では検出不可 - プロセス・組織の問題
 */
