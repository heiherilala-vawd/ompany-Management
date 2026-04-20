
create extension if not exists "uuid-ossp";

-- =========================
-- ENUMS
-- =========================


do
$$
begin
        if not exists(select from pg_type where typname = 'job_status') then
create type "job_status" as enum ('PENDING_SIGNATURE', 'IN_PROGRESS', 'COMPLETED');
end if;
        if not exists(select from pg_type where typname = 'company_type') then
create type company_type as enum ('BTP', 'HOTEL');
end if;
        if not exists(select from pg_type where typname = 'payment_type') then
create type payment_type as enum ('ADVANCE', 'MONTHLY', 'OTHER');
end if;
        if not exists(select from pg_type where typname = 'transport_status') then
create type transport_status as enum ('IN_PROGRESS', 'LOST', 'ARRIVED');
end if;
        if not exists(select from pg_type where typname = 'sex') then
create type sex as enum ('M', 'F');
end if;
        if not exists(select from pg_type where typname = 'role') then
create type "role" as enum ('ADMIN', 'WAREHOUSE_WORKER', 'EMPLOYEE', 'ADMINISTRATION');
end if;
end
$$;

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

create table if not exists users (
                                     id VARCHAR(150) constraint users_pk  primary key default uuid_generate_v4(),
    role role,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    sex sex,
    email VARCHAR(150) UNIQUE
    );

SELECT add_audit_columns('users');
