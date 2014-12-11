-- Add a reviewed_date field to the alert table.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE alert ADD COLUMN reviewed_date timestamp;
