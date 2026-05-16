-- テーブル作成と初期データ登録
-- Docker 起動時に一度だけ実行される（ボリュームが空のときのみ）

-- ─────────────────────────────────────────────
-- 商品テーブル
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    description VARCHAR(500),
    price       NUMERIC(10, 2) NOT NULL,
    stock       INTEGER        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 注文テーブル
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS orders (
    id            BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255)   NOT NULL,
    status        VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_amount  NUMERIC(10, 2) NOT NULL,
    ordered_at    TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 注文明細テーブル
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS order_items (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders(id),
    product_id BIGINT         NOT NULL REFERENCES products(id),
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL
);

-- ─────────────────────────────────────────────
-- 初期データ（商品）
-- ─────────────────────────────────────────────
INSERT INTO products (name, description, price, stock, created_at, updated_at) VALUES
    ('ノートPC',           '高性能薄型ノートPC 15インチ',  89800, 20, NOW(), NOW()),
    ('ワイヤレスマウス',   '静音設計 Bluetooth対応',         3980, 100, NOW(), NOW()),
    ('メカニカルキーボード','青軸 フルサイズ USB-C',         12800, 50, NOW(), NOW()),
    ('4Kモニター',         '27インチ IPS パネル HDR対応',   49800, 15, NOW(), NOW()),
    ('USBハブ',            '7ポート USB3.0 電源付き',         4500, 80, NOW(), NOW());
