-- Updates the database schema to add table to represent password reset requests.
-- Copyright (c) 2014 University of Oxford

CREATE TABLE password_reset_request (
    id serial CONSTRAINT pk_password_reset_request PRIMARY KEY,
    hashed_key varchar(60) NOT NULL,
    expert_id integer NOT NULL
        CONSTRAINT uq_password_reset_request_expert_id UNIQUE
        CONSTRAINT fk_password_reset_request_expert REFERENCES expert (id),
    request_date timestamp NOT NULL DEFAULT statement_timestamp()
);

GRANT SELECT, INSERT, DELETE ON password_reset_request TO ${application_username};
GRANT SELECT, UPDATE ON password_reset_request_id_seq TO ${application_username};