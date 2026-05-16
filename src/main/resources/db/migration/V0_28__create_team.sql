CREATE TABLE IF NOT EXISTS team (
    id VARCHAR(150) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    leader_id VARCHAR(150) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150),
    comment TEXT,
    CONSTRAINT fk_team_leader FOREIGN KEY (leader_id) REFERENCES "users"(id),
    CONSTRAINT fk_team_created_by FOREIGN KEY (created_by) REFERENCES "users"(id),
    CONSTRAINT fk_team_updated_by FOREIGN KEY (updated_by) REFERENCES "users"(id)
);

CREATE TABLE IF NOT EXISTS team_members (
    team_id VARCHAR(150) NOT NULL,
    user_id VARCHAR(150) NOT NULL,
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members(team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_user_id ON team_members(user_id);

ALTER TABLE employee_payment
    ADD COLUMN IF NOT EXISTS is_for_team BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS team_id VARCHAR(150),
    ADD CONSTRAINT fk_employee_payment_team FOREIGN KEY (team_id) REFERENCES team(id);

CREATE INDEX IF NOT EXISTS idx_employee_payment_team_id ON employee_payment(team_id);
