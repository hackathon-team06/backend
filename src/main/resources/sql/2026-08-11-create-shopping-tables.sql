CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    brand VARCHAR(100) NULL,
    category VARCHAR(30) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    purchase_url VARCHAR(500) NOT NULL,
    price INT NOT NULL,
    original_price INT NULL,
    discount_rate INT NULL,
    price_updated_at DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NULL,
    modified_at DATETIME NULL
);

CREATE TABLE product_skin_types (
    product_id BIGINT NOT NULL,
    skin_type VARCHAR(20) NOT NULL,
    CONSTRAINT fk_product_skin_types_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE product_likes (
    product_like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT uk_product_like_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_product_likes_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_product_likes_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
);
