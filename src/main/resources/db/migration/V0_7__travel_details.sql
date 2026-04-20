create table if not exists travel_people (
                                             id VARCHAR(150) constraint travel_people_pk  primary key default uuid_generate_v4(),
    travel_id VARCHAR(150) constraint travel_people_travel_expense_pk REFERENCES travel_expense(id),
    person_name VARCHAR(150)
    );

SELECT add_audit_columns('travel_people');

create table if not exists travel_materials (
                                                id VARCHAR(150) constraint travel_materials_pk  primary key default uuid_generate_v4(),
    travel_id VARCHAR(150) constraint travel_materials_travel_expense_pk REFERENCES travel_expense(id),
    material varchar not null constraint purchase_material_id_fk references "material"(id),
    quantity integer not null,
    quantity_received integer
    );

SELECT add_audit_columns('travel_materials');

create table if not exists travel_equipment (
                                                id VARCHAR(150) constraint travel_equipment_pk  primary key default uuid_generate_v4(),
    travel_id VARCHAR(150) constraint travel_materials_travel_expense_pk REFERENCES travel_expense(id),
    equipment varchar not null constraint purchase_equipment_id_fk references "equipment"(id),
    quantity integer not null,
    status transport_status
    );

SELECT add_audit_columns('travel_equipment');
