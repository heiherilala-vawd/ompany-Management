CREATE TABLE user_job (
    job_id VARCHAR(150) NOT NULL,
    user_id VARCHAR(150) NOT NULL,
    PRIMARY KEY (job_id, user_id),
    CONSTRAINT user_job_job_fk FOREIGN KEY (job_id) REFERENCES job(id),
    CONSTRAINT user_job_user_fk FOREIGN KEY (user_id) REFERENCES "users"(id)
);

CREATE INDEX idx_user_job_job_id ON user_job(job_id);
CREATE INDEX idx_user_job_user_id ON user_job(user_id);
