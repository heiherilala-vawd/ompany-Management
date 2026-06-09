create table if not exists organisation (
    id VARCHAR(150) constraint organisation_pk primary key default uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    address TEXT,
    email VARCHAR(255),
    phone VARCHAR(50),
    company_id VARCHAR(150) NOT NULL constraint organisation_company_fk REFERENCES company(id)
);

SELECT add_audit_columns('organisation');
