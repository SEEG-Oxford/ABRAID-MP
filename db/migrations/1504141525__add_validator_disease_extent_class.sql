-- Separate out disease_extent_class (for modeling) from validator_disease_extent_class (for review on validator)
--
-- Copyright (c) 2015 University of Oxford

ALTER TABLE admin_unit_disease_extent_class ADD COLUMN validator_disease_extent_class varchar(17);
UPDATE admin_unit_disease_extent_class SET validator_disease_extent_class=disease_extent_class;
ALTER TABLE admin_unit_disease_extent_class ALTER COLUMN validator_disease_extent_class SET NOT NULL;
ALTER TABLE admin_unit_disease_extent_class ADD CONSTRAINT fk_admin_unit_disease_extent_class_validator_disease_extent_class
    FOREIGN KEY (validator_disease_extent_class) REFERENCES disease_extent_class (name);

ALTER TABLE admin_unit_disease_extent_class RENAME COLUMN occurrence_count to validator_occurrence_count;