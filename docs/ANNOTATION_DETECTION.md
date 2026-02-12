# アノテーション問題検出機能

## 概要

aws-java-checker は、生成AIが起こしがちなアノテーション関連のミスを自動検出します。

## 検出される問題

### 1. @SuppressWarnings の誤用

#### ❌ BAD: 広すぎるスコープ
```java
@SuppressWarnings("all")  // すべての警告を抑制（危険！）
public List<String> getItems() {
    List items = new ArrayList();
    return items;
}
```

#### ✅ GOOD: 適切なスコープ
```java
@SuppressWarnings("rawtypes")  // 特定の警告のみ抑制
public List<String> getItems() {
    List items = new ArrayList();
    return items;
}
```

### 2. 不要な @SuppressWarnings

#### ❌ BAD: 警告が発生しないのに付けている
```java
@SuppressWarnings("unchecked")  // 不要！
public String getSimpleString() {
    return "Hello";
}
```

#### ✅ GOOD: 不要なアノテーションを削除
```java
public String getSimpleString() {
    return "Hello";
}
```

## 検証方法

### 1. アノテーション問題を含むコードでビルド

```bash
cd AwsJavaCheckDemo
mvn clean compile
```

**結果:**
```
[ERROR] src/main/java/com/logistics/demo/AnnotationIssues.java:[47,23] 
        (annotation) SuppressWarnings: この場所で、警告 'all' を抑制することはできません。
[ERROR] src/main/java/com/logistics/demo/AnnotationIssues.java:[55,23] 
        (annotation) SuppressWarnings: この場所で、警告 'unchecked' を抑制することはできません。

BUILD FAILURE
```

### 2. 問題を修正

`AnnotationIssues.java` を編集:

```java
// 修正前
@SuppressWarnings("all")
public List<String> getItems() { ... }

// 修正後
@SuppressWarnings("rawtypes")
public List<String> getItems() { ... }
```

```java
// 修正前
@SuppressWarnings("unchecked")
public String getSimpleString() { ... }

// 修正後
public String getSimpleString() { ... }
```

### 3. 再ビルド

```bash
mvn clean compile
```

**結果:**
```
BUILD SUCCESS (Checkstyle は通過、SpotBugs で他の問題が検出される)
```

## 重要なポイント

✨ **開発者は Checkstyle を意識する必要なし**

- 親 POM として `aws-java-checker-maven` を指定するだけ
- `mvn compile` するだけで自動的にチェックが実行される
- アノテーション問題が検出されると、ビルドが失敗する

## 設定ファイル

アノテーション検出は、以下の Checkstyle 設定で実現されています:

```xml
<!-- aws-java-checker/config/src/main/resources/aws-checker/checkstyle.xml -->
<module name="TreeWalker">
    <!-- @Override の付け忘れを検出 -->
    <module name="MissingOverride">
        <property name="severity" value="error"/>
        <property name="javaFiveCompatibility" value="false"/>
    </module>
    
    <!-- @SuppressWarnings の誤用を検出 -->
    <module name="SuppressWarnings">
        <property name="format" value="^all$|^unchecked$"/>
    </module>
</module>
```

## 生成AIが起こしがちなミス

1. **@SuppressWarnings("all")** - すべての警告を抑制してしまう
2. **@SuppressWarnings("unchecked")** - 不要な場所に付けてしまう
3. **@Override の付け忘れ** - メソッドのオーバーライドに付け忘れる
4. **@Deprecatedの説明不足** - 代替メソッドの説明がない

これらの問題は、aws-java-checker が自動的に検出します。
