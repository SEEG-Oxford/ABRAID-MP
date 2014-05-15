-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy provenance (id, name, default_feed_weighting) FROM 'provenance.txt' (ENCODING utf8, NULL '')
\copy feed (provenance_id, name, weighting, language, healthmap_feed_id) FROM 'feed.txt' (ENCODING utf8, NULL '')
\copy healthmap_country (id, name) FROM 'healthmap_country.txt' (ENCODING utf8, NULL '')
\copy healthmap_country_country (healthmap_country_id, gaul_code) FROM 'healthmap_country_country.txt' (ENCODING utf8, NULL '')
\copy validator_disease_group (id, name) FROM 'validator_disease_group.txt' (ENCODING utf8, NULL '')
\copy disease_group (id, parent_id, name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id) FROM 'disease_group.txt' (ENCODING utf8, NULL '')
\copy healthmap_disease (id, name, disease_group_id) FROM 'healthmap_disease.txt' (ENCODING utf8, NULL '')
\copy geonames_location_precision (geonames_feature_code, location_precision) FROM 'geonames_location_precision.txt' (ENCODING utf8, NULL '')
\copy disease_extent_class (name, weighting) FROM 'disease_extent_class.txt' (ENCODING utf8, NULL '')

-- Set the initial weighting of the disease groups, according to their type.
UPDATE disease_group SET weighting = 1 WHERE group_type = 'SINGLE';
UPDATE disease_group SET weighting = 0.5 WHERE group_type = 'MICROCLUSTER';
UPDATE disease_group SET weighting = 0 WHERE group_type = 'CLUSTER';

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
