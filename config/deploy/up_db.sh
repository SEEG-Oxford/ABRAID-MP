#!/usr/bin/env bash
set -e

cd "../../Database/"

echo "[[ DB | Performing prechecks ]]"
: "${DB_NAME:?"Variable must be set"}"
: "${DB_ADDRESS:?"Variable must be set"}"
: "${DB_PORT:?"Variable must be set"}"
: "${PG_ADMIN_USER:?"Variable must be set"}"
: "${PG_ADMIN_PASS:?"Variable must be set"}"
: "${PG_ABRAID_USER:?"Variable must be set"}"


echo "[[ DB | Setting up temporary database authentication ]]"
echo "$DB_ADDRESS:$DB_PORT:postgres:$PG_ADMIN_USER:$PG_ADMIN_PASS" > "pg_pass"
echo "$DB_ADDRESS:$DB_PORT:$DB_NAME:$PG_ADMIN_USER:$PG_ADMIN_PASS" >> "pg_pass"
echo "$DB_ADDRESS:$DB_PORT:$DB_NAME:$PG_ABRAID_USER:$PG_ABRAID_PASS" >> "pg_pass"
export PGPASSFILE="$PWD/pg_pass"
chmod 0600 "pg_pass"

echo "[[ DB | Writing database setup properties ]]"
echo "superuser.name=$PG_ADMIN_USER" > "database.properties"
echo "application.username=$PG_ABRAID_USER" >> "database.properties"
echo "application.password=$PG_ABRAID_PASS" >> "database.properties"
echo "database.name=$DB_NAME" >> "database.properties"

if [[ 1 -eq 0 && $(psql -U postgres -l | grep "$DB_NAME" | wc -l) -eq 1 ]]; then
  # Temp bypass
  echo "[[ DB | Performing database upgrade (db.log) ]]"
  ant "create.database" > "../config/deploy/db.log"
else
  echo "[[ DB | Performing database creation checks ]]"
  : "${ABRAID_USER_EMAIL:?"Variable must be set"}"
  : "${ABRAID_USER_PASS:?"Variable must be set"}"
  : "${SHAPEFILE_SOURCE:?"Variable must be set"}"
  : "${HEALTHMAP_SOURCE:?"Variable must be set"}"
  : "${GEONAMES_SOURCE:?"Variable must be set"}"
  : "${EXPERTS_SOURCE:?"Variable must be set"}"
  : "${REVIEWS_SOURCE:?"Variable must be set"}"

  echo "[[ DB | Writing database creation properties ]]"
  echo "shapefiles.path=$PWD/external/admin_units" >> "database.properties"

  echo "[[ DB | Collating files for new database creation ]]"
  echo "Creating cache dir"
  mkdir -p "external"
  cd "external"
    echo "Getting admin unit data"
    rsync -crm "$SHAPEFILE_SOURCE/*" "./admin_units/" --include="*.shp" --include="*.dbf" --include="*.prj" --include="*.shx" --include="*.sbx" --include="*/" --exclude="*"
    echo "Getting initial healthmap data"
    rsync -crm "$HEALTHMAP_SOURCE/*" "./healthmap/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"
    echo "Getting initial geonames data"
    rsync -crm "$GEONAMES_SOURCE/*" "./geonames/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"
    echo "Getting initial user data"
    rsync -crm "$EXPERTS_SOURCE/*" "./experts/"
    echo "Getting initial review data"
    rsync -crm "$REVIEWS_SOURCE/*" "./reviews/" --exclude="export_from_abraid.sql"
  cd ..

  echo "[[ DB | Creating database (db.log) ]]"
  ant "create.database" > "../config/deploy/db.log"

  echo "[[ DB | Importing initial data ]]"
  cd "external"
    cd "experts"
      echo "Importing experts"
      psql -wq -v "ON_ERROR_STOP=1" -U "$PG_ADMIN_USER" -d "$DB_NAME" -f "import_into_abraid.sql" > /dev/null
    cd ".."
    cd "healthmap"
      echo "Importing historic healthmap data"
      psql -wq -v "ON_ERROR_STOP=1" -U "$PG_ADMIN_USER" -d "$DB_NAME" -f "import_into_abraid.sql" > /dev/null
    cd ".."
    cd "geonames"
      echo "Importing geonames data"
      psql -wq -v "ON_ERROR_STOP=1" -U "$PG_ADMIN_USER" -d "$DB_NAME" -f "import_into_abraid.sql" > /dev/null
    cd ..
    cd "reviews"
      echo "Importing historical reviews"
      psql -wq -v "ON_ERROR_STOP=1" -U "$PG_ADMIN_USER" -d "$DB_NAME" -f "import_into_abraid.sql" > /dev/null
    cd ".."
  cd ".."

  echo "[[ DB | Setting initial retrieval date ready for daily process ]]"
  psql -wq -v "ON_ERROR_STOP=1" -U "$PG_ADMIN_USER" -d "$DB_NAME" --command "UPDATE provenance SET last_retrieval_end_date = (select max(occurrence_date) from disease_occurrence) WHERE name = 'HealthMap';" > /dev/null
fi

echo "[[ DB | Performing cleanup ]]"
rm -f "pg_pass"
rm -f "database.properties"

echo "[[ DB | Done ]]"
cd "../config/deploy/"