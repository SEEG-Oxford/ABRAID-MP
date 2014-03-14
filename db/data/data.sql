-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Provenance (Id, Name, DefaultFeedWeighting) FROM 'provenance.txt' (ENCODING utf8)
\copy Feed (ProvenanceId, Name, Weighting, HealthMapFeedId) FROM 'feed.txt' (ENCODING utf8)
\copy Country (Id, Name) FROM 'country.txt' (ENCODING utf8)
\copy HealthMapCountry (Id, Name, CountryId) FROM 'healthmapcountry.txt' (ENCODING utf8)
\copy DiseaseGroup (Id, ParentId, Name, GroupType) FROM 'diseasegroup.txt' (ENCODING utf8)
\copy HealthMapDisease (Id, Name, DiseaseGroupId) FROM 'healthmapdisease.txt' (ENCODING utf8)
\copy GeoNamesLocationPrecision (GeoNamesFeatureCode, LocationPrecision) FROM 'geonameslocationprecision.txt' (ENCODING utf8)

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('diseasegroup_id_seq', (SELECT MAX(id) FROM DiseaseGroup)) max_diseasegroup_id;
SELECT setval('provenance_id_seq', (SELECT MAX(id) FROM Provenance)) max_provenance_id;

VACUUM ANALYZE;
