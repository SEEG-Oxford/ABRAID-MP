-- Removes the unique constraint on geoname_id in the location table, to allow multiple locations to be tied to the same geoname (i.e. if geonames/hm fixes lat/long).
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE location DROP CONSTRAINT uq_location_geoname_id;
CREATE INDEX ix_location_geoname_id ON location (geoname_id);