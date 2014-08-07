#!/usr/bin/env bash
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASE

if [ "$(whoami)" != "root" ]; then
    echo "Needs sudo"
    exit 1
fi

# Fix bash files
find ../.. -name "*.sh" -exec dos2unix {} \;
find ../.. -name "*.sh" -exec chmod +x {} \;

# Source site specific variables
source $ABRAID_DEPLOYMENT_CONFIG_FILE

: ${DB_NAME:?"Variable must be set"}
: ${DB_ADDRESS:?"Variable must be set"}
: ${DB_PORT:?"Variable must be set"}
: ${PG_ADMIN_USER:?"Variable must be set"}
: ${PG_ADMIN_PASS:?"Variable must be set"}
: ${PG_ABRAID_USER:?"Variable must be set"}
: ${PG_ABRAID_PASS:?"Variable must be set"}
: ${ABRAID_USER_EMAIL:?"Variable must be set"}
: ${ABRAID_USER_PASS:?"Variable must be set"}
: ${MODELWRAPPER_USER:?"Variable must be set"}
: ${MODELWRAPPER_HASH:?"Variable must be set"}
: ${GEOSERVER_ROOT_PASSWORD:?"Variable must be set"}
: ${GEOSERVER_ADMIN_PASSWORD:?"Variable must be set"}
: ${GEONAMES_USER:?"Variable must be set"}
: ${HEALTH_MAP_KEY:?"Variable must be set"}
: ${MW_DRY_RUN:?"Variable must be set"}
: ${ABRAID_SUPPORT_PATH:?"Variable must be set"}
: ${MAIN_TC_PATH:?"Variable must be set"}
: ${MW_TC_PATH:?"Variable must be set"}
: ${MAIN_TC_SERVICE:?"Variable must be set"}
: ${MW_TC_SERVICE:?"Variable must be set"}
: ${MW_URL:?"Variable must be set"}
: ${MAIN_URL:?"Variable must be set"}
: ${SHAPEFILE_SOURCE:?"Variable must be set"}
: ${RASTER_SOURCE:?"Variable must be set"}
: ${COVARIATE_SOURCE:?"Variable must be set"}
: ${HEALTHMAP_SOURCE:?"Variable must be set"}
: ${GEONAMES_SOURCE:?"Variable must be set"}

# Setup psql authentication
echo "$DB_ADDRESS:$DB_PORT:postgres:$PG_ADMIN_USER:$PG_ADMIN_PASS" > $BASE/pg_pass
echo "$DB_ADDRESS:$DB_PORT:$DB_NAME:$PG_ADMIN_USER:$PG_ADMIN_PASS" >> $BASE/pg_pass
echo "$DB_ADDRESS:$DB_PORT:$DB_NAME:$PG_ABRAID_USER:$PG_ABRAID_PASS" >> $BASE/pg_pass
export PGPASSFILE=$BASE/pg_pass
chmod 0600 $BASE/pg_pass

# Stop servlet containers
service $MAIN_TC_SERVICE stop
service $MW_TC_SERVICE stop

# Teardown
rm -rf $MAIN_TC_PATH/*
rm -rf $MW_TC_PATH/*
rm -rf $ABRAID_SUPPORT_PATH
dropdb -U $PG_ADMIN_USER "$DB_NAME"

# Setup support dir
mkdir $ABRAID_SUPPORT_PATH

# Setup database
. up_c_db.sh

# Setup main server
. up_c_ms.sh

# Setup model wrapper
. up_c_mw.sh

# Permissions
chown -R tomcat7:tomcat7 $MAIN_TC_PATH/*
chown -R tomcat7:tomcat7 $MW_TC_PATH/*
chown -R tomcat7:tomcat7 $ABRAID_SUPPORT_PATH/*
chmod -R 664 $MAIN_TC_PATH/*
chmod -R 664 $MW_TC_PATH/*
chmod -R 664 $ABRAID_SUPPORT_PATH/*
find $MAIN_TC_PATH/ -type d -exec chmod +x {} \;
find $MW_TC_PATH/ -type d -exec chmod +x {} \;
find $ABRAID_SUPPORT_PATH/ -type d -exec chmod +x {} \;
chmod o+x $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.sh

# Clean up psql authentication
rm $BASE/pg_pass

# Bring services back up
service $MAIN_TC_SERVICE start
service $MW_TC_SERVICE start