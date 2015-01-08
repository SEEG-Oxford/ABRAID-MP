-- Script: migrate_shapefiles.sql
--
-- Description: Migrate imported shapefiles into the ABRAID-MP tables.
--
-- Copyright (c) 2014 University of Oxford

SET client_min_messages TO WARNING;

-- Move column admin_unit_simplified_global.geom to admin_unit_global.geom
UPDATE admin_unit_global aug
SET simplified_geom = ausg.geom
FROM admin_unit_simplified_global ausg
WHERE aug.gaul_code = ausg.gaul_code;

-- Move column admin_unit_simplified_tropical.geom to admin_unit_tropical.geom
UPDATE admin_unit_tropical aut
SET simplified_geom = aust.geom
FROM admin_unit_simplified_tropical aust
WHERE aut.gaul_code = aust.gaul_code;

-- Add spatial indexes
DROP INDEX IF EXISTS ix_admin_unit_global_geom;
DROP INDEX IF EXISTS ix_admin_unit_global_simplified_geom;
DROP INDEX IF EXISTS ix_admin_unit_qc_geom;
DROP INDEX IF EXISTS ix_admin_unit_tropical_geom;
DROP INDEX IF EXISTS ix_admin_unit_tropical_simplified_geom;
DROP INDEX IF EXISTS ix_country_geom;
DROP INDEX IF EXISTS ix_land_sea_border_geom;
CREATE INDEX ix_admin_unit_global_geom ON admin_unit_global USING GIST (geom);
CREATE INDEX ix_admin_unit_global_simplified_geom ON admin_unit_global USING GIST (simplified_geom);
CREATE INDEX ix_admin_unit_qc_geom ON admin_unit_qc USING GIST (geom);
CREATE INDEX ix_admin_unit_tropical_geom ON admin_unit_tropical USING GIST (geom);
CREATE INDEX ix_admin_unit_tropical_simplified_geom ON admin_unit_tropical USING GIST (simplified_geom);
CREATE INDEX ix_country_geom ON country USING GIST (geom);
CREATE INDEX ix_land_sea_border_geom ON land_sea_border USING GIST (geom);

-- Add NOT NULL constraints to all geometry columns (they are nullable in tables.sql because of the test data)
ALTER TABLE admin_unit_global ALTER simplified_geom SET NOT NULL;
ALTER TABLE admin_unit_global ALTER geom SET NOT NULL;
ALTER TABLE admin_unit_tropical ALTER simplified_geom SET NOT NULL;
ALTER TABLE admin_unit_tropical ALTER geom SET NOT NULL;
ALTER TABLE admin_unit_qc ALTER geom SET NOT NULL;
ALTER TABLE country ALTER geom SET NOT NULL;

-- Drop the simplified shapefile tables
DROP TABLE admin_unit_simplified_global;
DROP TABLE admin_unit_simplified_tropical;

-- Amend country table to indicate the African countries that should be considered when calculating the minimum data spread
UPDATE country SET for_min_data_spread = true WHERE gaul_code in
(4, 6, 8, 29, 35, 42, 43, 45, 47, 49, 50, 58, 59, 66, 68, 70, 74, 76, 77, 79, 89, 90, 94, 102, 105, 106, 133, 142, 144, 145, 150, 152, 155,
159, 161, 169, 170, 172, 181, 182, 205, 214, 217, 221, 226, 235, 243, 248, 253, 257, 268, 270, 271, 40760, 40762, 40765, 61013, 1013965); 
UPDATE country SET for_min_data_spread = false WHERE for_min_data_spread is null;
ALTER TABLE country ALTER for_min_data_spread SET NOT NULL;

VACUUM ANALYZE;
