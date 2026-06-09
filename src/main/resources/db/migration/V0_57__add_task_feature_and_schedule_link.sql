-- ============================================================
-- Add polymorphic feature reference and schedule link to task
-- feature_name / feature_id: generic entity reference
--   e.g. feature_name='equipment', feature_id='eq_001'
-- maintenance_schedule_id: FK back to the schedule that
--   generated this task (nullable)
-- ============================================================

ALTER TABLE task
ADD COLUMN IF NOT EXISTS feature_name VARCHAR(255);

ALTER TABLE task
ADD COLUMN IF NOT EXISTS feature_id VARCHAR(150);

ALTER TABLE task
ADD COLUMN IF NOT EXISTS maintenance_schedule_id VARCHAR(150);

ALTER TABLE task
ADD CONSTRAINT task_maintenance_schedule_fk
FOREIGN KEY (maintenance_schedule_id) REFERENCES maintenance_schedule(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_task_feature
ON task(feature_name, feature_id);

CREATE INDEX IF NOT EXISTS idx_task_maintenance_schedule
ON task(maintenance_schedule_id);

do $$ begin
    alter type entity_type add value if not exists 'MAINTENANCESCHEDULE';
exception when duplicate_object then null;
end $$;
