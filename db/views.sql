-- Script: views.sql
--
-- Description: Creates views for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


CREATE VIEW admin_unit_simplified_global_view (
    simplified_geom
)
AS
SELECT simplified_geom
FROM admin_unit_global;
