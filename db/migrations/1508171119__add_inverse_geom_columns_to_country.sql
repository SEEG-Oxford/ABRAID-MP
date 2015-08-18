-- Add inverse geoms to country tables (for masking).
-- Copyright (c) 2015 University of Oxford

ALTER TABLE country ADD COLUMN inverse_geom geometry(MULTIPOLYGON, 4326);

UPDATE country SET inverse_geom = ST_Multi(St_SymDifference(geom, ST_GeomFromText('POLYGON ((-180 -60, -180 85, 180 85, 180 -60, -180 -60))', 4326)));

