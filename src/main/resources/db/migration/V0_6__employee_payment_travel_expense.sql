create table if not exists employee_payment (
                                                id VARCHAR(150) constraint employee_payment_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint employee_payment_expense_money_pk REFERENCES expense_money(id),
    employee_id varchar not null constraint employee_payment_user_id_fk references "users"(id),
    payment_description TEXT,
    payment_type payment_type
    );

create table if not exists travel_expense (
                                              id VARCHAR(150) constraint travel_expense_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint travel_expense_expense_money_pk REFERENCES expense_money(id),
    departure_location VARCHAR(150) constraint travel_expense_departure_location_fk references warehouse(id),
    arrival_location VARCHAR(150) constraint travel_expense_arrival_location_fk references warehouse(id),
    departure_date timestamp with time zone not null default now(),
    arrival_date timestamp with time zone not null default now()
    );
