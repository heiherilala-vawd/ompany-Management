alter table users
add column if not exists employee_leave_config_id VARCHAR(150)
    constraint users_emp_leave_config_fk references employee_leave_config(id);

create index if not exists idx_users_emp_leave_config on users(employee_leave_config_id);
