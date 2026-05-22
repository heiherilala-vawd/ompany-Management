do
$$
begin
    alter type entity_type add value if not exists 'EMPLOYEELEAVECONFIG';
exception
    when duplicate_object then null;
end
$$;

create table if not exists employee_leave_config (
    id VARCHAR(150) constraint employee_leave_config_pk primary key,
    company_id VARCHAR(150) NOT NULL constraint emp_leave_config_company_fk references company(id),
    hire_date DATE,
    contract_type VARCHAR(50),
    vacation_days_per_month NUMERIC(5, 2) NOT NULL DEFAULT 2.50,
    comment TEXT
);
SELECT add_audit_columns('employee_leave_config');

create index if not exists idx_emp_leave_config_company on employee_leave_config(company_id);
