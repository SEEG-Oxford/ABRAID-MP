-- Script: roles.sql
--
-- Description: Creates roles for the ABRAID-MP database, for testing purposes only.
--
-- Copyright (c) 2014 University of Oxford

GRANT DELETE ON admin_unit_disease_extent_class TO :application_username;
GRANT UPDATE ON disease_occurrence_review TO :application_username;
