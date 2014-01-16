-- Script: create_database.sql
--
-- Description: Creates (or recreates) the ABRAID-MP database itself. Notes:
-- * Must be run as a user which is able to create databases
-- * Expects parameter "database_name" to contain the database name
--
-- Copyright (c) 2014 University of Oxford


-- Drop and create the database
DROP DATABASE IF EXISTS :database_name;
CREATE DATABASE :database_name;

-- For later: Create a user that has minimal privileges, for use by the Java application

-- Change context to the newly-created database
\c :database_name

-- Enable PostGIS
CREATE EXTENSION postgis;

-- Create schema objects
\i tables.sql
\i constraints.sql
\i indexes.sql

-- Create default data
\cd data
\i data.sql
VACUUM ANALYZE;
