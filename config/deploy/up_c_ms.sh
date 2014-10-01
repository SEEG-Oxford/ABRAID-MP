#!/usr/bin/env bash
set -e

# Set up ModelOutputHandler
unzip ../../ABRAID-MP_ModelOutputHandler.war -d /var/lib/tomcat7/webapps/modeloutput
# Configure
echo "jdbc.url=jdbc:postgresql://$DB_ADDRESS:$DB_PORT/$DB_NAME" > /var/lib/tomcat7/webapps/modeloutput/WEB-INF/common-override.properties
echo "jdbc.username=$PG_ABRAID_USER" >> /var/lib/tomcat7/webapps/modeloutput/WEB-INF/common-override.properties
echo "jdbc.password=$PG_ABRAID_PASS" >> /var/lib/tomcat7/webapps/modeloutput/WEB-INF/common-override.properties
echo "model.wrapper.host=$MW_URL" >> /var/lib/tomcat7/webapps/modeloutput/WEB-INF/common-override.properties
echo "model.wrapper.path=/" >> /var/lib/tomcat7/webapps/modeloutput/WEB-INF/common-override.properties
# Configure log4j
sed -i "s/^log4j\.rootLogger\=.*$/log4j.rootLogger=ERROR, logfile, email/g" /var/lib/tomcat7/webapps/modeloutput/WEB-INF/classes/log4j.properties

# Set up GeoServer
unzip external/geoserver.war -d /var/lib/tomcat7/webapps/geoserver
# Purge demo geoserver config
rm -rf /var/lib/tomcat7/webapps/geoserver/data/workspaces/* /var/lib/tomcat7/webapps/geoserver/data/styles/* /var/lib/tomcat7/webapps/geoserver/data/palettes/* /var/lib/tomcat7/webapps/geoserver/data/layergroups/* /var/lib/tomcat7/webapps/geoserver/data/data/* /var/lib/tomcat7/webapps/geoserver/data/coverages/*
# Customize geoserver admin passwords
echo "$GEOSERVER_ROOT_PASSWORD" > /var/lib/tomcat7/webapps/geoserver/data/security/masterpw/default/passwd
sed -i "s/password\=\".*\"/password=\"$GEOSERVER_ADMIN_PASSWORD\"/g" /var/lib/tomcat7/webapps/geoserver/data/security/usergroup/default/users.xml
# Add abraid geoserver config
cp -r ../geoserver/abraid /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid
sed -i "s/USER\_REPLACE/$PG_ABRAID_USER/g" /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/PW\_REPLACE/$PG_ABRAID_PASS/g" /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/DB\_REPLACE/$DB_NAME/g" /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/PORT\_REPLACE/$DB_PORT/g" /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
sed -i "s/HOST\_REPLACE/$DB_ADDRESS/g" /var/lib/tomcat7/webapps/geoserver/data/workspaces/abraid/abraid-db/datastore.xml
cp ../geoserver/logging.xml /var/lib/tomcat7/webapps/geoserver/data/logging.xml
mkdir /var/lib/tomcat7/webapps/geoserver/data/logs
cp ../geoserver/ABRAID_LOGGING.properties /var/lib/tomcat7/webapps/geoserver/data/logs/ABRAID_LOGGING.properties

# Set up PublicSite
unzip ../../ABRAID-MP_PublicSite.war -d /var/lib/tomcat7/webapps/ROOT
# Configure database
echo "jdbc.url=jdbc:postgresql://$DB_ADDRESS:$DB_PORT/$DB_NAME" > /var/lib/tomcat7/webapps/ROOT/WEB-INF/common-override.properties
echo "jdbc.username=$PG_ABRAID_USER" >> /var/lib/tomcat7/webapps/ROOT/WEB-INF/common-override.properties
echo "jdbc.password=$PG_ABRAID_PASS" >> /var/lib/tomcat7/webapps/ROOT/WEB-INF/common-override.properties
echo "model.wrapper.host=$MW_URL" >> /var/lib/tomcat7/webapps/ROOT/WEB-INF/common-override.properties
echo "model.wrapper.path=/" >> /var/lib/tomcat7/webapps/ROOT/WEB-INF/common-override.properties
# Configure log4j
sed -i "s/^log4j\.rootLogger\=.*$/log4j.rootLogger=ERROR, stdout, logfile, email/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/classes/log4j.properties
# Configure wms path
sed -i "s|http\:\/\/localhost\:8081\/geoserver\/abraid\/wms|http\:\/\/$MAIN_URL\/wms|g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/freemarker/datavalidation/content.ftl

# Set up DataManager (incomplete)
cp -r ../../DataManager $ABRAID_SUPPORT_PATH/datamanager
rm $ABRAID_SUPPORT_PATH/datamanager/datamanager.bat
# Configure
sed -i "s/jdbc\.username\=.*/jdbc.username=$PG_ABRAID_USER/g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
sed -i "s/jdbc\.password\=.*/jdbc.password=$PG_ABRAID_PASS/g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
sed -i "s/healthmap\.authorizationCode\=.*/healthmap.authorizationCode=$HEALTH_MAP_KEY/g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
sed -i "s/geonames\.username\=.*/geonames.username=$GEONAMES_USER/g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
sed -i "s/model\.wrapper\.host\=.*/model.wrapper.host=$MW_URL/g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
sed -i "s/model\.wrapper\.path\=.*/model.wrapper.path=\//g" $ABRAID_SUPPORT_PATH/datamanager/datamanager.properties
# Configure log4j
sed -i "s/^log4j\.rootLogger\=.*$/log4j.rootLogger=ERROR, logfile, email/g" $ABRAID_SUPPORT_PATH/datamanager/log4j.properties
sed -i "s|\${user\.home}\/ABRAID\-MP|$ABRAID_SUPPORT_PATH|g" $ABRAID_SUPPORT_PATH/datamanager/log4j.properties
mkdir -p $ABRAID_SUPPORT_PATH/datamanager/logs
mkdir -p $ABRAID_SUPPORT_PATH/datamanager/logs/old
# Cron job

