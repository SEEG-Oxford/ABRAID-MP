#!/usr/bin/env bash
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASE

source $ABRAID_DEPLOYMENT_CONFIG_FILE

rm -rf external
mkdir external
cd external

# Geoserver
curl -L http://sourceforge.net/projects/geoserver/files/GeoServer/2.5.1/geoserver-2.5.1-war.zip > geoserver-2.5.1-war.zip
unzip -p geoserver-2.5.1-war.zip geoserver.war > geoserver.war
rm geoserver-2.5.1-war.zip

# Shapefiles
mkdir admin_units
scp -C $SHAPEFILE_SOURCE/admin_unit_qc.* admin_units/
scp -C $SHAPEFILE_SOURCE/admin_unit_global.* admin_units/
scp -C $SHAPEFILE_SOURCE/admin_unit_simplified_global.* admin_units/
scp -C $SHAPEFILE_SOURCE/admin_unit_tropical.* admin_units/
scp -C $SHAPEFILE_SOURCE/admin_unit_simplified_tropical.* admin_units/
scp -C $SHAPEFILE_SOURCE/country.* admin_units/
scp -C $SHAPEFILE_SOURCE/land_sea_border.* admin_units/

# Rasters
mkdir rasters
scp -C $RASTER_SOURCE/admin1qc.tif rasters/
scp -C $RASTER_SOURCE/admin1qc.tif rasters/
scp -C $RASTER_SOURCE/admin_tropical.tif rasters/
scp -C $RASTER_SOURCE/admin_global.tif rasters/

# Covariates
mkdir covariates
scp -C -r $COVARIATE_SOURCE/* covariates/

# Historic healthmap
mkdir healthmap
scp -C $HEALTHMAP_SOURCE/admin_unit_disease_extent_class.txt healthmap/
scp -C $HEALTHMAP_SOURCE/alert.txt healthmap/
scp -C $HEALTHMAP_SOURCE/disease_group.txt healthmap/
scp -C $HEALTHMAP_SOURCE/disease_occurrence.txt healthmap/
scp -C $HEALTHMAP_SOURCE/healthmap_disease.txt healthmap/
scp -C $HEALTHMAP_SOURCE/location.txt healthmap/
scp -C $HEALTHMAP_SOURCE/import_into_abraid.sh healthmap/
scp -C $HEALTHMAP_SOURCE/import_into_abraid.sql healthmap/

# Geonames
mkdir geonames
scp -C $GEONAMES_SOURCE/import_geoname.sh geonames/
scp -C $GEONAMES_SOURCE/geoname.txt geonames/
