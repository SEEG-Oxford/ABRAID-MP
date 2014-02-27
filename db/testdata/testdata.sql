-- Script: testdata.sql
--
-- Description: Adds test data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Expert (Name, Email, HashedPassword, IsAdministrator) FROM 'expert.txt' (ENCODING utf8)
\copy Location (Id, Name, Geom, Precision, CountryId, GeoNamesId, GeoNamesFeatureCode) FROM 'location.txt' (ENCODING utf8)
\copy Alert (Id, FeedId, Title, PublicationDate, Url, Summary, HealthMapAlertId) FROM 'alert.txt' (ENCODING utf8)
\copy DiseaseOccurrence (Id, DiseaseGroupId, LocationId, AlertId, OccurrenceStartDate) FROM 'diseaseoccurrence.txt' (ENCODING utf8)

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('location_id_seq', (SELECT MAX(id) FROM Location)) max_location_id;
SELECT setval('alert_id_seq', (SELECT MAX(id) FROM Alert)) max_alert_id;
SELECT setval('diseaseoccurrence_id_seq', (SELECT MAX(id) FROM DiseaseOccurrence)) max_diseaseoccurrence_id;

VACUUM ANALYZE;
