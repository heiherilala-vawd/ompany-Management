CREATE TABLE IF NOT EXISTS employee_payment_users (
    employee_payment_id VARCHAR(150) NOT NULL,
    user_id VARCHAR(150) NOT NULL,
    PRIMARY KEY (employee_payment_id, user_id),
    CONSTRAINT fk_emp_pay_users_payment FOREIGN KEY (employee_payment_id) REFERENCES employee_payment(id),
    CONSTRAINT fk_emp_pay_users_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);
SELECT add_audit_columns('employee_payment_users');

CREATE INDEX IF NOT EXISTS idx_emp_pay_users_payment_id ON employee_payment_users(employee_payment_id);
CREATE INDEX IF NOT EXISTS idx_emp_pay_users_user_id ON employee_payment_users(user_id);
