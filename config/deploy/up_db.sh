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

if [[ $(psql -U postgres -l | grep "${db_props[jdbc.database.name]}" | wc -l) -eq 1 ]]; then
  # If a database exists with the correct name - upgrade it
  echo "[[ DB | Performing database upgrade (db.log) ]]"
  ant "upgrade.schema" > "../config/deploy/db.log"
else
  # If no database exists with the correct name - create it
  echo "[[ DB | Performing database creation checks ]]"
  : "${deploy_props[shapefile.source]:?"Variable must be set"}"
  : "${deploy_props[healthmap.source]:?"Variable must be set"}"
  : "${deploy_props[geonames.source]:?"Variable must be set"}"
  : "${deploy_props[experts.source]:?"Variable must be set"}"
  : "${deploy_props[reviews.source]:?"Variable must be set"}"

  echo "[[ DB | Writing database creation properties ]]"
  echo "shapefiles.path=$PWD/external/admin_units" >> "database.properties"

  echo "[[ DB | Collating files for new database creation ]]"
  echo "Creating cache dir"
  mkdir -p "external"
  cd "external"
    echo "Getting admin unit data"
    rsync -crm "$REMOTE_USER@${deploy_props[shapefile.source]}/*" "./admin_units/" --include="*.shp" --include="*.dbf" --include="*.prj" --include="*.shx" --include="*.sbx" --include="*/" --exclude="*"
    echo "Getting initial healthmap data"
    rsync -crm "$REMOTE_USER@${deploy_props[healthmap.source]}/*" "./healthmap/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"
    echo "Getting initial geonames data"
    rsync -crm "$REMOTE_USER@${deploy_props[geonames.source]}/*" "./geonames/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"
    echo "Getting initial review data"
    rsync -crm "$REMOTE_USER@${deploy_props[reviews.source]}/*" "./reviews/" --exclude="export_from_abraid.sql"
  cd ..

  echo "[[ DB | Creating database (db.log) ]]"
  ant "create.database" > "../config/deploy/db.log"

  echo "[[ DB | Importing initial data ]]"
  cd "external"
    cd "experts"
      echo "Importing experts"
      psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" -f "import_into_abraid.sql" > /dev/null
    cd ".."
    cd "healthmap"
      echo "Importing historic healthmap data"
      psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" -f "import_into_abraid.sql" > /dev/null
    cd ".."
    cd "geonames"
      echo "Importing geonames data"
      psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" -f "import_into_abraid.sql" > /dev/null
    cd ..
    cd "reviews"
      echo "Importing historical reviews and experts"
      psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" -f "import_into_abraid.sql" > /dev/null
    cd ".."
  cd ".."

  echo "[[ DB | Setting initial retrieval date ready for daily process ]]"
  psql -wq -v "ON_ERROR_STOP=1" -U "postgres" -d "${db_props[jdbc.database.name]}" --command "UPDATE provenance SET last_retrieval_end_date = (select max(occurrence_date) from disease_occurrence) WHERE name = 'HealthMap';" > /dev/null
fi

echo "[[ DB | Performing cleanup ]]"
rm -f "pg_pass"
rm -f "database.properties"

echo "[[ DB | Done ]]"
cd "../config/deploy/"