-- Script: migrate_shapefiles.sql
--
-- Description: Migrate imported shapefiles into the ABRAID-MP tables.
--
-- Copyright (c) 2014 University of Oxford


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

-- Add spatial indexes to the newly-populated columns
CREATE INDEX ix_admin_unit_global_simplified_geom ON admin_unit_global USING GIST (simplified_geom);
CREATE INDEX ix_admin_unit_tropical_simplified_geom ON admin_unit_tropical USING GIST (simplified_geom);

-- Rename other spatial indexes to conform to our naming standard
ALTER INDEX admin_unit_global_geom_gist RENAME TO ix_admin_unit_global_geom;
ALTER INDEX admin_unit_qc_geom_gist RENAME TO ix_admin_unit_qc_geom;
ALTER INDEX admin_unit_tropical_geom_gist RENAME TO ix_admin_unit_tropical_geom;
ALTER INDEX country_geom_gist RENAME TO ix_country_geom;
ALTER INDEX land_sea_border_geom_gist RENAME TO ix_land_sea_border_geom;

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

VACUUM ANALYZE;
