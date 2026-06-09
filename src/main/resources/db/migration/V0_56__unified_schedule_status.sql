-- ============================================================
-- Unify schedule statuses: ACTIVE / PAUSED / DONE
-- Both task_schedule and maintenance_schedule share the same
-- perpetual model: ACTIVE remains active forever, advancing
-- its scheduled_date; PAUSED suspends; DONE terminates.
-- ============================================================

-- Add new values to the existing PostgreSQL enum
ALTER TYPE schedule_status ADD VALUE IF NOT EXISTS 'ACTIVE';
ALTER TYPE schedule_status ADD VALUE IF NOT EXISTS 'PAUSED';
ALTER TYPE schedule_status ADD VALUE IF NOT EXISTS 'DONE';

-- NOTE: task_schedule UPDATEs are in V0_58__migrate_task_schedule_statuses.sql
-- because PostgreSQL forbids using a new enum value in the same transaction
-- where it was added.

-- Migrate existing maintenance_schedule rows (VARCHAR column, no DB enum)
UPDATE maintenance_schedule
SET status = 'ACTIVE'
WHERE status IN ('PENDING', 'SCHEDULED');

UPDATE maintenance_schedule
SET status = 'PAUSED'
WHERE status = 'SKIPPED';
