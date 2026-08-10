ALTER TABLE schedules
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER category;

UPDATE schedules
SET status = 'ACTIVE'
WHERE status IS NULL;
