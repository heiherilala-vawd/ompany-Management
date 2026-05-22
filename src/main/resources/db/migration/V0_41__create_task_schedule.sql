CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');
CREATE TYPE schedule_status AS ENUM ('PENDING', 'GENERATED', 'SKIPPED');

ALTER TABLE task DROP COLUMN frequency;
ALTER TABLE task ALTER COLUMN priority DROP DEFAULT;
ALTER TABLE task ALTER COLUMN priority TYPE task_priority USING priority::task_priority;
ALTER TABLE task ALTER COLUMN priority SET NOT NULL;
ALTER TABLE task ALTER COLUMN priority SET DEFAULT 'MEDIUM';

CREATE TABLE task_schedule (
    id VARCHAR(150) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority task_priority NOT NULL DEFAULT 'MEDIUM',
    frequency VARCHAR(100) NOT NULL,
    scheduled_date DATE NOT NULL,
    status schedule_status NOT NULL DEFAULT 'PENDING',
    company_id VARCHAR(150) NOT NULL constraint task_schedule_company_fk references company(id)
);

SELECT add_audit_columns('task_schedule');

CREATE TABLE task_schedule_assigned_user (
    task_schedule_id VARCHAR(150) NOT NULL constraint tsau_schedule_fk references task_schedule(id),
    user_id VARCHAR(150) NOT NULL constraint tsau_user_fk references users(id),
    PRIMARY KEY (task_schedule_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_task_schedule_status_date ON task_schedule(status, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_tsau_schedule_id ON task_schedule_assigned_user(task_schedule_id);

do $$ begin
    alter type entity_type add value if not exists 'TASKSCHEDULE';
exception when duplicate_object then null;
end $$;

do $$ begin
    alter type entity_type add value if not exists 'TASKASSIGNMENT';
exception when duplicate_object then null;
end $$;
