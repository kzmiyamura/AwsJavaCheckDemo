package com.logistics.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * セキュリティ問題を含むクラス - Checkstyle/SpotBugsで検出可能
 */
public class SecurityIssues {
    
    // BAD: ハードコードされた認証情報
    private static final String DB_PASSWORD = "MySecretPassword123";
    private static final String API_KEY = "sk-1234567890abcdef";
    
    // BAD: SQLインジェクションの脆弱性
    public ResultSet getUserData(String userId) throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mydb", 
            "root", 
            DB_PASSWORD
        );
        
        Statement stmt = conn.createStatement();
        // BAD: ユーザー入力を直接SQL文に連結
        String query = "SELECT * FROM users WHERE id = '" + userId + "'";
        return stmt.executeQuery(query);
    }
    
    // BAD: リソースリーク - Connectionをクローズしない
    public void executeQuery(String sql) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb");
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        // BAD: conn.close() と stmt.close() を呼んでいない
    }
    
    // BAD: 空のcatchブロック
    public void riskyOperation() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            // BAD: 例外を無視
        }
    }
    
    // BAD: nullチェックなし
    public int getLength(String str) {
        return str.length(); // NullPointerExceptionの可能性
    }
}
