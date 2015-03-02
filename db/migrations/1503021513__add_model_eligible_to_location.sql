-- Add model_eligible to location.
-- Copyright (c) 2014 University of Oxford

ALTER TABLE location ADD COLUMN model_eligible boolean;
UPDATE location SET model_eligible=((has_passed_qc is TRUE) AND ((precision IN ('PRECISE', 'ADMIN1', 'ADMIN2')) OR (precision='COUNTRY' AND country_gaul_code IN (SELECT gaul_code FROM country WHERE area <= 115000))));
ALTER TABLE location ALTER COLUMN model_eligible SET NOT NULL;