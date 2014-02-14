-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Provenance (Id, Name) FROM 'provenance.txt' (ENCODING utf8)
\copy Feed (ProvenanceId, Name, Weight, HealthMapFeedId) FROM 'feed.txt' (ENCODING utf8)
\copy Country (Id, Name) FROM 'country.txt' (ENCODING utf8)
\copy HealthMapCountry (Id, Name, CountryId) FROM 'healthmapcountry.txt' (ENCODING utf8)
\copy HealthMapDisease (Id, Name, IsOfInterest) FROM 'healthmapdisease.txt' (ENCODING utf8)

-- The provenance data contains explicit values of ID, which is of type serial. This is so that the feed
-- data can refer to the appropriate provenance row. So now we need to reset the provenance ID sequence.
SELECT setval('provenance_id_seq', (SELECT MAX(id) FROM Provenance));

VACUUM ANALYZE;
