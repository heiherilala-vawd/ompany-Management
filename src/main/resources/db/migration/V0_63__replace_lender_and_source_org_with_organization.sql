ALTER TABLE loan ADD COLUMN IF NOT EXISTS organization_id VARCHAR(150) REFERENCES organization(id);
ALTER TABLE income_money ADD COLUMN IF NOT EXISTS organization_id VARCHAR(150) REFERENCES organization(id);
ALTER TABLE loan DROP COLUMN IF EXISTS lender;
ALTER TABLE income_money DROP COLUMN IF EXISTS source_organization;
CREATE INDEX IF NOT EXISTS idx_loan_organization ON loan(organization_id);
CREATE INDEX IF NOT EXISTS idx_income_money_organization ON income_money(organization_id);
