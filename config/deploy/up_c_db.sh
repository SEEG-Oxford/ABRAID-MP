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
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" --command "INSERT INTO expert (name, email, hashed_password, is_administrator, job_title, institution, is_seeg_member, visibility_requested, visibility_approved, weighting) VALUES ( 'Dr Test', '$ABRAID_USER_EMAIL', '$ABRAID_USER_PASS', true, 'Tester', 'Testland', true, false, false, 0 )"

# Load historic healthmap data
cd $BASE/external/healthmap
echo "Importing historic healthmap data"
./import_into_abraid.sh "$PG_ADMIN_USER" "$DB_NAME"
cd $BASE

# Load geonames data
echo "Importing geonames data"
cd $BASE/external/geonames
./import_geoname.sh "$PG_ADMIN_USER" "$DB_NAME"
cd $BASE

# Load historical reviews
cd $BASE/external/reviews
echo "Importing historic reviews"
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" -f import_into_abraid.sql
cd $BASE

# Make a 500 most recent dengue points show in the validator
#psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" --command "update disease_occurrence set is_validated=false where id in (select id from disease_occurrence where disease_group_id=87 order by occurrence_date desc limit 500)"

# Set last_retrieval_end_date ready for cron process
psql -wq -U "$PG_ABRAID_USER" -d "$DB_NAME" --command "UPDATE provenance SET last_retrieval_end_date = (select max(occurrence_date) from disease_occurrence) WHERE name = 'HealthMap';"