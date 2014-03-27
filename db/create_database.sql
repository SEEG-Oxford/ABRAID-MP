-- Script: create_database.sql
--
-- Description: Creates (or recreates) the ABRAID-MP database itself. Must be run as a user that is able to create databases.
--
-- Parameters:
--      database_name: The ABRAID-MP database name
--      application_username: The ABRAID-MP application's username
--      application_password: The ABRAID-MP application's password
--
-- Copyright (c) 2014 University of Oxford


-- Drop and create the database
DROP DATABASE IF EXISTS :database_name;
CREATE DATABASE :database_name;

-- Change context to the newly-created database
\c :database_name

-- Enable PostGIS
CREATE EXTENSION postgis;

-- Create schema objects
\i tables.sql
\i constraints.sql
\i indexes.sql
\i roles.sql
