--liquibase formatted sql
--changeset ivana:6

ALTER TABLE notes
 ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (
        setweight(to_tsvector('english', title), 'A') ||
        setweight(to_tsvector('english', body), 'B')
    ) STORED;

CREATE INDEX notes_fts ON notes USING gin(search_vector);

