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

ALTER TABLE admin_unit_global ALTER simplified_geom SET NOT NULL;
ALTER TABLE admin_unit_tropical ALTER simplified_geom SET NOT NULL;

-- Add spatial indexes to the newly-populated columns
CREATE INDEX admin_unit_global_simplified_geom_gist ON admin_unit_global USING GIST (simplified_geom);
CREATE INDEX admin_unit_tropical_simplified_geom_gist ON admin_unit_tropical USING GIST (simplified_geom);

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
