# コードレビューチェックリスト

## マルチスレッド・並行処理チェック

### 1. 共有変数の確認
- [ ] クラスのフィールド変数（インスタンス変数）はあるか？
- [ ] static変数はあるか？
- [ ] 複数のスレッドから同じオブジェクトにアクセスする可能性はあるか？

**チェックポイント**:
```java
// ❌ 危険: 共有変数
public class Service {
    private Map<String, Integer> cache = new HashMap<>();  // 複数スレッドからアクセス
}

// ✓ 安全: ローカル変数
public void method() {
    Map<String, Integer> cache = new HashMap<>();  // メソッド内のみ
}
```

### 2. スレッドセーフなコレクションの使用
- [ ] HashMap → ConcurrentHashMap に変更したか？
- [ ] ArrayList → CopyOnWriteArrayList または Collections.synchronizedList() に変更したか？
- [ ] HashSet → ConcurrentHashMap.newKeySet() に変更したか？

**推奨パターン**:
```java
// ❌ スレッドセーフではない
private Map<String, Integer> data = new HashMap<>();
private List<String> items = new ArrayList<>();
private Set<String> ids = new HashSet<>();

// ✓ スレッドセーフ
private Map<String, Integer> data = new ConcurrentHashMap<>();
private List<String> items = new CopyOnWriteArrayList<>();
private Set<String> ids = ConcurrentHashMap.newKeySet();
```

### 3. スレッド作成箇所の確認
- [ ] `new Thread()` を使っている箇所はあるか？
- [ ] `ExecutorService` を使っている箇所はあるか？
- [ ] `@Async` アノテーションを使っている箇所はあるか？
- [ ] `CompletableFuture.supplyAsync()` を使っている箇所はあるか？

**チェック対象**:
```java
// スレッドを作成するパターン
new Thread(() -> { ... }).start();
Executors.newFixedThreadPool(10);
CompletableFuture.supplyAsync(() -> { ... });
@Async public void asyncMethod() { ... }
```

### 4. 排他制御の実装
- [ ] 共有変数へのアクセスに `synchronized` を使っているか？
- [ ] または `Lock` を使っているか？
- [ ] read-modify-write操作（読んで→計算して→書く）は保護されているか？

**推奨パターン**:
```java
// ✓ synchronizedで保護
public synchronized void updateData(String key, int value) {
    Integer current = data.get(key);
    data.put(key, current + value);
}

// ✓ Lockで保護
private final Lock lock = new ReentrantLock();
public void updateData(String key, int value) {
    lock.lock();
    try {
        Integer current = data.get(key);
        data.put(key, current + value);
    } finally {
        lock.unlock();
    }
}
```

---

## リソース管理チェック

### 1. リソースのクローズ確認
- [ ] ファイル、ストリーム、接続をクローズしているか？
- [ ] `try-with-resources` を使っているか？
- [ ] AWS SDK クライアント（S3Client等）をクローズしているか？

**チェックポイント**:
```java
// ❌ クローズ忘れ
public void upload(String key, String content) {
    S3Client s3Client = S3Client.builder().build();
    s3Client.putObject(...);
    // クローズしていない！
}

// ✓ try-with-resources
public void upload(String key, String content) {
    try (S3Client s3Client = S3Client.builder().build()) {
        s3Client.putObject(...);
    }  // 自動でクローズ
}
```

### 2. 例外発生時のリソース解放
- [ ] 例外が発生してもリソースは解放されるか？
- [ ] `finally` ブロックまたは `try-with-resources` を使っているか？

---

## JavaScript経験者向けの注意点

### Javaとの違い

| 項目 | JavaScript/Node.js | Java |
|------|-------------------|------|
| 非同期の仕組み | イベントループ（シングルスレッド） | マルチスレッド |
| 排他制御 | 基本不要 | **必須** |
| リソース管理 | GCが自動処理 | **明示的にクローズ必要** |

### よくある間違い

```java
// ❌ JS脳: 「非同期だから大丈夫」
CompletableFuture.supplyAsync(() -> {
    Integer current = sharedMap.get(key);  // 排他制御なし
    sharedMap.put(key, current + 1);       // 競合状態！
});

// ✓ Java: 排他制御が必要
CompletableFuture.supplyAsync(() -> {
    synchronized(lock) {
        Integer current = sharedMap.get(key);
        sharedMap.put(key, current + 1);
    }
});
```

---

## 自動チェックツール

### ビルド時チェック
- **Checkstyle**: コーディング規約違反
- **SpotBugs + Find Security Bugs**: バグパターン、スレッドセーフ問題
- **Amazon Q Code Review**: AI による総合分析

### 実行コマンド
```bash
# ビルド時に自動チェック
mvn clean compile

# SpotBugsレポート生成
mvn spotbugs:spotbugs
mvn spotbugs:gui  # GUI表示
```

---

## レビュー時の質問例

1. **このクラスは複数のスレッドから使われますか？**
2. **この変数は複数のリクエストで共有されますか？**
3. **このリソースは必ずクローズされますか？**
4. **例外が発生した場合、リソースは解放されますか？**
