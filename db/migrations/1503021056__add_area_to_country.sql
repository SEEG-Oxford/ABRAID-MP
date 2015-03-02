-- Add area to country (in square km).
-- Copyright (c) 2014 University of Oxford

ALTER TABLE country ADD COLUMN area double precision;
UPDATE country SET area=st_area(st_transform(geom, 3410))/1000000;
ALTER TABLE country ALTER COLUMN area SET NOT NULL;