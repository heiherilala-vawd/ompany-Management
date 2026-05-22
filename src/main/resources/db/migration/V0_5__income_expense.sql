create table if not exists income_type (
    id varchar(150) constraint income_type_pk primary key default uuid_generate_v4(),
    name varchar(150) not null,
    description text,
    company_id varchar(150) not null,
    constraint income_type_company_fk foreign key (company_id) references company(id)
);

SELECT add_audit_columns('income_type');

create table if not exists income_money (
                                            id VARCHAR(150) constraint income_money_pk  primary key default uuid_generate_v4(),
    source_organization VARCHAR(150),
    invoice_reference VARCHAR(150),
    amount NUMERIC(19, 2) not null,
    description TEXT,
    job_id VARCHAR(150) constraint income_money_job_fk references job(id),
    billing_start_date date,
    income_type_id varchar(150) constraint income_money_income_type_fk references income_type(id)
);

SELECT add_audit_columns('income_money');

create table if not exists expense_money (
                                             id VARCHAR(150) constraint expense_money_pk  primary key default uuid_generate_v4(),
    amount NUMERIC(19, 2) not null,
    description TEXT,
    job_id VARCHAR(150) constraint expense_money_job_fk references job(id)
);

SELECT add_audit_columns('expense_money');
