#!/usr/bin/env bash
set -e

# Setup DB
cd ../../Database/
echo "shapefiles.path=$BASE/external/admin_units" > database.properties
echo "superuser.name=$PG_ADMIN_USER" >> database.properties
echo "application.username=$PG_ABRAID_USER" >> database.properties
echo "application.password=$PG_ABRAID_PASS" >> database.properties
echo "database.name=$DB_NAME" >> database.properties
ant create.database

# Create application users
cd $BASE/external/experts
echo "Importing experts"
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" -f import_into_abraid.sql
cd $BASE
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" --command "INSERT INTO expert (name, email, hashed_password, is_administrator) VALUES ( 'Dr Test', '$ABRAID_USER_EMAIL', '$ABRAID_USER_PASS', true )"

# Load historic healthmap data
cd $BASE/external/healthmap
echo "Importing historic healthmap data"
./import_into_abraid.sh "$PG_ADMIN_USER" "$DB_NAME"
cd $BASE

# Load disease extent
cd $BASE/external/disease_extent
echo "Importing disease extent"
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" -f import_into_abraid.sql
cd $BASE

# Load geonames data
echo "Importing geonames data"
cd $BASE/external/geonames
./import_geoname.sh "$PG_ADMIN_USER" "$DB_NAME"
cd $BASE

# Make a 500 most recent dengue points show in the validator
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" --command "update disease_occurrence set is_validated=false where id in (select id from disease_occurrence where disease_group_id=87 order by occurrence_date desc limit 500)"
