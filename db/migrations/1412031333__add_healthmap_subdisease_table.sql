-- Add healthmap_subdisease table to store sub-diseases (e.g. disease = Malaria, subdisease = plasmodium falciparum).
--
-- Copyright (c) 2014 University of Oxford

CREATE TABLE healthmap_subdisease (
    id serial
        CONSTRAINT pk_healthmap_subdisease PRIMARY KEY,
    healthmap_disease_id integer NOT NULL
        CONSTRAINT fk_healthmap_subdisease_healthmap_disease_id REFERENCES healthmap_disease (id),
    name varchar(100) NOT NULL
        -- Subdisease names are globally unique (not unique per disease)
        CONSTRAINT uq_healthmap_subdisease_name UNIQUE
        -- Disallow commas and spaces as these are removed during data acquisition, and also constrain to lowercase
        CONSTRAINT ck_healthmap_subdisease_name CHECK (name NOT SIMILAR TO '%[ ,]%' AND name = LOWER(name)),
    disease_group_id integer
        CONSTRAINT fk_healthmap_subdisease_disease_group_id REFERENCES disease_group (id),
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE INDEX ix_healthmap_subdisease_healthmap_disease_id ON healthmap_subdisease (healthmap_disease_id);

GRANT SELECT ON healthmap_subdisease TO ${application_username};
GRANT SELECT, UPDATE ON healthmap_subdisease_id_seq TO ${application_username};

INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (49, 'fbanc', 121);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (49, 'fmalayi', 122);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (49, 'ftimori', 123);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (74, 'lnew', 189);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (74, 'lold', 190);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (74, 'lvisceral', 191);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (80, 'pk', 250);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (80, 'pm', 251);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (80, 'po', 252);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (80, 'pv', 253);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (80, 'pf', 249);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (124, 'sh', 316);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (124, 'sj', 318);
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES (124, 'sm', 319);
