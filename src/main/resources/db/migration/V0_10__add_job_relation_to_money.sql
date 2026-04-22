-- =========================
-- ADD JOB RELATION TO MONEY TABLES
-- =========================

-- Add job_id column to expense_money table
alter table expense_money
add column if not exists job_id VARCHAR(150);

-- Add foreign key constraint for expense_money.job_id
alter table expense_money
add constraint expense_money_job_fk
foreign key (job_id) references job(id);

-- Add job_id column to income_money table
alter table income_money
add column if not exists job_id VARCHAR(150);

-- Add foreign key constraint for income_money.job_id
alter table income_money
add constraint income_money_job_fk
foreign key (job_id) references job(id);

-- Create indexes for better performance
create index if not exists idx_expense_money_job_id on expense_money(job_id);
create index if not exists idx_income_money_job_id on income_money(job_id);
