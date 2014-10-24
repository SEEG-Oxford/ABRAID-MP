#!/usr/bin/env bash
set -e
GS_TEMP_DIR="$(mktemp -d)"
GS_UPDATE_CMD="fileAsk"

setupTempConfigFiles() {
  echo "$GEOSERVER_ROOT_PASSWORD" > "$GS_TEMP_DIR/passwd"
  cp "$WEBAPP_PATH/geoserver/data/security/usergroup/default/users.xml" "$GS_TEMP_DIR/users.xml"
  sed -i "s|password=\".*\"|password=\"$GEOSERVER_ADMIN_PASSWORD\"|g" "$GS_TEMP_DIR/users.xml"
}

setupTempWorkspaceFiles() {
  cp -r "../geoserver/abraid" "$GS_TEMP_DIR/workspace"
  sed -i "s/USER\_REPLACE/$PG_ABRAID_USER/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/PW\_REPLACE/$PG_ABRAID_PASS/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/DB\_REPLACE/$DB_NAME/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/PORT\_REPLACE/$DB_PORT/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/HOST\_REPLACE/$DB_ADDRESS/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
}

echo "[[ GS | Checking for existing GeoServer installation ]]"
if [[ ! -d "$WEBAPP_PATH/geoserver" ]]; then
  echo "No GeoServer install found"
  echo "[[ GS | Downloading GeoServer 2.5.1 ]]"
  curl -L "http://sourceforge.net/projects/geoserver/files/GeoServer/2.5.1/geoserver-2.5.1-war.zip" > "$GS_TEMP_DIR/geoserver-2.5.1-war.zip"
  unzip -p "geoserver-2.5.1-war.zip" "geoserver.war" > "$GS_TEMP_DIR/geoserver.war"
  rm -f "geoserver-2.5.1-war.zip"

  echo "[[ GS | Installing GeoServer 2.5.1 ]]"
  unzip "$GS_TEMP_DIR/geoserver.war" -d "$WEBAPP_PATH/geoserver"

  echo "[[ GS | Removing default setup ]]"
  rm -rf "$WEBAPP_PATH/geoserver/data/workspaces/*"
  rm -rf "$WEBAPP_PATH/geoserver/data/styles/*"
  rm -rf "$WEBAPP_PATH/geoserver/data/palettes/*"
  rm -rf "$WEBAPP_PATH/geoserver/data/layergroups/*"
  rm -rf "$WEBAPP_PATH/geoserver/data/data/*"
  rm -rf "$WEBAPP_PATH/geoserver/data/coverages/*"

  GS_UPDATE_CMD="fileCopy"
else
  echo "There appears to be appears be an existing GeoServer install"
  echo "[[ GS | Skipping GeoServer installation ]]"
  echo "Don't forget you may need to update your GeoServer version (manually)"
fi

echo "[[ GS | Customizing/checking geoserver config ]]"
setupTempConfigFiles
$GS_UPDATE_CMD "$GS_TEMP_DIR/passwd" "$WEBAPP_PATH/geoserver/data/security/masterpw/default/passwd" "GeoServer root password"
$GS_UPDATE_CMD "$GS_TEMP_DIR/users.xml" "$WEBAPP_PATH/geoserver/data/security/usergroup/default/users.xml" "GeoServer admin password"
$GS_UPDATE_CMD "../geoserver/logging.xml" "$WEBAPP_PATH/geoserver/data/logging.xml" "GeoServer logging config"
$GS_UPDATE_CMD "../geoserver/ABRAID_LOGGING.properties" "$WEBAPP_PATH/geoserver/data/logs/ABRAID_LOGGING.properties" "ABRAID GeoServer logging settings"
$GS_UPDATE_CMD "../geoserver/gwc-gs.xml" "$WEBAPP_PATH/geoserver/data/gwc-gs.xml" "GeoServer geo-web-cache config"

echo "[[ GS | Adding/checking the abraid workspace ]]"
setupTempWorkspaceFiles
{ cd "$GS_TEMP_DIR/workspace" && find . -type "f" -exec "$GS_UPDATE_CMD" "$GS_TEMP_DIR/workspace" "$WEBAPP_PATH/geoserver/data/workspaces/abraid/{}" \; ; }

echo "[[ GS | Ensuring correct file permissions ]]"
chown -R tomcat7:tomcat7 "$WEBAPP_PATH/geoserver/"
chmod -R 664 "$WEBAPP_PATH/geoserver/"
find "$WEBAPP_PATH/geoserver/" -type d -exec chmod +x {} \;

echo "[[ GS | Done ]]"
rm -rf "$GS_TEMP_DIR"
cd "../config/deploy/"
