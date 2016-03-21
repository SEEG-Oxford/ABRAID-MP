-- Allow null disease occurrence reviews.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE disease_occurrence_review ALTER COLUMN response DROP NOT NULL;