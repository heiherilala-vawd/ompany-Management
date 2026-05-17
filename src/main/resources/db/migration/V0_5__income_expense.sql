create table if not exists income_type (
    id varchar(150) constraint income_type_pk primary key default uuid_generate_v4(),
    name varchar(150) not null,
    description text,
    company_id varchar(150) not null,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT,
    constraint income_type_company_fk foreign key (company_id) references company(id)
);

create table if not exists income_money (
                                            id VARCHAR(150) constraint income_money_pk  primary key default uuid_generate_v4(),
    source_organization VARCHAR(150),
    invoice_reference VARCHAR(150),
    amount NUMERIC(19, 2) not null,
    description TEXT,
    job_id VARCHAR(150) constraint income_money_job_fk references job(id),
    billing_start_date date,
    income_type_id varchar(150) constraint income_money_income_type_fk references income_type(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );

create table if not exists expense_money (
                                             id VARCHAR(150) constraint expense_money_pk  primary key default uuid_generate_v4(),
    amount NUMERIC(19, 2) not null,
    description TEXT,
    job_id VARCHAR(150) constraint expense_money_job_fk references job(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );
