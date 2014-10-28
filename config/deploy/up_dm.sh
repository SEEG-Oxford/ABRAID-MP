#!/usr/bin/env bash
set -e
cd "../../DataManager/"

if [[ ! -d "$ABRAID_SUPPORT_PATH/datamanager/" ]]; then
  echo "[[ DM | Creating datamanager directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/datamanager/"
fi

echo "[[ DM | Checking if update required ]]"
if [[ ! -z "$(rsync --dry-run -crmi --delete "." "$ABRAID_SUPPORT_PATH/datamanager/" --exclude="*.bat")" ]]; then
  echo "[[ DM | Updating files ]]"
  rsync -crm --delete "." "$ABRAID_SUPPORT_PATH/datamanager/" --exclude="*.bat"
else
  echo "[[ DM | No update required ]]"
fi

echo "[[ DM | Ensuring correct file permissions ]]"
permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/datamanager/"

echo "[[ DM | Done ]]"
cd "../config/deploy/"
