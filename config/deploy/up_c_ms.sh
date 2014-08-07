#!/usr/bin/env bash
set -e

# Set up ModelOutputHandler
unzip ../../ABRAID-MP_ModelOutputHandler.war -d $MAIN_TC_PATH/modeloutput
# Configure
echo "jdbc.url=jdbc:postgresql://$DB_ADDRESS:$DB_PORT/$DB_NAME" > $MAIN_TC_PATH/modeloutput/WEB-INF/common-override.properties
echo "jdbc.username=$PG_ABRAID_USER" >> $MAIN_TC_PATH/modeloutput/WEB-INF/common-override.properties
echo "jdbc.password=$PG_ABRAID_PASS" >> $MAIN_TC_PATH/modeloutput/WEB-INF/common-override.properties
echo "modelwrapper.rootUrl=$MW_URL" >> $MAIN_TC_PATH/modeloutput/WEB-INF/common-override.properties

# Set up GeoServer
unzip external/geoserver.war -d $MAIN_TC_PATH/geoserver
# Purge demo geoserver config
rm -rf $MAIN_TC_PATH/geoserver/data/workspaces/* $MAIN_TC_PATH/geoserver/data/styles/* $MAIN_TC_PATH/geoserver/data/palettes/* $MAIN_TC_PATH/geoserver/data/layergroups/* $MAIN_TC_PATH/geoserver/data/data/* $MAIN_TC_PATH/geoserver/data/coverages/*
# Customize geoserver admin passwords
echo "$GEOSERVER_ROOT_PASSWORD" > $MAIN_TC_PATH/geoserver/data/security/masterpw/default/passwd
sed -i "s/password\=\".*\"/password=\"$GEOSERVER_ADMIN_PASSWORD\"/g" $MAIN_TC_PATH/geoserver/data/security/usergroup/default/users.xml
# Add abraid geoserver config
cp -r ../geoserver/abraid $MAIN_TC_PATH/geoserver/data/workspaces/abraid
sed -i "s/USER\_REPLACE/$PG_ABRAID_USER/g" $MAIN_TC_PATH/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/PW\_REPLACE/$PG_ABRAID_PASS/g" $MAIN_TC_PATH/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/DB\_REPLACE/$DB_NAME/g" $MAIN_TC_PATH/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/PORT\_REPLACE/$DB_PORT/g" $MAIN_TC_PATH/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/HOST\_REPLACE/$DB_ADDRESS/g" $MAIN_TC_PATH/geoserver/data/workspaces/abraid/abraid-db/datastore.xml

# Set up PublicSite
unzip ../../ABRAID-MP_PublicSite.war -d $MAIN_TC_PATH/ROOT
# Configure database
echo "jdbc.url=jdbc:postgresql://$DB_ADDRESS:$DB_PORT/$DB_NAME" > $MAIN_TC_PATH/ROOT/WEB-INF/common-override.properties
echo "jdbc.username=$PG_ABRAID_USER" >> $MAIN_TC_PATH/ROOT/WEB-INF/common-override.properties
echo "jdbc.password=$PG_ABRAID_PASS" >> $MAIN_TC_PATH/ROOT/WEB-INF/common-override.properties
echo "modelwrapper.rootUrl=$MW_URL" >> $MAIN_TC_PATH/ROOT/WEB-INF/common-override.properties
# Configure wms path
sed -i "s/http\:\/\/localhost\:8081\/geoserver\/abraid\/wms/$MAIN_URL\/wms/g" $MAIN_TC_PATH/ROOT/WEB-INF/freemarker/datavalidation/content.ftl

# Set up DataAcquisition (incomplete)
cp -r ../../DataAcquisition $ABRAID_SUPPORT_PATH/dataacquisition
rm $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.bat
# Configure
sed -i "s/jdbc\.username\=.*/jdbc.username=$PG_ABRAID_USER/g" $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.properties
sed -i "s/jdbc\.password\=.*/jdbc.password=$PG_ABRAID_PASS/g" $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.properties
sed -i "s/healthmap\.authorizationCode\=.*/healthmap.authorizationCode=$HEALTH_MAP_KEY/g" $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.properties
sed -i "s/geonames\.username\=.*/geonames.username=$GEONAMES_USER/g" $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.properties
sed -i "s/modelwrapper\.rootUrl\=.*/modelwrapper.rootUrl=$MW_URL/g" $ABRAID_SUPPORT_PATH/dataacquisition/dataacquisition.properties
# Set defaultStartDate?
sed -i "s|\${user\.home}\/ABRAID\-MP|$ABRAID_SUPPORT_PATH|g" $ABRAID_SUPPORT_PATH/dataacquisition/log4j.properties
mkdir -p $ABRAID_SUPPORT_PATH/dataacquisition/logs
# Cron job

