-- Script: roles.sql
--
-- Description: Creates roles for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- The ABRAID-MP application will log in using this username and password
DROP ROLE IF EXISTS :application_username;
CREATE ROLE :application_username LOGIN PASSWORD :'application_password';

-- Privileges for the ABRAID-MP application: tables
GRANT SELECT, INSERT, UPDATE        ON admin_unit_disease_extent_class TO :application_username;
GRANT SELECT                        ON admin_unit_global TO :application_username;
GRANT SELECT                        ON admin_unit_qc TO :application_username;
GRANT SELECT, INSERT                ON admin_unit_review TO :application_username;
GRANT SELECT                        ON admin_unit_tropical TO :application_username;
GRANT SELECT, INSERT                ON alert TO :application_username;
GRANT SELECT                        ON country TO :application_username;
GRANT SELECT, INSERT				ON covariate_influence TO :application_username;
GRANT SELECT, INSERT,        DELETE ON disease_extent TO :application_username;
GRANT SELECT                        ON disease_extent_class TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON disease_group TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON disease_occurrence TO :application_username;
GRANT SELECT, INSERT                ON disease_occurrence_review TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON expert TO :application_username;
GRANT SELECT, INSERT,        DELETE ON expert_validator_disease_group TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON feed TO :application_username;
GRANT SELECT, INSERT                ON geoname TO :application_username;
GRANT SELECT                        ON geonames_location_precision TO :application_username;
GRANT SELECT                        ON healthmap_country TO :application_username;
GRANT SELECT                        ON healthmap_country_country TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON healthmap_disease TO :application_username;
GRANT SELECT                        ON land_sea_border TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON location TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON model_run TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON provenance TO :application_username;
GRANT SELECT, INSERT				ON submodel_statistic TO :application_username;
GRANT SELECT, INSERT, UPDATE        ON validator_disease_group TO :application_username;

-- Privileges for the ABRAID-MP application: views
GRANT SELECT ON admin_unit_global_view TO :application_username;
GRANT SELECT ON admin_unit_simplified_global_view TO :application_username;
GRANT SELECT ON admin_unit_tropical_view TO :application_username;

-- Privileges for the ABRAID-MP application: sequences (one per column of type serial)
GRANT SELECT, UPDATE ON admin_unit_disease_extent_class_id_seq TO :application_username;
GRANT SELECT, UPDATE ON admin_unit_review_id_seq TO :application_username;
GRANT SELECT, UPDATE ON alert_id_seq TO :application_username;
GRANT SELECT, UPDATE ON covariate_influence_id_seq TO :application_username;
GRANT SELECT, UPDATE ON disease_group_id_seq TO :application_username;
GRANT SELECT, UPDATE ON disease_occurrence_id_seq TO :application_username;
GRANT SELECT, UPDATE ON disease_occurrence_review_id_seq TO :application_username;
GRANT SELECT, UPDATE ON expert_id_seq TO :application_username;
GRANT SELECT, UPDATE ON feed_id_seq TO :application_username;
GRANT SELECT, UPDATE ON location_id_seq TO :application_username;
GRANT SELECT, UPDATE ON model_run_id_seq TO :application_username;
GRANT SELECT, UPDATE ON provenance_id_seq TO :application_username;
GRANT SELECT, UPDATE ON submodel_statistic_id_seq TO :application_username;
GRANT SELECT, UPDATE ON validator_disease_group_id_seq TO :application_username;
