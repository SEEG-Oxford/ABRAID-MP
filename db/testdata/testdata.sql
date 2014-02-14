-- Script: testdata.sql
--
-- Description: Adds test data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy Expert (Name, Email, HashedPassword, IsAdministrator) FROM 'expert.txt' (ENCODING utf8)

VACUUM ANALYZE;
