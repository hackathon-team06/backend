CREATE TABLE point_wallets (
    point_wallet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    point INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,
    CONSTRAINT fk_point_wallets_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_point_wallet_user UNIQUE (user_id)
);

CREATE TABLE point_histories (
    point_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mission_id BIGINT NULL,
    step_id BIGINT NULL,
    reward_type VARCHAR(40) NOT NULL,
    amount INT NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_point_histories_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_point_histories_mission
        FOREIGN KEY (mission_id) REFERENCES generated_missions(generated_mission_id),
    CONSTRAINT fk_point_histories_step
        FOREIGN KEY (step_id) REFERENCES generated_mission_steps(step_id),
    CONSTRAINT uk_point_history_user_step_reward UNIQUE (user_id, step_id, reward_type),
    CONSTRAINT uk_point_history_user_mission_reward UNIQUE (user_id, mission_id, reward_type)
);

UPDATE point_histories
SET mission_id = NULL
WHERE reward_type = 'MISSION_STEP';

UPDATE point_histories
SET step_id = NULL
WHERE reward_type IN ('MORNING_COMPLETE_BONUS', 'EVENING_COMPLETE_BONUS');
