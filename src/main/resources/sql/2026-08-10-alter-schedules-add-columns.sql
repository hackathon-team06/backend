ALTER TABLE schedules
    ADD COLUMN title VARCHAR(100) NOT NULL AFTER user_id,
    ADD COLUMN start_time TIME NULL AFTER schedule_date,
    ADD COLUMN end_time TIME NULL AFTER start_time;
