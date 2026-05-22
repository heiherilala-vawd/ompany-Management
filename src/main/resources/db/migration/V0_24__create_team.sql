CREATE TABLE IF NOT EXISTS team (
    id VARCHAR(150) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    leader_id VARCHAR(150) NOT NULL,
    CONSTRAINT fk_team_leader FOREIGN KEY (leader_id) REFERENCES "users"(id)
);

SELECT add_audit_columns('team');

CREATE TABLE IF NOT EXISTS team_members (
    team_id VARCHAR(150) NOT NULL,
    user_id VARCHAR(150) NOT NULL,
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);
SELECT add_audit_columns('team_members');

CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members(team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_user_id ON team_members(user_id);

CREATE INDEX IF NOT EXISTS idx_employee_payment_team_id ON employee_payment(team_id);
