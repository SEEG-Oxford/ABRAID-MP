-- Script: roles.sql
--
-- Description: Creates roles for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- The ABRAID-MP application will log in using this username and password
DROP ROLE IF EXISTS :application_username;
CREATE ROLE :application_username LOGIN PASSWORD :'application_password';

-- Privileges for the ABRAID-MP application: tables
GRANT SELECT, INSERT                ON Alert TO :application_username;
GRANT SELECT                        ON Country TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON DiseaseGroup TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON DiseaseOccurrence TO :application_username;
GRANT SELECT, INSERT                ON DiseaseOccurrenceReview TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON Expert TO :application_username;
GRANT SELECT, INSERT,        DELETE ON ExpertDiseaseGroup TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON Feed TO :application_username;
GRANT SELECT                        ON GeoNamesLocationPrecision TO :application_username;
GRANT SELECT                        ON HealthMapCountry TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON HealthMapDisease TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON Location TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON Provenance TO :application_username;

-- Privileges for the ABRAID-MP application: sequences (one per column of type serial)
GRANT SELECT, UPDATE ON alert_id_seq TO :application_username;
GRANT SELECT, UPDATE ON diseasegroup_id_seq TO :application_username;
GRANT SELECT, UPDATE ON diseaseoccurrence_id_seq TO :application_username;
GRANT SELECT, UPDATE ON diseaseoccurrencereview_id_seq TO :application_username;
GRANT SELECT, UPDATE ON expert_id_seq TO :application_username;
GRANT SELECT, UPDATE ON feed_id_seq TO :application_username;
GRANT SELECT, UPDATE ON location_id_seq TO :application_username;
GRANT SELECT, UPDATE ON provenance_id_seq TO :application_username;
