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

create table if not exists employee_payment (
                                                id VARCHAR(150) constraint employee_payment_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint employee_payment_expense_money_pk REFERENCES expense_money(id),
    payment_description TEXT,
    payment_type payment_type,
    is_for_team BOOLEAN DEFAULT FALSE,
    team_id VARCHAR(150) constraint fk_employee_payment_team references team(id)
    );

create table if not exists travel_expense (
                                              id VARCHAR(150) constraint travel_expense_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint travel_expense_expense_money_pk REFERENCES expense_money(id),
    departure_location VARCHAR(150) constraint travel_expense_departure_location_fk references warehouse(id),
    arrival_location VARCHAR(150) constraint travel_expense_arrival_location_fk references warehouse(id),
    departure_date timestamp with time zone not null default now(),
    arrival_date timestamp with time zone not null default now()
    );
