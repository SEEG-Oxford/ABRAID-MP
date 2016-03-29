-- Allow longer file paths for covariates.
--
-- Copyright (c) 2016 University of Oxford

ALTER TABLE covariate_sub_file ALTER COLUMN file TYPE varchar(250);
ALTER TABLE covariate_file ALTER COLUMN name TYPE varchar(150);