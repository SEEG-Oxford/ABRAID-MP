-- Add area to country (in square km). "COALESCE with 0" is for compatibility with "create.test.database", where some countries lack geoms.
-- Copyright (c) 2014 University of Oxford

ALTER TABLE country ADD COLUMN area double precision;
UPDATE country SET area=COALESCE(st_area(st_transform(geom, 3410))/1000000, 0);
ALTER TABLE country ALTER COLUMN area SET NOT NULL;