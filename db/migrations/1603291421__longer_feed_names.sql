-- Allow longer feed names.
--
-- Copyright (c) 2016 University of Oxford

ALTER TABLE feed ALTER COLUMN name TYPE varchar(200);