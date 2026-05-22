-- =========================
-- function
-- =========================

CREATE OR REPLACE FUNCTION add_audit_columns(table_name TEXT)
RETURNS VOID AS
$$
BEGIN
    -- Ajouter colonnes
EXECUTE format('
        ALTER TABLE %I
        ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        ADD COLUMN IF NOT EXISTS updated_by VARCHAR(150),
        ADD COLUMN IF NOT EXISTS created_by VARCHAR(150),
        ADD COLUMN IF NOT EXISTS comment TEXT;
    ', table_name);

-- FK updated_by
IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = table_name || '_updated_by_fkey'
    ) THEN
        EXECUTE format('
            ALTER TABLE %I
            ADD CONSTRAINT %I
            FOREIGN KEY (updated_by) REFERENCES users(id);
        ', table_name, table_name || '_updated_by_fkey');
END IF;

    -- FK created_by
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = table_name || '_created_by_fkey'
    ) THEN
        EXECUTE format('
            ALTER TABLE %I
            ADD CONSTRAINT %I
            FOREIGN KEY (created_by) REFERENCES users(id);
        ', table_name, table_name || '_created_by_fkey');
END IF;

END;
$$ LANGUAGE plpgsql;

do
$$
begin
    alter type entity_type add value if not exists 'TASK';
exception
    when duplicate_object then null;
end
$$;

do
$$
begin
    alter type entity_type add value if not exists 'TASKASSIGNMENT';
exception
    when duplicate_object then null;
end
$$;

create table if not exists task (
    id VARCHAR(150) constraint task_pk primary key,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    frequency VARCHAR(100),
    completed BOOLEAN NOT NULL DEFAULT false,
    completed_at TIMESTAMPTZ,
    company_id VARCHAR(150) NOT NULL constraint task_company_fk references company(id)
);

SELECT add_audit_columns('task');

create index if not exists idx_task_company_id on task(company_id);

create table if not exists task_assignment (
    id VARCHAR(150) constraint task_assignment_pk primary key,
    task_id VARCHAR(150) NOT NULL constraint task_assignment_task_fk references task(id),
    user_id VARCHAR(150) NOT NULL constraint task_assignment_user_fk references users(id)
);

SELECT add_audit_columns('task_assignment');

create index if not exists idx_task_assignment_task_id on task_assignment(task_id);
create index if not exists idx_task_assignment_user_id on task_assignment(user_id);
