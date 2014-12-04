-- Add healthmap_disease.sub_name field to store subdiseases (e.g. disease = Malaria, subdisease = plasmodium falciparum).
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE healthmap_disease ADD COLUMN sub_name varchar(100);

-- The introduction of sub_name means that HealthMap disease ID is no longer unique
-- So move it from id to healthmap_disease_id
ALTER TABLE healthmap_disease ADD COLUMN healthmap_disease_id integer;
UPDATE healthmap_disease SET healthmap_disease_id = id;
ALTER TABLE healthmap_disease ALTER COLUMN healthmap_disease_id SET NOT NULL;

-- Now make id a serial column (this cannot be done directly, but the below statements have the same effect)
CREATE SEQUENCE healthmap_disease_id_seq;
GRANT SELECT, UPDATE ON healthmap_disease_id_seq TO ${application_username};
ALTER TABLE healthmap_disease ALTER COLUMN id SET DEFAULT nextval('healthmap_disease_id_seq');
SELECT setval('healthmap_disease_id_seq', (SELECT MAX(id) FROM healthmap_disease)) max_healthmap_disease_id;

-- Set uniqueness across name and sub_name
ALTER TABLE healthmap_disease DROP CONSTRAINT uq_healthmap_disease_name;
ALTER TABLE healthmap_disease ADD CONSTRAINT uq_healthmap_disease_name_sub_name UNIQUE (name, sub_name);

-- Disallow commas and spaces within sub_name as these are removed during data acquisition, and also constrain to lowercase
ALTER TABLE healthmap_disease
ADD CONSTRAINT ck_healthmap_disease_sub_name
CHECK (sub_name NOT SIMILAR TO '%[ ,]%' AND sub_name = LOWER(sub_name));

-- HealthMap diseases with sub-disease names
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Filariasis', 'fbanc', 49, 121);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Filariasis', 'fmalayi', 49, 122);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Filariasis', 'ftimori', 49, 123);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Leishmaniasis', 'lnew', 74, 189);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Leishmaniasis', 'lold', 74, 190);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Leishmaniasis', 'lvisceral', 74, 191);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Malaria', 'pk', 80, 250);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Malaria', 'pm', 80, 251);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Malaria', 'po', 80, 252);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Malaria', 'pv', 80, 253);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Malaria', 'pf', 80, 249);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Schistosomiasis', 'sh', 124, 316);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Schistosomiasis', 'sj', 124, 318);
INSERT INTO healthmap_disease (name, sub_name, healthmap_disease_id, disease_group_id) VALUES ('Schistosomiasis', 'sm', 124, 319);
