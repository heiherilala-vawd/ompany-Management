-- =========================
-- TABLE loan
-- =========================
create table if not exists loan (
    id VARCHAR(150) constraint loan_pk primary key,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255),
    lender VARCHAR(255) NOT NULL,
    interest_rate INTEGER NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE,
    job_id VARCHAR(150) constraint loan_job_fk references job(id)
);

SELECT add_audit_columns('loan');

-- =========================
-- TABLE loan_repayment
-- =========================
create table if not exists loan_repayment (
    id VARCHAR(150) constraint loan_repayment_pk primary key,
    payment_date DATE NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    principal_portion NUMERIC(19, 2) NOT NULL,
    interest_portion NUMERIC(19, 2) NOT NULL,
    loan_id VARCHAR(150) NOT NULL constraint loan_repayment_loan_fk references loan(id)
);

SELECT add_audit_columns('loan_repayment');

create index if not exists idx_loan_job_id on loan(job_id);
create index if not exists idx_loan_repayment_loan_id on loan_repayment(loan_id);
