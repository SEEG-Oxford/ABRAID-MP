-- Add area to country (in square km). "COALESCE with 0" is for compatibility with "create.test.database", where some countries lack geoms.
-- Copyright (c) 2014 University of Oxford

-- Add Zhi's recommend area calculation projection (http://epsg.io/54009)
INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values ( 54009, 'ESRI', 54009, '+proj=moll +lon_0=0 +x_0=0 +y_0=0 +datum=WGS84 +units=m +no_defs ', 'PROJCS["World_Mollweide",GEOGCS["GCS_WGS_1984",DATUM["WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]],PROJECTION["Mollweide"],PARAMETER["False_Easting",0],PARAMETER["False_Northing",0],PARAMETER["Central_Meridian",0],UNIT["Meter",1],AUTHORITY["EPSG","54009"]]');

ALTER TABLE country ADD COLUMN area double precision;
UPDATE country SET area=COALESCE(st_area(st_transform(geom, 54009))/1000000, 0);
ALTER TABLE country ALTER COLUMN area SET NOT NULL;