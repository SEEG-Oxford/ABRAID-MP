-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy provenance (id, name, default_feed_weighting) FROM 'provenance.txt' (ENCODING utf8)
\copy feed (provenance_id, name, weighting, healthmap_feed_id) FROM 'feed.txt' (ENCODING utf8)
\copy country (id, name) FROM 'country.txt' (ENCODING utf8)
\copy healthmap_country (id, name, country_id) FROM 'healthmapcountry.txt' (ENCODING utf8)
\copy disease_group (id, parent_id, name, group_type) FROM 'diseasegroup.txt' (ENCODING utf8)
\copy healthmap_disease (id, name, disease_group_id) FROM 'healthmapdisease.txt' (ENCODING utf8)
\copy geonames_location_precision (geonames_feature_code, location_precision) FROM 'geonameslocationprecision.txt' (ENCODING utf8)

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('disease_group_id_seq', (SELECT MAX(id) FROM disease_group)) max_disease_group_id;
SELECT setval('provenance_id_seq', (SELECT MAX(id) FROM provenance)) max_provenance_id;

VACUUM ANALYZE;
