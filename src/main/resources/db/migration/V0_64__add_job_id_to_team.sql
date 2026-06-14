ALTER TABLE team
  ADD COLUMN IF NOT EXISTS job_id VARCHAR(150);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_team_job'
    ) THEN
        ALTER TABLE team
          ADD CONSTRAINT fk_team_job FOREIGN KEY (job_id) REFERENCES job(id);
    END IF;
END;
$$;

CREATE INDEX IF NOT EXISTS idx_team_job_id ON team(job_id);

ALTER TYPE entity_type ADD VALUE IF NOT EXISTS 'TEAM';
