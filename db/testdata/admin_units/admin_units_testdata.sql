-- Script: admin_units_testdata.sql
--
-- Description: Adds administrative unit test data to the ABRAID-MP database. This is done if the shapefiles are not found.
--
-- Copyright (c) 2014 University of Oxford

\copy admin_unit_tropical(gaul_code, level, name, pub_name, geom) FROM 'admin_unit_tropical.txt' (ENCODING utf8, NULL '')
\copy admin_unit_global(gaul_code, level, name, pub_name, geom) FROM 'admin_unit_global.txt' (ENCODING utf8, NULL '')
\copy admin_unit_qc(gaul_code, level, name, centr_lat, centr_lon, area) FROM 'admin_unit_qc.txt' (ENCODING utf8, NULL '')
\copy country(gaul_code, name, geom) FROM 'country.txt' (ENCODING utf8, NULL '')
\copy land_sea_border(id, geom) FROM 'land_sea_border.txt' (ENCODING utf8, NULL '')

SET client_min_messages TO WARNING;
VACUUM ANALYZE;

-- To refresh the above data, use the following statements (these use subqueries to order the data, which aids difference comparisons):
-- \copy (select gaul_code, level, name, pub_name, geom from admin_unit_tropical order by gaul_code) TO 'admin_unit_tropical.txt' (ENCODING utf8, NULL '')
-- \copy (select gaul_code, level, name, pub_name, geom from admin_unit_global order by gaul_code) TO 'admin_unit_global.txt' (ENCODING utf8, NULL '')
-- \copy (select gaul_code, level, name, centr_lat, centr_lon, area from admin_unit_qc order by gaul_code) TO 'admin_unit_qc.txt' (ENCODING utf8, NULL '')
-- \copy (select gaul_code, name, geom from country order by gaul_code) TO 'country.txt' (ENCODING utf8, NULL '')
-- \copy (select id, geom from land_sea_border order by id) TO 'land_sea_border.txt' (ENCODING utf8, NULL '')
