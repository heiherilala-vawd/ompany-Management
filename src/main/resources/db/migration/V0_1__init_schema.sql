
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
        if not exists(select from pg_type where typname = 'material_unit') then
create type material_unit as enum (
    'SAC', 'L', 'KG', 'M2', 'M3', 'KIT', 'POT', 'PNL', 'FEU', 'BAR', 'T', 'M', 'FFT', 'U'
);
end if;
end
$$;

create table if not exists users (
                                     id VARCHAR(150) constraint users_pk  primary key default uuid_generate_v4(),
    role role,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    sex sex,
    email VARCHAR(150) UNIQUE,
    password VARCHAR(255) NOT NULL DEFAULT '',
    company_id VARCHAR(150),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );
