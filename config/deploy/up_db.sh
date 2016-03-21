#!/usr/bin/env bash
set -e

cd "../../Database/"

echo "[[ DB | Loading configuration ]]"
TEMP_FILE=$(mktemp)
declare -A db_props
cat "$ABRAID_SUPPORT_PATH/conf/application/database.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  db_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
declare -A deploy_props
cat "$ABRAID_SUPPORT_PATH/conf/application/deployment.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  deploy_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
rm -f "$TEMP_FILE"

echo "[[ DB | Performing prechecks ]]"
: "${db_props[jdbc.database.name]:?"Variable must be set"}"
: "${db_props[jdbc.database.host]:?"Variable must be set"}"
: "${db_props[jdbc.database.port]:?"Variable must be set"}"
: "${db_props[jdbc.username]:?"Variable must be set"}"
: "${db_props[jdbc.password]:?"Variable must be set"}"
: "${deploy_props[database.root.password]:?"Variable must be set"}"

echo "[[ DB | Setting up temporary database authentication ]]"
echo "${db_props[jdbc.database.host]}:${db_props[jdbc.database.port]}:postgres:postgres:${deploy_props[database.root.password]}" > "pg_pass"
echo "${db_props[jdbc.database.host]}:${db_props[jdbc.database.port]}:${db_props[jdbc.database.name]}:postgres:${deploy_props[database.root.password]}" >> "pg_pass"
echo "${db_props[jdbc.database.host]}:${db_props[jdbc.database.port]}:${db_props[jdbc.database.name]}:${db_props[jdbc.username]}:${db_props[jdbc.password]}" >> "pg_pass"
export PGPASSFILE="$PWD/pg_pass"
chmod 0600 "pg_pass"

echo "[[ DB | Writing database setup properties ]]"
echo "superuser.name=postgres" > "database.properties"
echo "superuser.password=${deploy_props[database.root.password]}" >> "database.properties"
echo "application.username=${db_props[jdbc.username]}" >> "database.properties"
echo "application.password=${db_props[jdbc.password]}" >> "database.properties"
echo "database.name=${db_props[jdbc.database.name]}" >> "database.properties"

echo "[[ DB | Setting up backups ]]"
export DB_SNAPSHOT_CRON_SCRIPT_PATH='/etc/cron.daily/abraid_db'
declare -r DB_SNAPSHOT_CRON_SCRIPT_PATH
echo -e "#\x21/bin/sh\n\nsudo rm /opt/abraid_db.backup\nsudo PGPASSWORD=${deploy_props[database.root.password]} pg_dump -U postgres -d abraid_mp -f /opt/abraid_db.backup -F custom -Z 9" > "$DB_SNAPSHOT_CRON_SCRIPT_PATH"
chmod o+x "$DB_SNAPSHOT_CRON_SCRIPT_PATH"

if [[ $(psql -U postgres -l | grep "${db_props[jdbc.database.name]}" | wc -l) -eq 1 ]]; then
  # If a database exists with the correct name - upgrade it
  echo "[[ DB | Performing database upgrade (db.log) ]]"
  ant "upgrade.schema" > "../config/deploy/db.log"
else
  # If no database exists with the correct name - create it
  echo "[[ DB | Performing database creation checks ]]"
  : "${deploy_props[shapefile.source]:?"Variable must be set"}"

  echo "[[ DB | Writing database creation properties ]]"
  echo "shapefiles.path=$PWD/external/admin_units" >> "database.properties"

  echo "[[ DB | Collating files for new database creation ]]"
  echo "Creating cache dir"
  mkdir -p "external"
  cd "external"
    echo "Getting admin unit data"
    rsync -crm "$REMOTE_USER@${deploy_props[shapefile.source]}/*" "./admin_units/" --include="*.shp" --include="*.dbf" --include="*.prj" --include="*.shx" --include="*.sbx" --include="*/" --exclude="*"
  cd ..

  echo "[[ DB | Creating database (db.log) ]]"
  ant "create.database" > "../config/deploy/db.log"

  echo "[[ DB | Setting initial retrieval date ready for daily process ]]"
  psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" --command "UPDATE provenance SET last_retrieval_end_date = now() WHERE name = 'HealthMap';" > /dev/null
fi

echo "[[ DB | Performing cleanup ]]"
rm -f "pg_pass"
rm -f "database.properties"

echo "[[ DB | Done ]]"
cd "../config/deploy/"