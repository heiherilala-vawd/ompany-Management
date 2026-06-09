-- Create join table for user-company many-to-many relationship
CREATE TABLE IF NOT EXISTS users_companies (
    user_id VARCHAR(150) NOT NULL,
    company_id VARCHAR(150) NOT NULL,
    PRIMARY KEY (user_id, company_id),
    CONSTRAINT fk_users_companies_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_companies_company FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

-- Migrate existing data from users.company_id to the join table
INSERT INTO users_companies (user_id, company_id)
SELECT id, company_id FROM "users" WHERE company_id IS NOT NULL;

-- Drop the old company_id column from users
ALTER TABLE "users" DROP COLUMN IF EXISTS company_id;
