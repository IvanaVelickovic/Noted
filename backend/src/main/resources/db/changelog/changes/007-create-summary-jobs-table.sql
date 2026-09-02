--liquibase formatted sql
--changeset ivana:7

CREATE TABLE summary_jobs(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    result TEXT,
    error_message TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL REFERENCES users(id),
    note_id BIGINT NOT NULL REFERENCES notes(id)
)