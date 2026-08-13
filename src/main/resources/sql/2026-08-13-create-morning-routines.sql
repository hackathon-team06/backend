CREATE TABLE morning_routines (
    morning_routine_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME NULL,
    modified_at DATETIME NULL,
    CONSTRAINT uk_morning_routine_user UNIQUE (user_id),
    CONSTRAINT fk_morning_routines_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE morning_routine_items (
    morning_routine_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    morning_routine_id BIGINT NOT NULL,
    content VARCHAR(300) NOT NULL,
    category VARCHAR(100) NULL,
    source VARCHAR(20) NOT NULL,
    item_order INT NOT NULL,
    CONSTRAINT uk_morning_routine_item_order UNIQUE (morning_routine_id, item_order),
    CONSTRAINT fk_morning_routine_items_routine
        FOREIGN KEY (morning_routine_id) REFERENCES morning_routines(morning_routine_id)
);
