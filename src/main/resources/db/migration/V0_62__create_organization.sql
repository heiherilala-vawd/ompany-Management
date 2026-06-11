create table if not exists organization (
    id VARCHAR(150) constraint organization_pk primary key default uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    address TEXT,
    email VARCHAR(255),
    phone VARCHAR(50),
    contact_name VARCHAR(255),
    company_id VARCHAR(150) NOT NULL constraint organization_company_fk REFERENCES company(id)
);
SELECT add_audit_columns('organization');
do $$ begin alter type entity_type add value if not exists 'ORGANIZATION'; exception when duplicate_object then null; end $$;
CREATE INDEX IF NOT EXISTS idx_organization_company ON organization(company_id);
