-- Updates the database schema to prevent the display_name on covariate_influence and
-- effect_curve_covariate_influence from being null, so that it may be used on Atlas page.
--
-- Copyright (c) 2014 University of Oxford

UPDATE covariate_influence
SET covariate_display_name = covariate_name 
WHERE covariate_display_name IS NULL;

ALTER TABLE covariate_influence
ALTER COLUMN covariate_display_name SET NOT NULL;

UPDATE effect_curve_covariate_influence
SET covariate_display_name = covariate_name 
WHERE covariate_display_name IS NULL;

ALTER TABLE effect_curve_covariate_influence
ALTER COLUMN covariate_display_name SET NOT NULL;
