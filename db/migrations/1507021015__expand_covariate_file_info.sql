-- Expand info text field on covariate files.
-- Copyright (c) 2015 University of Oxford

ALTER TABLE covariate_file ALTER COLUMN info TYPE varchar(500);
