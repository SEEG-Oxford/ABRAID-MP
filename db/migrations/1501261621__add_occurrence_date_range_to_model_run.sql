-- Updates the database schema to add occurrence_data_range_start_date and occurrence_data_range_end_date columns to the model_run table.
-- Copyright (c) 2015 University of Oxford

ALTER TABLE model_run ADD COLUMN occurrence_data_range_start_date timestamp;
ALTER TABLE model_run ADD COLUMN occurrence_data_range_end_date timestamp;

-- Set all old model runs to "now", as old model runs will be deleted and the data range is not retrievable for non-automatic historic runs.
UPDATE model_run
SET occurrence_data_range_start_date = statement_timestamp()
WHERE occurrence_data_range_start_date IS NULL;

UPDATE model_run
SET occurrence_data_range_end_date = statement_timestamp()
WHERE occurrence_data_range_end_date IS NULL;


ALTER TABLE model_run ALTER COLUMN occurrence_data_range_start_date SET NOT NULL;
ALTER TABLE model_run ALTER COLUMN occurrence_data_range_end_date SET NOT NULL;
