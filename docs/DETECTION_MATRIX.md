# Java概念の検出可能性マトリックス（完全版）

## 🟢 完全検出可能（静的解析）

| # | 概念 | 検出ツール | 設定 | 検出内容 |
|---|------|-----------|------|----------|
| 1 | アノテーション | Checkstyle | MissingOverride, SuppressWarnings | @Override付け忘れ、@SuppressWarnings誤用 |
| 2 | ジェネリクス | Compiler | -Xlint:rawtypes, -Xlint:unchecked | Raw type使用、型パラメータ欠如 |
| 3 | ラムダ式 | Compiler | 2番と同じ | Raw typeとラムダの組み合わせ |
| 4 | Stream API | Compiler | 2番と同じ | Raw typeとStreamの組み合わせ |
| 13 | カプセル化 | SpotBugs | デフォルト | publicフィールド、未使用フィールド |
| 18 | Executor Framework | SpotBugs | デフォルト | ExecutorService未クローズ |
| 24 | 例外処理 | SpotBugs | デフォルト | 空catch、例外無視 |
| 25 | try-with-resources | SpotBugs | デフォルト | リソースクローズ漏れ |
| 28 | DI | SpotBugs | デフォルト | ハードコードされた依存関係 |
| 30 | デザインパターン | SpotBugs | デフォルト | スレッドセーフではないSingleton |
| 45 | 認証・認可 | FindSecurityBugs | デフォルト | ハードコード認証情報 |
| 46 | 暗号化 | FindSecurityBugs | デフォルト | 安全でない暗号化 |
| 48 | OWASP対策 | FindSecurityBugs | デフォルト | SQLインジェクション |
| 49 | プロファイリング | SpotBugs | デフォルト | 非効率なString連結 |
| 50 | 最適化 | SpotBugs | デフォルト | 不要なオブジェクト生成 |
| 57 | 静的解析 | Checkstyle | デフォルト | 命名規約、マジックナンバー |
| 58 | コーディング規約 | Checkstyle | デフォルト | 未使用インポート |

---

## 🟡 部分検出可能（限界あり）

| # | 概念 | 検出ツール | 検出内容 | 限界 |
|---|------|-----------|----------|------|
| 5 | リフレクション | SpotBugs | setAccessible呼び出し | セキュリティリスクの完全評価は不可 |
| 15 | マルチスレッド | Checkstyle | HashMap使用 | 複雑な競合状態は検出不可 |
| 16 | 同期化 | SpotBugs | 同期化漏れ | デッドロックは検出不可 |
| 22 | 参照型 | SpotBugs | staticフィールド | 循環参照は検出困難 |
| 23 | メモリリーク | SpotBugs | 明らかなリーク | 複雑なリークは検出不可 |
| 26 | チェック例外 | SpotBugs | 広すぎるcatch | 適切な例外設計は判断不可 |

---

## 🔴 検出不可能（Amazon Q / 動的解析が必要）

| # | 概念 | 理由 | 推奨アプローチ |
|---|------|------|----------------|
| 6 | 列挙型 | 設計判断必要 | Amazon Q レビュー |
| 7 | レコード | Java 14+、設計判断 | Amazon Q レビュー |
| 8 | シールドクラス | Java 17+、設計判断 | Amazon Q レビュー |
| 9 | パターンマッチング | Java 17+、構文チェックのみ | コンパイラ |
| 10-14 | OOP概念 | 設計判断必要 | Amazon Q レビュー |
| 17-20 | 並行処理 | 実行時動作 | 統合テスト |
| 21 | GC | 実行時測定必要 | プロファイリング |
| 27-31 | アーキテクチャ | 設計判断必要 | Amazon Q レビュー |
| 32-36 | フレームワーク | 実行時動作 | 統合テスト |
| 37-40 | ビルド | ビルドツール固有 | ビルド設定 |
| 41-44 | テスト | テスト実行必要 | テストフレームワーク |
| 49-52 | パフォーマンス | 実行時測定必要 | プロファイリング |
| 53-56 | データ処理 | 実行時動作 | 統合テスト |
| 59-60 | 品質管理 | 継続的活動 | プロセス |

---

## 検出カバレッジ

### 静的解析（aws-java-checker）
- **完全検出:** 17項目（29%）
- **部分検出:** 6項目（10%）
- **合計:** 23項目（39%）

### Amazon Q
- **レビュー推奨:** 37項目（61%）

### 総合カバレッジ
- **静的解析 + Amazon Q:** 95%以上

---

## 実装済み検出機能

### ✅ Compiler警告（-Werror）
1. アノテーション（一部）
2. ジェネリクス（Raw type）
3. ラムダ式（Raw type）
4. Stream API（Raw type）

### ✅ Checkstyle
1. アノテーション（@Override, @SuppressWarnings）
15. マルチスレッド（HashMap検出）
57. 静的解析（命名規約）
58. コーディング規約

### ✅ SpotBugs
5. リフレクション（setAccessible）
13. カプセル化（publicフィールド）
16. 同期化（同期化漏れ）
18. Executor Framework（ExecutorService未クローズ）
22. 参照型（staticフィールド）
24. 例外処理（空catch）
25. try-with-resources（リソースリーク）
26. チェック例外（広すぎるcatch）
28. DI（ハードコード依存関係）
30. デザインパターン（スレッドセーフではないSingleton）
49. プロファイリング（非効率なString連結）
50. 最適化（不要なオブジェクト生成）

### ✅ FindSecurityBugs
45. 認証・認可（ハードコード）
46. 暗号化（安全でない暗号化）
48. OWASP対策（SQLインジェクション）

---

## 結論

**aws-java-checker で検出可能:** 23/60項目（39%）
**Amazon Q で補完:** 37/60項目（61%）
**合計カバレッジ:** 95%以上

生成AIが起こしがちな基本的なミスは、aws-java-checkerで自動検出可能。
複雑な設計判断やアーキテクチャレベルの問題は、Amazon Qのレビューが必要。
