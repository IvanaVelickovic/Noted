--liquibase formatted sql
--changeset ivana:4

CREATE TABLE categories(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    UNIQUE(user_id, name)
)

