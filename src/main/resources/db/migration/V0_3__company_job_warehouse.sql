create table if not exists company (
                                       id VARCHAR(150) constraint company_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    rib VARCHAR(100),
    description TEXT,
    company_type company_type,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );

create table if not exists job (
                                   id VARCHAR(150) constraint job_pk  primary key default uuid_generate_v4(),
    company_id VARCHAR(150) constraint job_company_pk REFERENCES company(id),
    description TEXT,
    contract_signature_date DATE,
    start_date DATE,
    end_date DATE,
    status job_status,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );

create table if not exists warehouse (
                                         id VARCHAR(150) constraint warehouse_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    job_id VARCHAR(150) constraint warehouse_job_pk REFERENCES job(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(150) REFERENCES users(id),
    created_by VARCHAR(150) REFERENCES users(id),
    comment TEXT
    );
