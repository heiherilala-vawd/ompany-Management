-- =========================
-- DEPARTMENT
-- =========================
create table if not exists department (
    id VARCHAR(150) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    company_id VARCHAR(150) NOT NULL constraint department_company_fk references company(id)
);
SELECT add_audit_columns('department');

-- =========================
-- USER NEW FIELDS
-- =========================
alter table users add column if not exists birth_date DATE;
alter table users add column if not exists manager_id VARCHAR(150) constraint user_manager_fk references users(id);
alter table users add column if not exists department_id VARCHAR(150) constraint user_department_fk references department(id);

create index if not exists idx_users_department_id on users(department_id);
create index if not exists idx_users_manager_id on users(manager_id);

-- =========================
-- EMPLOYEE LEAVE CONFIG NEW FIELDS
-- =========================
alter table employee_leave_config add column if not exists end_date DATE;
alter table employee_leave_config add column if not exists weekly_hours INTEGER;

-- =========================
-- EQUIPMENT NEW FIELDS
-- =========================
alter table equipment add column if not exists purchase_price NUMERIC(19,2);
alter table equipment add column if not exists purchase_date DATE;
alter table equipment add column if not exists category VARCHAR(100);

-- =========================
-- MATERIAL NEW FIELDS
-- =========================
alter table material add column if not exists unit_price NUMERIC(19,2);

-- =========================
-- MATERIAL WAREHOUSE NEW FIELDS
-- =========================
alter table material_warehouse add column if not exists min_stock INTEGER;
alter table material_warehouse add column if not exists max_stock INTEGER;

-- =========================
-- CASH ACCOUNT
-- =========================
create table if not exists cash_account (
    id VARCHAR(150) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    description TEXT,
    company_id VARCHAR(150) NOT NULL constraint cash_account_company_fk references company(id)
);
SELECT add_audit_columns('cash_account');

-- =========================
-- CASH TRANSACTION
-- =========================
create table if not exists cash_transaction (
    id VARCHAR(150) PRIMARY KEY,
    cash_account_id VARCHAR(150) NOT NULL constraint ct_cash_account_fk references cash_account(id),
    amount NUMERIC(19,2) NOT NULL,
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    description TEXT,
    type VARCHAR(20) NOT NULL
);
SELECT add_audit_columns('cash_transaction');

create index if not exists idx_cash_transaction_account on cash_transaction(cash_account_id);
create index if not exists idx_cash_transaction_date on cash_transaction(transaction_date);

-- =========================
-- BUDGET LINE
-- =========================
create table if not exists budget_line (
    id VARCHAR(150) PRIMARY KEY,
    company_id VARCHAR(150) NOT NULL constraint budget_line_company_fk references company(id),
    category VARCHAR(255) NOT NULL,
    planned_amount NUMERIC(19,2) NOT NULL,
    actual_amount NUMERIC(19,2),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    description TEXT
);
SELECT add_audit_columns('budget_line');

create index if not exists idx_budget_line_company on budget_line(company_id);
create index if not exists idx_budget_line_period on budget_line(period_start, period_end);

-- =========================
-- EQUIPMENT USAGE
-- =========================
create table if not exists equipment_usage (
    id VARCHAR(150) PRIMARY KEY,
    equipment_id VARCHAR(150) NOT NULL constraint eu_equipment_fk references equipment(id),
    job_id VARCHAR(150) constraint eu_job_fk references job(id),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ
);
SELECT add_audit_columns('equipment_usage');

create index if not exists idx_equipment_usage_equipment on equipment_usage(equipment_id);
create index if not exists idx_equipment_usage_job on equipment_usage(job_id);

-- =========================
-- MATERIAL CONSUMPTION
-- =========================
create table if not exists material_consumption (
    id VARCHAR(150) PRIMARY KEY,
    material_id VARCHAR(150) NOT NULL constraint mc_material_fk references material(id),
    warehouse_id VARCHAR(150) NOT NULL constraint mc_warehouse_fk references warehouse(id),
    quantity INTEGER NOT NULL,
    consumption_date DATE NOT NULL DEFAULT CURRENT_DATE,
    job_id VARCHAR(150) constraint mc_job_fk references job(id),
    reason TEXT
);
SELECT add_audit_columns('material_consumption');

create index if not exists idx_material_consumption_material on material_consumption(material_id);
create index if not exists idx_material_consumption_date on material_consumption(consumption_date);

-- =========================
-- ENTITY TYPE ENUM
-- =========================
do $$ begin
    alter type entity_type add value if not exists 'DEPARTMENT';
exception when duplicate_object then null;
end $$;
do $$ begin
    alter type entity_type add value if not exists 'CASHACCOUNT';
exception when duplicate_object then null;
end $$;
do $$ begin
    alter type entity_type add value if not exists 'CASHTRANSACTION';
exception when duplicate_object then null;
end $$;
do $$ begin
    alter type entity_type add value if not exists 'BUDGETLINE';
exception when duplicate_object then null;
end $$;
do $$ begin
    alter type entity_type add value if not exists 'EQUIPMENTUSAGE';
exception when duplicate_object then null;
end $$;
do $$ begin
    alter type entity_type add value if not exists 'MATERIALCONSUMPTION';
exception when duplicate_object then null;
end $$;
