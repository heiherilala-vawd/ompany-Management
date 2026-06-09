do
$$
begin
    alter type entity_type add value if not exists 'LEAVEACCRUEDBYMONTH';
exception
    when duplicate_object then null;
end
$$;

create table if not exists leave_accrued_by_month (
    id VARCHAR(150) constraint leave_accrued_by_month_pk primary key,
    user_id VARCHAR(150) NOT NULL constraint labm_user_fk references "users"(id),
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    accrued_days NUMERIC(5, 2) NOT NULL DEFAULT 0,
    comment TEXT
);
SELECT add_audit_columns('leave_accrued_by_month');

create index if not exists idx_labm_user_year on leave_accrued_by_month(user_id, year);
