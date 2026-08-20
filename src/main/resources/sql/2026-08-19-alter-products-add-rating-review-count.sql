ALTER TABLE products
    ADD COLUMN rating DECIMAL(2,1) NULL AFTER discount_rate,
    ADD COLUMN review_count INT NOT NULL DEFAULT 0 AFTER rating;
