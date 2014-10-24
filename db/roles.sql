-- Script: roles.sql
--
-- Description: Creates roles for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Set up application role for ABRAID-MP (the application will log in under this username).
-- We need to create a temporary function because we need IF NOT EXISTS, and it cannot be an anonymous block
-- because we need to pass in psql variables.
CREATE OR REPLACE FUNCTION create_or_replace_role(p_username text, p_password text) RETURNS void AS $$
DECLARE
    suffix text := p_username || ' LOGIN PASSWORD ' || quote_literal(p_password);
BEGIN
    IF NOT EXISTS (SELECT * FROM pg_catalog.pg_user WHERE usename = p_username) THEN
        -- If the role does not exist, create it
        EXECUTE 'CREATE ROLE ' || suffix;
    ELSE
        -- If the role already exists, do not drop it because it may have privileges on other databases.
        -- Instead ensure its password is correct, then revoke all privileges from the current database only.
        EXECUTE 'ALTER ROLE ' || suffix;
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA public FROM ' || p_username;
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM ' || p_username;
    END IF;
END
$$ LANGUAGE plpgsql;

\pset footer off
SELECT create_or_replace_role(:'application_username', :'application_password');
DROP FUNCTION create_or_replace_role(text, text);

-- Privileges for the ABRAID-MP application: tables
GRANT SELECT, INSERT, UPDATE         ON admin_unit_disease_extent_class TO :application_username;
GRANT SELECT                         ON admin_unit_global TO :application_username;
GRANT SELECT                         ON admin_unit_qc TO :application_username;
GRANT SELECT, INSERT                 ON admin_unit_review TO :application_username;
GRANT SELECT                         ON admin_unit_tropical TO :application_username;
GRANT SELECT, INSERT                 ON alert TO :application_username;
GRANT SELECT                         ON country TO :application_username;
GRANT SELECT, INSERT                 ON covariate_influence TO :application_username;
GRANT SELECT, INSERT                 ON effect_curve_covariate_influence TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON disease_extent TO :application_username;
GRANT SELECT                         ON disease_extent_class TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON disease_group TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON disease_occurrence TO :application_username;
GRANT SELECT, INSERT                 ON disease_occurrence_review TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON expert TO :application_username;
GRANT SELECT, INSERT,         DELETE ON expert_validator_disease_group TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON feed TO :application_username;
GRANT SELECT, INSERT                 ON geoname TO :application_username;
GRANT SELECT                         ON geonames_location_precision TO :application_username;
GRANT SELECT                         ON healthmap_country TO :application_username;
GRANT SELECT                         ON healthmap_country_country TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON healthmap_disease TO :application_username;
GRANT SELECT                         ON land_sea_border TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON location TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON model_run TO :application_username;
GRANT SELECT, INSERT, UPDATE, DELETE ON persistent_logins TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON provenance TO :application_username;
GRANT SELECT, INSERT                 ON submodel_statistic TO :application_username;
GRANT SELECT, INSERT, UPDATE         ON validator_disease_group TO :application_username;

-- Privileges for the ABRAID-MP application: views
GRANT SELECT ON admin_unit_global_view TO :application_username;
GRANT SELECT ON admin_unit_simplified_global_view TO :application_username;
GRANT SELECT ON admin_unit_tropical_view TO :application_username;

-- Privileges for the ABRAID-MP application: sequences (one per column of type serial)
GRANT SELECT, UPDATE ON admin_unit_disease_extent_class_id_seq TO :application_username;
GRANT SELECT, UPDATE ON admin_unit_review_id_seq TO :application_username;
GRANT SELECT, UPDATE ON alert_id_seq TO :application_username;
GRANT SELECT, UPDATE ON covariate_influence_id_seq TO :application_username;
GRANT SELECT, UPDATE ON effect_curve_covariate_influence_id_seq TO :application_username;
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
