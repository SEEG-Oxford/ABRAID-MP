-- Add table for storing covariate subfiles
-- Copyright (c) 2016 University of Oxford

-- Create covariate_sub_file
-- Qualifier will normally be year or month
CREATE TABLE covariate_sub_file (
    id serial CONSTRAINT pk_covariate_sub_file PRIMARY KEY,
    covariate_file_id integer NOT NULL
        CONSTRAINT fk_covariate_sub_file_covariate_file REFERENCES covariate_file (id),
    file varchar(80) NOT NULL
        CONSTRAINT uq_covariate_sub_file_file UNIQUE,
    qualifier varchar(10)
);

GRANT SELECT, INSERT, UPDATE ON covariate_sub_file TO ${application_username};
GRANT SELECT, UPDATE ON covariate_sub_file_id_seq TO ${application_username};

INSERT INTO covariate_sub_file (covariate_file_id, file, qualifier) SELECT id, file, 'Single' FROM covariate_file;
ALTER TABLE covariate_file DROP COLUMN file;
