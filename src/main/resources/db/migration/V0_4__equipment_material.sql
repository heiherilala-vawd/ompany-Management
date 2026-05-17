create table if not exists equipment (
                                         id VARCHAR(150) constraint equipment_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    warehouse_id VARCHAR(150) constraint equipment_warehouse_pk REFERENCES warehouse(id),
    floor_number INTEGER,
    storage_number INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );

create table if not exists material (
                                        id VARCHAR(150) constraint material_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    unit material_unit,
    company_id VARCHAR(150) REFERENCES company(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );
