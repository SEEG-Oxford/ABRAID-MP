-- Script: admin_units_testdata.sql
--
-- Description: Adds administrative unit test data to the ABRAID-MP database. This is done if the shapefiles are not found.
--
-- Copyright (c) 2014 University of Oxford

\copy admin_unit_tropical(gaul_code, parent_gaul_code, country_code, admin_level, name, display_name) FROM 'admin_unit_tropical.txt' (ENCODING utf8, NULL '')
\copy admin_unit_global(gaul_code, parent_gaul_code, country_code, admin_level, name, display_name) FROM 'admin_unit_global.txt' (ENCODING utf8, NULL '')
\copy admin_unit_simplified_tropical(gaul_code, name, display_name) FROM 'admin_unit_simplified_tropical.txt' (ENCODING utf8, NULL '')
\copy admin_unit_simplified_global(gaul_code, name, display_name) FROM 'admin_unit_simplified_global.txt' (ENCODING utf8, NULL '')
\copy admin_unit(gaul_code, parent_gaul_code, country_code, admin_level, name, centroid_latitude, centroid_longitude, area) FROM 'admin_unit.txt' (ENCODING utf8, NULL '')
\copy country(gaul_code, country_code, name) FROM 'country.txt' (ENCODING utf8, NULL '')

SET client_min_messages TO WARNING;
VACUUM ANALYZE;
