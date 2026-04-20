create table if not exists equipment (
                                         id VARCHAR(150) constraint equipment_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    warehouse_id VARCHAR(150) constraint equipment_warehouse_pk REFERENCES warehouse(id),
    floor_number INTEGER,
    storage_number INTEGER
    );

SELECT add_audit_columns('equipment');

create table if not exists material (
                                        id VARCHAR(150) constraint material_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    warehouse_id VARCHAR(150) constraint material_warehouse_pk REFERENCES warehouse(id),
    floor_number INTEGER,
    storage_number INTEGER
    );

SELECT add_audit_columns('material');
