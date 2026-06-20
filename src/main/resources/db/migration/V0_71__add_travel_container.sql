create table if not exists travel_container (
    id VARCHAR(150) constraint travel_container_pk primary key default uuid_generate_v4(),
    travel_id VARCHAR(150) not null constraint travel_container_travel_fk references travel_expense(id),
    name VARCHAR(255) not null,
    description TEXT
);

select add_audit_columns('travel_container');

alter table travel_equipment add column container_id VARCHAR(150) constraint travel_equipment_container_fk references travel_container(id);
alter table travel_materials add column container_id VARCHAR(150) constraint travel_materials_container_fk references travel_container(id);

create index if not exists idx_travel_container_travel_id on travel_container(travel_id);
