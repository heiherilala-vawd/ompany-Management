ALTER TABLE team
  ADD COLUMN job_id VARCHAR(150),
  ADD CONSTRAINT fk_team_job FOREIGN KEY (job_id) REFERENCES job(id);

CREATE INDEX IF NOT EXISTS idx_team_job_id ON team(job_id);

ALTER TYPE entity_type ADD VALUE IF NOT EXISTS 'TEAM';
