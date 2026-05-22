do
$$
begin
    alter type entity_type add value if not exists 'LEAVETYPE';
exception
    when duplicate_object then null;
end
$$;

create table if not exists leave_type (
    id VARCHAR(150) constraint leave_type_pk primary key,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    paid BOOLEAN NOT NULL DEFAULT true,
    deduct_from_balance BOOLEAN NOT NULL DEFAULT true,
    color VARCHAR(7),
    days_per_year INTEGER,
    company_id VARCHAR(150) NOT NULL constraint leave_type_company_fk references company(id),
    comment TEXT
);
SELECT add_audit_columns('leave_type');

create index if not exists idx_leave_type_company_id on leave_type(company_id);
