-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy admin_unit_country (admin_unit_gaul_code, country_gaul_code) FROM 'admin_unit_country.txt' (ENCODING utf8, NULL '')
\copy provenance (id, name, default_feed_weighting) FROM 'provenance.txt' (ENCODING utf8, NULL '')
\copy feed (provenance_id, name, weighting, language, healthmap_feed_id) FROM 'feed.txt' (ENCODING utf8, NULL '')
\copy healthmap_country (id, name) FROM 'healthmap_country.txt' (ENCODING utf8, NULL '')
\copy healthmap_country_country (healthmap_country_id, gaul_code) FROM 'healthmap_country_country.txt' (ENCODING utf8, NULL '')
\copy validator_disease_group (id, name) FROM 'validator_disease_group.txt' (ENCODING utf8, NULL '')
\copy disease_group (id, parent_id, name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id, weighting, automatic_model_runs, min_new_occurrences_trigger, min_data_volume, min_distinct_countries, high_frequency_threshold, min_high_frequency_countries, occurs_in_africa) FROM 'disease_group.txt' (ENCODING utf8, NULL '')
\copy disease_extent (disease_group_id, min_validation_weighting, min_occurrences_for_presence, min_occurrences_for_possible_presence, max_months_ago_for_higher_occurrence_score, lower_occurrence_score, higher_occurrence_score) FROM 'disease_extent.txt' (ENCODING utf8, NULL '')
\copy healthmap_disease (id, name, disease_group_id) FROM 'healthmap_disease.txt' (ENCODING utf8, NULL '')
\copy geonames_location_precision (geonames_feature_code, location_precision) FROM 'geonames_location_precision.txt' (ENCODING utf8, NULL '')
\copy disease_extent_class (name, weighting, distance_if_within_extent) FROM 'disease_extent_class.txt' (ENCODING utf8, NULL '')

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('validator_disease_group_id_seq', (SELECT MAX(id) FROM validator_disease_group)) max_validator_disease_group_id;
SELECT setval('disease_group_id_seq', (SELECT MAX(id) FROM disease_group)) max_disease_group_id;
SELECT setval('provenance_id_seq', (SELECT MAX(id) FROM provenance)) max_provenance_id;

SET client_min_messages TO WARNING;
VACUUM ANALYZE;

-- To refresh the above data, use the following statements (these use subqueries to order the data, which aids difference comparisons):
-- \copy (select admin_unit_gaul_code, country_gaul_code from admin_unit_country order by admin_unit_gaul_code) TO 'admin_unit_country.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name, default_feed_weighting from provenance order by id) TO 'provenance.txt' (ENCODING utf8, NULL '')
-- \copy (select provenance_id, name, weighting, language, healthmap_feed_id from feed order by id) TO 'feed.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name from healthmap_country order by id) TO 'healthmap_country.txt' (ENCODING utf8, NULL '')
-- \copy (select healthmap_country_id, gaul_code from healthmap_country_country order by id) TO 'healthmap_country_country.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name from validator_disease_group order by id) TO 'validator_disease_group.txt' (ENCODING utf8, NULL '')
-- \copy (select id, parent_id, name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id, weighting, automatic_model_runs, min_new_occurrences_trigger, min_data_volume, min_distinct_countries, high_frequency_threshold, min_high_frequency_countries, occurs_in_africa from disease_group order by id) TO 'disease_group.txt' (ENCODING utf8, NULL '')
-- \copy (select id, name, disease_group_id from healthmap_disease order by id) TO 'healthmap_disease.txt' (ENCODING utf8, NULL '')
-- \copy (select geonames_feature_code, location_precision from geonames_location_precision order by id) TO 'geonames_location_precision.txt' (ENCODING utf8, NULL '')
-- \copy (select name, weighting, distance_if_within_extent from disease_extent_class order by id) TO 'disease_extent_class.txt' (ENCODING utf8, NULL '')
