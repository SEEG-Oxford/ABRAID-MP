#!/usr/bin/env bash
# Imports a shapefile into a Postgres database
# NB: A database table with the same name as the shapefile must already exist
# Usage: import_shapefile.sh <shapefile name> <database superuser name> <database name>

shp2pgsql -a -s 4326 -D -I -W LATIN1 "$1" | psql -U $2 -q -w -v ON_ERROR_STOP=ON $3
