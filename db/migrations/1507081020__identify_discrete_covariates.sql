-- Add column to identify discrete covariates.
-- Copyright (c) 2014 University of Oxford

ALTER TABLE covariate_file ADD COLUMN discrete boolean;
UPDATE covariate_file SET discrete = FALSE;
UPDATE covariate_file SET discrete = TRUE WHERE file IN ('upr_p.tif', 'upr_u.tif');
ALTER TABLE covariate_file ALTER COLUMN discrete SET NOT NULL;

-- For existing discrete effect curves delete entries that are not the min or max of the curve
WITH
 covariates AS (SELECT id FROM covariate_file WHERE discrete=true),
 max_value AS (SELECT e.model_run_id, e.covariate_file_id, max(e.covariate_value) FROM effect_curve_covariate_influence AS e WHERE e.covariate_file_id IN (SELECT id FROM covariates) GROUP BY e.model_run_id, e.covariate_file_id),
 max_id AS (SELECT e.model_run_id, e.covariate_file_id, e.id FROM effect_curve_covariate_influence AS e join max_value AS v ON e.model_run_id=v.model_run_id AND e.covariate_file_id=v.covariate_file_id AND e.covariate_value=v.max),
 min_value AS (SELECT e.model_run_id, e.covariate_file_id, min(e.covariate_value) FROM effect_curve_covariate_influence AS e WHERE e.covariate_file_id IN (SELECT id FROM covariates) GROUP BY e.model_run_id, e.covariate_file_id),
 min_id AS (SELECT e.model_run_id, e.covariate_file_id, e.id FROM effect_curve_covariate_influence AS e join min_value AS v ON e.model_run_id=v.model_run_id AND e.covariate_file_id=v.covariate_file_id AND e.covariate_value=v.min),
 keep AS (SELECT id FROM max_id UNION SELECT id FROM min_id)
DELETE FROM effect_curve_covariate_influence AS e WHERE e.id not IN (SELECT k.id FROM keep AS k) AND e.covariate_file_id IN (SELECT c.id FROM covariates AS c);
