-- Rename foreign keys in the healthmap_subdisease table, in accordance with our naming convention.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE healthmap_subdisease RENAME CONSTRAINT fk_healthmap_subdisease_healthmap_disease_id TO fk_healthmap_subdisease_healthmap_disease;
ALTER TABLE healthmap_subdisease RENAME CONSTRAINT fk_healthmap_subdisease_disease_group_id TO fk_healthmap_subdisease_disease_group;
