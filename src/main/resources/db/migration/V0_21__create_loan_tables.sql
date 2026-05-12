-- =========================
-- ENUM loan_status
-- =========================
do
$$
begin
    if not exists(select from pg_type where typname = 'loan_status') then
        create type loan_status as enum ('ACTIVE', 'PAID', 'DEFAULTED');
    end if;
end
$$;

-- =========================
-- TABLE loan
-- =========================
create table if not exists loan (
    id VARCHAR(150) constraint loan_pk primary key,
    amount INTEGER NOT NULL,
    description VARCHAR(255),
    lender VARCHAR(255) NOT NULL,
    interest_rate INTEGER NOT NULL,
    start_date DATE NOT NULL,
    status loan_status NOT NULL DEFAULT 'ACTIVE',
    job_id VARCHAR(150) constraint loan_job_fk references job(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(150) constraint loan_created_by_fk references users(id),
    updated_by VARCHAR(150) constraint loan_updated_by_fk references users(id),
    comment TEXT
);

-- =========================
-- TABLE loan_repayment
-- =========================
create table if not exists loan_repayment (
    id VARCHAR(150) constraint loan_repayment_pk primary key,
    payment_date DATE NOT NULL,
    amount INTEGER NOT NULL,
    principal_portion INTEGER NOT NULL,
    interest_portion INTEGER NOT NULL,
    loan_id VARCHAR(150) NOT NULL constraint loan_repayment_loan_fk references loan(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(150) constraint loan_repayment_created_by_fk references users(id),
    updated_by VARCHAR(150) constraint loan_repayment_updated_by_fk references users(id),
    comment TEXT
);

create index if not exists idx_loan_job_id on loan(job_id);
create index if not exists idx_loan_repayment_loan_id on loan_repayment(loan_id);
