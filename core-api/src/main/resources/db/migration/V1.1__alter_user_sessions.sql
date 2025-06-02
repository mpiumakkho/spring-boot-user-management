-- Drop the active column and add status column
ALTER TABLE sample_app.user_sessions 
    DROP COLUMN IF EXISTS active,
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

-- Add check constraint to ensure status is either 'active' or 'inactive'
ALTER TABLE sample_app.user_sessions 
    ADD CONSTRAINT chk_user_sessions_status 
    CHECK (status IN ('active', 'inactive')); 