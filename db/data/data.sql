-- Script: data.sql
--
-- Description: Adds default data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Provenance (Name, HealthMapFeedId) FROM 'provenance.txt' (ENCODING utf8)
\copy Disease (Name, HealthMapDiseaseId) FROM 'disease.txt' (ENCODING utf8)
\copy Country (Id, Name) FROM 'country.txt' (ENCODING utf8)
\copy Expert (Name, Email, HashedPassword, IsAdministrator) FROM 'expert.txt' (ENCODING utf8)
