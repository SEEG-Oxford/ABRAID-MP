-- Script: testdata.sql
--
-- Description: Adds test data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

-- Firstly clear the disease_extent table, as it was already populated by data.sql
TRUNCATE TABLE disease_extent CASCADE;

\copy expert (name, email, hashed_password, job_title, institution, is_administrator, is_seeg_member, has_seen_help_text, weighting, visibility_requested, visibility_approved) FROM 'expert.txt' (ENCODING utf8, NULL '')
\copy location (id, name, geom, precision, geoname_id, resolution_weighting, healthmap_country_id, admin_unit_qc_gaul_code, admin_unit_global_gaul_code, admin_unit_tropical_gaul_code, country_gaul_code, has_passed_qc, model_eligible) FROM 'location.txt' (ENCODING utf8, NULL '')
\copy alert (id, feed_id, title, publication_date, url, summary, healthmap_alert_id) FROM 'alert.txt' (ENCODING utf8, NULL '')
\copy disease_occurrence (id, disease_group_id, location_id, alert_id, status, occurrence_date, machine_weighting, validation_weighting, final_weighting, final_weighting_excl_spatial) FROM 'disease_occurrence.txt' (ENCODING utf8, NULL '')
\copy disease_extent (disease_group_id, geom, min_validation_weighting, max_months_ago_for_higher_occurrence_score, lower_occurrence_score, higher_occurrence_score) FROM 'disease_extent.txt' (ENCODING utf8, NULL '')
\copy expert_validator_disease_group (expert_id, validator_disease_group_id) FROM 'expert_validator_disease_group.txt' (ENCODING utf8, NULL '')
\copy admin_unit_disease_extent_class (disease_group_id, global_gaul_code, tropical_gaul_code, disease_extent_class, validator_disease_extent_class, validator_occurrence_count, class_changed_date) FROM 'admin_unit_disease_extent_class.txt' (ENCODING utf8, NULL '')

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
-- \copy (select disease_group_id, global_gaul_code, tropical_gaul_code, disease_extent_class, occurrence_count from admin_unit_disease_extent_class order by disease_group_id, global_gaul_code, tropical_gaul_code) TO 'admin_unit_disease_extent_class.txt' (ENCODING utf8, NULL '')
-- \copy (select id, feed_id, title, publication_date, url, summary, healthmap_alert_id from alert order by id) TO 'alert.txt' (ENCODING utf8, NULL '')
-- \copy (select id, disease_group_id, location_id, alert_id, status, occurrence_date, machine_weighting, validation_weighting, final_weighting, final_weighting_excl_spatial from disease_occurrence order by id) TO 'disease_occurrence.txt' (ENCODING utf8, NULL '')
-- \copy (select disease_group_id, geom, min_validation_weighting, max_months_ago_for_higher_occurrence_score, lower_occurrence_score, higher_occurrence_score from disease_extent order by disease_group_id) TO 'disease_extent.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name, geom, precision, geoname_id, resolution_weighting, healthmap_country_id, admin_unit_qc_gaul_code, admin_unit_global_gaul_code, admin_unit_tropical_gaul_code, country_gaul_code, has_passed_qc from location order by id) TO 'location.txt' (ENCODING utf8, NULL '')