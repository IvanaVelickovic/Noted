--liquibase formatted sql
--changeset ivana:5

ALTER TABLE notes
 ADD category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL;

