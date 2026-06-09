-- ============================================================
-- Migrate task_schedule rows to use new unified enum values
-- This MUST be in a separate migration from the ALTER TYPE ADD VALUE
-- because PostgreSQL cannot use a new enum value in the same transaction
-- where it was added.
-- ============================================================

UPDATE task_schedule
SET status = 'ACTIVE'
WHERE status = 'PENDING';

UPDATE task_schedule
SET status = 'PAUSED'
WHERE status = 'SKIPPED';

UPDATE task_schedule
SET status = 'DONE'
WHERE status = 'GENERATED';
