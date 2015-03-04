-- Recalculate resolution weighting using area. Also allow resolution weight to be null, where qc failed
-- Copyright (c) 2014 University of Oxford

ALTER TABLE location ALTER COLUMN resolution_weighting DROP NOT NULL;
UPDATE location SET resolution_weighting=NULL WHERE has_passed_qc=FALSE;
UPDATE location SET resolution_weighting=1 WHERE precision='PRECISE' AND has_passed_qc=TRUE;
UPDATE location SET resolution_weighting=((1.0/ln(25.0/115000.0))*ln((1.0/115000.0)*(SELECT area FROM admin_unit_qc WHERE admin_unit_qc.gaul_code=location.admin_unit_qc_gaul_code))) WHERE precision IN ('ADMIN1', 'ADMIN2') AND has_passed_qc=TRUE AND admin_unit_qc_gaul_code IS NOT NULL;
UPDATE location SET resolution_weighting=((1.0/ln(25.0/115000.0))*ln((1.0/115000.0)*(SELECT area FROM country WHERE country.gaul_code=location.country_gaul_code))) WHERE precision='COUNTRY' AND has_passed_qc=TRUE AND admin_unit_qc_gaul_code IS NOT NULL;
UPDATE location SET resolution_weighting=1 WHERE resolution_weighting>1 AND has_passed_qc=TRUE;