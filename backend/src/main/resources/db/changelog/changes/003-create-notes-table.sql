--liquibase formatted sql
--changeset ivana:1

CREATE TABLE notes(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_edited TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL DEFAULT '',
    user_id BIGINT NOT NULL REFERENCES users(id),
    delete BOOLEAN DEFAULT false
)