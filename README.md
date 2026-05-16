# ECサイトAPI - Spring Boot サンプル

## 技術スタック

| 分類 | 内容 |
|------|------|
| フレームワーク | Spring Boot 4.0.1 |
| ORM | Hibernate 7.1（Spring Data JPA 経由） |
| DB | PostgreSQL |
| 言語 | Java 25 |
| ビルド | Gradle 8.14 (Kotlin DSL) |
| コードフォーマット | Spotless 8.4.0 + Google Java Format |

## プロジェクト構成

```
ec-api/
├── build.gradle.kts             # ビルド設定・依存関係・Spotless 設定
├── settings.gradle.kts
├── gradle/wrapper/
│   └── gradle-wrapper.properties   # Gradle 8.14
└── src/main/java/com/example/ecapi/
    ├── EcApiApplication.java
    ├── config/
    │   └── DataInitializer.java     # 起動時サンプルデータ投入
    ├── controller/
    │   ├── ProductController.java
    │   ├── OrderController.java
    │   └── dto/                     # Web 層専用 DTO (Request/Response)
    │       ├── ProductDto.java
    │       └── OrderDto.java
    ├── service/
    │   ├── ProductService.java
    │   ├── OrderService.java        # 在庫チェック・トランザクション管理
    │   └── dto/                     # Service 層専用 DTO (Command/Result)
    │       ├── ProductServiceDto.java
    │       └── OrderServiceDto.java
    ├── repository/
    │   ├── ProductRepository.java
    │   └── OrderRepository.java
    ├── entity/
    │   ├── Product.java
    │   ├── Order.java
    │   └── OrderItem.java
    └── exception/
        └── GlobalExceptionHandler.java
```

## 起動手順

### 1. docker起動

```
docker-compose up -d
```

### 2. 接続設定の確認・変更

`src/main/resources/application.yml` のユーザー名・パスワードを環境に合わせて変更。

### 3. 起動

```bash
./gradlew bootRun
```

起動後、テーブルが自動作成されサンプル商品5件が登録されます。

---

## Spotless の使い方

```bash
# コードを自動整形（Google Java Format）
./gradlew spotlessApply

# フォーマットチェックのみ（CI で使用）
./gradlew spotlessCheck

# ビルドと同時にチェック（build.gradle.kts のコメントを外す）
./gradlew check
```

### Git フック（任意）

プッシュ前に自動チェックするフックを設定できます：

```bash
./gradlew spotlessInstallGitPrePushHook
```

---

## API エンドポイント

### 商品 API

| メソッド | URL | 説明 |
|--------|-----|------|
| GET | /api/products | 全商品取得 |
| GET | /api/products?q=キーワード | 商品名検索 |
| GET | /api/products/{id} | 商品詳細 |
| POST | /api/products | 商品登録 |
| PUT | /api/products/{id} | 商品更新 |
| DELETE | /api/products/{id} | 商品削除 |

### 注文 API

| メソッド | URL | 説明 |
|--------|-----|------|
| GET | /api/orders | 全注文取得 |
| GET | /api/orders/{id} | 注文詳細 |
| POST | /api/orders | 注文作成（在庫チェックあり） |
| PATCH | /api/orders/{id}/status?status=CONFIRMED | ステータス更新 |

---

## 動作確認（curl）

```bash
# 商品一覧
curl http://localhost:8080/api/products

# 商品名検索
curl "http://localhost:8080/api/products?q=USBハブ"

# 商品登録
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"ヘッドセット","description":"ノイズキャンセリング","price":15800,"stock":30}'

# 注文作成（在庫が自動減算される）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "山田太郎",
    "items": [
      {"productId": 1, "quantity": 1},
      {"productId": 2, "quantity": 2}
    ]
  }'

# ステータス更新
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
```

---

## 学習ポイント

1. **Gradle Kotlin DSL** `build.gradle.kts` による型安全なビルド設定
2. **Spotless** Google Java Format でチーム全体のコードスタイルを統一
3. **Hibernate 7** Spring Boot 4.0 に同梱される最新 ORM。Jakarta EE 11 ベース
4. **レイヤードアーキテクチャ** Controller → Service → Repository の役割分担
5. **Spring Data JPA** メソッド名クエリ・`@Query` によるカスタムクエリ
6. **トランザクション管理** `@Transactional` で在庫チェック〜注文〜在庫減算を原子的に実行
7. **N+1 問題対策** `LEFT JOIN FETCH` で関連エンティティを一括取得
8. **例外ハンドリング** `@RestControllerAdvice` で統一エラーレスポンス
