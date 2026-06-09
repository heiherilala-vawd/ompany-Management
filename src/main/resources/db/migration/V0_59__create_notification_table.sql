CREATE TABLE notification (
    id VARCHAR(150) PRIMARY KEY,
    user_id VARCHAR(150) NOT NULL REFERENCES users(id),
    task_id VARCHAR(150) REFERENCES task(id),
    title VARCHAR(255) NOT NULL,
    message TEXT,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    comment TEXT,
    created_by VARCHAR(150) REFERENCES users(id),
    updated_by VARCHAR(150) REFERENCES users(id)
);
