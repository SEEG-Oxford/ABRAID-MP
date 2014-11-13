-- Rename covariate_name to covariate_file_path on covariate_influence
-- and effect_curve_covariate_influence for clarity.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE covariate_influence
RENAME COLUMN covariate_name TO covariate_file_path;

ALTER TABLE covariate_influence
RENAME CONSTRAINT uq_covariate_influence_model_run_id_covariate_name TO uq_covariate_influence_model_run_id_covariate_file_path;

ALTER TABLE effect_curve_covariate_influence
RENAME COLUMN covariate_name TO covariate_file_path;
