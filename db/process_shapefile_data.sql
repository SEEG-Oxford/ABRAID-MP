-- Script: process_shapefile_tables.sql
--
-- Description: Perform additional operations on shapefile tables after they have been populated.
--
-- Copyright (c) 2014 University of Oxford

-- Maximum distance from centroid = square root of area + 10%
UPDATE admin_unit
SET max_centr_distance = SQRT(area) * 1.1;
