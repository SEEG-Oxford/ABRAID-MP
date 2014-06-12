-- Script: testdata.sql
--
-- Description: Adds test data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy expert (name, email, hashed_password, is_administrator) FROM 'expert.txt' (ENCODING utf8, NULL '')
\copy location (id, name, geom, precision, geoname_id, resolution_weighting, healthmap_country_id, admin_unit_qc_gaul_code, admin_unit_global_gaul_code, admin_unit_tropical_gaul_code, has_passed_qc) FROM 'location.txt' (ENCODING utf8, NULL '')
\copy alert (id, feed_id, title, publication_date, url, summary, healthmap_alert_id) FROM 'alert.txt' (ENCODING utf8, NULL '')
\copy disease_occurrence (id, disease_group_id, location_id, alert_id, occurrence_date, is_validated, machine_weighting, validation_weighting) FROM 'disease_occurrence.txt' (ENCODING utf8, NULL '')
\copy expert_validator_disease_group (expert_id, validator_disease_group_id) FROM 'expert_validator_disease_group.txt' (ENCODING utf8, NULL '')
\copy admin_unit_disease_extent_class (disease_group_id, global_gaul_code, tropical_gaul_code, disease_extent_class, occurrence_count, has_class_changed) FROM 'admin_unit_disease_extent_class.txt' (ENCODING utf8, NULL '')

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('location_id_seq', (SELECT MAX(id) FROM location)) max_location_id;
SELECT setval('alert_id_seq', (SELECT MAX(id) FROM alert)) max_alert_id;
SELECT setval('disease_occurrence_id_seq', (SELECT MAX(id) FROM disease_occurrence)) max_disease_occurrence_id;

SET client_min_messages TO WARNING;
VACUUM ANALYZE;

-- To refresh the above data, use the following statements (these use subqueries to order the data, which aids difference comparisons):
-- \copy (select disease_group_id, global_gaul_code, tropical_gaul_code, disease_extent_class, occurrence_count, has_class_changed from admin_unit_disease_extent_class order by disease_group_id, global_gaul_code, tropical_gaul_code) TO 'admin_unit_disease_extent_class.txt' (ENCODING utf8, NULL '')
-- \copy (select id, feed_id, title, publication_date, url, summary, healthmap_alert_id from alert order by id) TO 'alert.txt' (ENCODING utf8, NULL '')
-- \copy (select id, disease_group_id, location_id, alert_id, occurrence_date, is_validated, machine_weighting, validation_weighting from disease_occurrence order by id) TO 'disease_occurrence.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name, geom, precision, geoname_id, resolution_weighting, healthmap_country_id, admin_unit_qc_gaul_code, admin_unit_global_gaul_code, admin_unit_tropical_gaul_code, country_gaul_code, has_passed_qc from location order by id) TO 'location.txt' (ENCODING utf8, NULL '')
