ALTER TABLE schedules
    ADD COLUMN start_date DATE NULL,
    ADD COLUMN end_date DATE NULL;

UPDATE schedules
SET start_date = schedule_date,
    end_date = schedule_date
WHERE start_date IS NULL
   OR end_date IS NULL;

ALTER TABLE schedules
    MODIFY COLUMN start_date DATE NOT NULL,
    MODIFY COLUMN end_date DATE NOT NULL;
