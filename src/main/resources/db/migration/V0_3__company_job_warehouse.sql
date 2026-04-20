create table if not exists company (
                                       id VARCHAR(150) constraint company_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    rib VARCHAR(100),
    description TEXT,
    company_type company_type
    );

SELECT add_audit_columns('company');

create table if not exists job (
                                   id VARCHAR(150) constraint job_pk  primary key default uuid_generate_v4(),
    company_id VARCHAR(150) constraint job_company_pk REFERENCES company(id),
    description TEXT,
    contract_signature_date DATE,
    start_date DATE,
    end_date DATE,
    status job_status
    );

SELECT add_audit_columns('job');

create table if not exists warehouse (
                                         id VARCHAR(150) constraint warehouse_pk  primary key default uuid_generate_v4(),
    name VARCHAR(150),
    description TEXT,
    job_id VARCHAR(150) constraint warehouse_job_pk REFERENCES job(id)
    );

SELECT add_audit_columns('warehouse');
