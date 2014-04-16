-- Script: admin_units_testdata.sql
--
-- Description: Adds administrative unit test data to the ABRAID-MP database. This is done if the shapefiles are not found.
--
-- Copyright (c) 2014 University of Oxford

\copy admin_unit_tropical(gaul_code, level, name, pub_name) FROM 'admin_unit_tropical.txt' (ENCODING utf8, NULL '')
\copy admin_unit_global(gaul_code, level, name, pub_name) FROM 'admin_unit_global.txt' (ENCODING utf8, NULL '')
\copy admin_unit_simplified_tropical(gaul_code, name, pub_name) FROM 'admin_unit_simplified_tropical.txt' (ENCODING utf8, NULL '')
\copy admin_unit_simplified_global(gaul_code, name, pub_name) FROM 'admin_unit_simplified_global.txt' (ENCODING utf8, NULL '')
\copy admin_unit(gaul_code, level, name, centr_lat, centr_lon, area) FROM 'admin_unit.txt' (ENCODING utf8, NULL '')
\copy country(gaul_code, name) FROM 'country.txt' (ENCODING utf8, NULL '')
\copy land_sea_border(id, geom) FROM 'land_sea_border.txt' (ENCODING utf8, NULL '')

SET client_min_messages TO WARNING;
VACUUM ANALYZE;
