-- Adds a not null boolean field to expert table to indicate whether the user has been shown the Data Validation help text.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE expert
ADD COLUMN has_seen_help_text boolean;

UPDATE expert
SET has_seen_help_text = false;

ALTER TABLE expert
ALTER COLUMN has_seen_help_text SET NOT NULL;
