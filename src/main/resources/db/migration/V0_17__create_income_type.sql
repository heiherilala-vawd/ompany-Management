create table if not exists income_type (
    id varchar(150) constraint income_type_pk primary key default uuid_generate_v4(),
    name varchar(150) not null,
    description text,
    company_id varchar(150) not null,
    constraint income_type_company_fk foreign key (company_id) references company(id)
);

select add_audit_columns('income_type');

create index if not exists idx_income_type_company_id on income_type(company_id);
