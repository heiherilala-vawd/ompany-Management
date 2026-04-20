create table if not exists income_money (
                                            id VARCHAR(150) constraint income_money_pk  primary key default uuid_generate_v4(),
    source_organization VARCHAR(150),
    invoice_reference VARCHAR(150),
    amount integer not null,
    description TEXT
    );

SELECT add_audit_columns('income_money');

create table if not exists expense_money (
                                             id VARCHAR(150) constraint expense_money_pk  primary key default uuid_generate_v4(),
    amount integer not null,
    description TEXT
    );

SELECT add_audit_columns('expense_money');
