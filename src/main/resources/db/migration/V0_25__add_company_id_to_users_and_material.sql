ALTER TABLE "users" ADD COLUMN IF NOT EXISTS company_id VARCHAR(150);
ALTER TABLE "material" ADD COLUMN IF NOT EXISTS company_id VARCHAR(150);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'users_company_id_fkey'
    ) THEN
        ALTER TABLE "users"
        ADD CONSTRAINT users_company_id_fkey
        FOREIGN KEY (company_id) REFERENCES company(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'material_company_id_fkey'
    ) THEN
        ALTER TABLE "material"
        ADD CONSTRAINT material_company_id_fkey
        FOREIGN KEY (company_id) REFERENCES company(id);
    END IF;
END;
$$;
