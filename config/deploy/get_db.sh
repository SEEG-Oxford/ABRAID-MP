#!/usr/bin/env bash
set -e

echo "[[ DB | Collate | Createing cache dir ]]"
mkdir -p external
cd external

# Shapefiles
echo "[[ DB | Collate | Getting admin unit data ]]"
rsync -crm "$SHAPEFILE_SOURCE" "./admin_units/" --include="*.shp" --include="*.dbf" --include="*.prj" --include="*.shx" --include="*.sbx" --include="*/" --exclude="*"

# Historic healthmap
echo "[[ DB | Collate | Getting intial healthmap data ]]"
rsync -crm "$HEALTHMAP_SOURCE" "./healthmap/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"

# Geonames
echo "[[ DB | Collate | Getting intial geonames data ]]"
rsync -crm "$GEONAMES_SOURCE" "./geonames/" --include="*.txt" --include="*.sql" --include="*/" --exclude="*"

# Experts
echo "[[ DB | Collate | Getting intial user data ]]"
rsync -crm "$EXPERTS_SOURCE" "./experts/"

# Reviews
echo "[[ DB | Collate | Getting intial review data ]]"
rsync -crm "$REVIEWS_SOURCE" "./geonames/" --exclude="export_from_abraid.sql"

echo "[[ DB | Collate | Done ]]"
cd ..
