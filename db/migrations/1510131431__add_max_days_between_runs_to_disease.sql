-- Add max_days_between_runs to disease_group.
--
-- Copyright (c) 2015 University of Oxford

ALTER TABLE disease_group ADD COLUMN max_days_between_runs integer;
UPDATE disease_group SET max_days_between_runs=7;
ALTER TABLE disease_group ALTER COLUMN max_days_between_runs SET NOT NULL;