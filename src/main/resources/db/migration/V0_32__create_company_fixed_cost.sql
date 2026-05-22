create table if not exists company_fixed_cost (
    id varchar(150) constraint company_fixed_cost_pk primary key default uuid_generate_v4(),
    name varchar(255) not null,
    amount numeric(19,2) not null,
    description text,
    start_date DATE not null,
    end_date DATE,
    company_id varchar(150) not null,
    comment TEXT,
    constraint company_fixed_cost_company_fk foreign key (company_id) references company(id)
);
SELECT add_audit_columns('company_fixed_cost');
create index if not exists idx_company_fixed_cost_company_id on company_fixed_cost(company_id);
