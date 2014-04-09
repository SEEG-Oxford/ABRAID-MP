@echo off
rem Imports a shapefile into a Postgres database
rem NB: A database table with the same name as the shapefile must already exist
rem Usage: import_shapefile.bat <shapefile name> <database superuser name> <database name>

set TEMPFILE=%TEMP%\temp.dmp
shp2pgsql -a -s 4326 -D -I -W LATIN1 "%1" > "%TEMPFILE%"
psql -U %2 -q -w -v ON_ERROR_STOP=ON %3 < "%TEMPFILE%"
del "%TEMPFILE%"
