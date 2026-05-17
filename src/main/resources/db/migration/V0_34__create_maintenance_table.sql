create table if not exists maintenance (
    id VARCHAR(150) constraint maintenance_pk primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint maintenance_expense_money_pk REFERENCES expense_money(id),
    equipment_id VARCHAR(150) constraint maintenance_equipment_fk REFERENCES equipment(id),
    description TEXT
);
CREATE INDEX IF NOT EXISTS idx_maintenance_equipment_id ON maintenance(equipment_id);
