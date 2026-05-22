do
$$
begin
    alter type entity_type add value if not exists 'LEAVE';
exception
    when duplicate_object then null;
end
$$;

create table if not exists "leave" (
    id VARCHAR(150) constraint leave_pk primary key,
    user_id VARCHAR(150) NOT NULL constraint leave_user_fk references users(id),
    leave_type_id VARCHAR(150) NOT NULL constraint leave_type_fk references leave_type(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    duration_days NUMERIC(5, 1) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    approved_by VARCHAR(150) constraint leave_approved_by_fk references users(id),
    approved_at TIMESTAMPTZ,
    comment TEXT
);
SELECT add_audit_columns('leave');

create index if not exists idx_leave_user_id on "leave"(user_id);
create index if not exists idx_leave_status on "leave"(status);
create index if not exists idx_leave_start_date on "leave"(start_date);
