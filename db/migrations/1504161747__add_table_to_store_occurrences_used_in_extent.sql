-- Add a table to store the occurrences used in the last validator extent update.
--
-- Copyright (c) 2014 University of Oxford

CREATE TABLE disease_extent_disease_occurrence (
    disease_group_id integer NOT NULL
        CONSTRAINT fk_disease_extent_disease_occurrence_disease_extent REFERENCES disease_extent (disease_group_id),
    disease_occurrence_id integer NOT NULL
        CONSTRAINT fk_disease_extent_disease_occurrence_disease_occurrence REFERENCES disease_occurrence (id),
    CONSTRAINT pk_disease_extent_disease_occurrence PRIMARY KEY (disease_group_id, disease_occurrence_id)
);

GRANT SELECT, INSERT, DELETE ON disease_extent_disease_occurrence TO ${application_username};