create table if not exists other_expense_type (
    id varchar(150) constraint other_expense_type_pk primary key default uuid_generate_v4(),
    name varchar(150) not null,
    description text,
    company_id varchar(150) not null,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT,
    constraint other_expense_type_company_fk foreign key (company_id) references company(id)
);
create index if not exists idx_other_expense_type_company_id on other_expense_type(company_id);
