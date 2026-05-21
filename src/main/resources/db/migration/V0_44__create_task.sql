do
$$
begin
    alter type entity_type add value if not exists 'TASK';
exception
    when duplicate_object then null;
end
$$;

do
$$
begin
    alter type entity_type add value if not exists 'TASKASSIGNMENT';
exception
    when duplicate_object then null;
end
$$;

create table if not exists task (
    id VARCHAR(150) constraint task_pk primary key,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    frequency VARCHAR(100),
    company_id VARCHAR(150) NOT NULL constraint task_company_fk references company(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(150) constraint task_created_by_fk references users(id),
    updated_by VARCHAR(150) constraint task_updated_by_fk references users(id),
    comment TEXT
);

create index if not exists idx_task_company_id on task(company_id);

create table if not exists task_assignment (
    id VARCHAR(150) constraint task_assignment_pk primary key,
    task_id VARCHAR(150) NOT NULL constraint task_assignment_task_fk references task(id),
    user_id VARCHAR(150) NOT NULL constraint task_assignment_user_fk references users(id),
    completed BOOLEAN NOT NULL DEFAULT false,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(150) constraint task_assignment_created_by_fk references users(id),
    updated_by VARCHAR(150) constraint task_assignment_updated_by_fk references users(id),
    comment TEXT
);

create index if not exists idx_task_assignment_task_id on task_assignment(task_id);
create index if not exists idx_task_assignment_user_id on task_assignment(user_id);
