-- Fix the gaul code of the canada shape in global.
--
-- Copyright (c) 2015 University of Oxford
INSERT INTO admin_unit_tropical (gaul_code, level, name, pub_name, geom, simplified_geom) SELECT 46, level, name, pub_name, geom, simplified_geom FROM admin_unit_tropical WHERE gaul_code=825;
UPDATE admin_unit_review SET tropical_gaul_code=46 WHERE tropical_gaul_code=825;
UPDATE model_run_admin_unit_disease_extent_class SET tropical_gaul_code=46 WHERE tropical_gaul_code=825;
UPDATE admin_unit_disease_extent_class SET tropical_gaul_code=46 WHERE tropical_gaul_code=825;
UPDATE location SET admin_unit_tropical_gaul_code=46 WHERE admin_unit_tropical_gaul_code=825;
DELETE FROM admin_unit_tropical WHERE gaul_code=825;