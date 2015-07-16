-- Add model run disease occurrence view for geoserver.
--
-- Copyright (c) 2015 University of Oxford

CREATE OR REPLACE VIEW admin_unit_full_global_view AS 
 SELECT admin_unit_global.geom
   FROM admin_unit_global;

ALTER TABLE admin_unit_full_global_view
  OWNER TO postgres;
GRANT ALL ON TABLE admin_unit_full_global_view TO postgres;
GRANT SELECT ON TABLE admin_unit_full_global_view TO abraid_mp_application;
