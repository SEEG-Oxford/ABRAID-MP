-- Script: views.sql
--
-- Description: Creates views for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Used by GeoServer to display the global simplified geometries as a base layer.
CREATE VIEW admin_unit_simplified_global_view
AS
SELECT simplified_geom
FROM admin_unit_global;


-- The views admin_unit_global_view and admin_unit_tropical_view link the rows in the admin_unit_global and
-- admin_unit_tropical tables to their parent countries (if any), using the admin_unit_country table.
-- SELECT * is considered the right solution in this case because we always want all of the columns from the
-- underlying tables.
CREATE VIEW admin_unit_global_view
AS
SELECT g.*, c.country_gaul_code
FROM admin_unit_global g
LEFT OUTER JOIN admin_unit_country c on g.gaul_code = c.admin_unit_gaul_code;


CREATE VIEW admin_unit_tropical_view
AS
SELECT t.*, c.country_gaul_code
FROM admin_unit_tropical t
LEFT OUTER JOIN admin_unit_country c on t.gaul_code = c.admin_unit_gaul_code;
