-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Provenance (Id, Name) FROM 'provenance.txt' (ENCODING utf8)
\copy Feed (ProvenanceId, Name, Weight, HealthMapFeedId) FROM 'feed.txt' (ENCODING utf8)
\copy Country (Id, Name) FROM 'country.txt' (ENCODING utf8)
\copy HealthMapCountry (Id, Name, CountryId) FROM 'healthmapcountry.txt' (ENCODING utf8)
\copy DiseaseGroup (Id, ParentId, Name, GroupType) FROM 'diseasegroup.txt' (ENCODING utf8)
\copy HealthMapDisease (Id, Name, IsOfInterest, DiseaseGroupId) FROM 'healthmapdisease.txt' (ENCODING utf8)

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
SELECT setval('diseasegroup_id_seq', (SELECT MAX(id) FROM DiseaseGroup));
SELECT setval('provenance_id_seq', (SELECT MAX(id) FROM Provenance));

VACUUM ANALYZE;
