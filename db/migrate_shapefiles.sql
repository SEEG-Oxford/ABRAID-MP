-- Script: migrate_shapefiles.sql
--
-- Description: Migrate imported shapefiles into the ABRAID-MP tables.
--
-- Copyright (c) 2014 University of Oxford


-- Shapefile A: abraid_admin_0_1_tropical.shp
INSERT INTO admin_unit_tropical(gaul_code, parent_gaul_code, country_code, admin_level, name, public_name, geom)
SELECT gaul, parent_id, country_id, admin_leve, name, pub_name, geom
FROM abraid_admin_0_1_tropical;

-- Shapefile B: abraid_admin_0_1_global.shp
INSERT INTO admin_unit_global(gaul_code, parent_gaul_code, country_code, admin_level, name, public_name, geom)
SELECT gaul, parent_id, country_id, admin_leve, name, pub_name, geom
FROM abraid_admin_0_1_global;

-- Shapefile C: abraid_admin_0_1__simp_tropical.shp
INSERT INTO admin_unit_simplified_tropical(gaul_code, name, public_name, geom)
SELECT gaul, name, pub_name, geom
FROM abraid_admin_0_1__simp_tropical;

-- Shapefile D: abraid_admin_0_1__simp_global.shp
INSERT INTO admin_unit_simplified_global(gaul_code, name, public_name, geom)
SELECT gaul, name, pub_name, geom
FROM abraid_admin_0_1__simp_global;

-- Shapefile E: admin2013_1.shp
INSERT INTO admin_unit(gaul_code, parent_gaul_code, country_code, admin_level, name, centroid_latitude, centroid_longitude, area, geom)
SELECT gaul, parent_id, country_id, admin_leve, name, cent_lat, cent_lon, area, geom
FROM admin2013_1;

-- Shapefile F: admin2013_2.shp
INSERT INTO admin_unit(gaul_code, parent_gaul_code, country_code, admin_level, name, centroid_latitude, centroid_longitude, area, geom)
SELECT gaul, parent_id, country_id, admin_leve, name, cen_lat, cen_lon, area, geom
FROM admin2013_2;

-- Shapefile G: admin2013_0.shp
INSERT INTO country(gaul_code, country_code, name, geom)
SELECT gaul, country_id, name, geom
FROM admin2013_0;

-- Drop the import tables
DROP TABLE abraid_admin_0_1_tropical;
DROP TABLE abraid_admin_0_1_global;
DROP TABLE abraid_admin_0_1__simp_tropical;
DROP TABLE abraid_admin_0_1__simp_global;
DROP TABLE admin2013_1;
DROP TABLE admin2013_2;
DROP TABLE admin2013_0;

VACUUM ANALYZE;
