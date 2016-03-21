#!/usr/bin/env bash
set -e
cd "../../DataManager/"

echo "[[ DM | Loading configuration ]]"
TEMP_FILE=$(mktemp)
declare -A deploy_props
cat "$ABRAID_SUPPORT_PATH/conf/application/deployment.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  deploy_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
rm -f "$TEMP_FILE"

if [[ ! -d "$ABRAID_SUPPORT_PATH/datamanager/" ]]; then
  echo "[[ DM | Creating datamanager directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/datamanager/"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/datamanager/logs/" ]]; then
  mkdir -p "$ABRAID_SUPPORT_PATH/datamanager/logs/"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/datamanager/logs/old/" ]]; then
  mkdir -p "$ABRAID_SUPPORT_PATH/datamanager/logs/old/"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/covariates/" ]]; then
  mkdir -p "$ABRAID_SUPPORT_PATH/covariates/"
fi

echo "[[ DM | Checking if update required ]]"
if [[ ! -z "$(rsync --dry-run -crmi --delete "." "$ABRAID_SUPPORT_PATH/datamanager/" --exclude="*.bat" --exclude="logs/")" ]]; then
  echo "[[ DM | Updating files ]]"
  sed -i "s|^log4j\.rootLogger\=.*$|log4j.rootLogger=ERROR, logfile|g" "log4j.properties"
  sed -i "s|^log4j\.appender\.logfile\.file\=.*$|log4j.appender.logfile.file=$ABRAID_SUPPORT_PATH/datamanager/logs/datamanager.log|g" "log4j.properties"
  rsync -crm --delete "." "$ABRAID_SUPPORT_PATH/datamanager/" --exclude="*.bat" --exclude="logs/"
else
  echo "[[ DM | No update required ]]"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/rasters/" ]]; then
  echo "[[ DM | Getting admin raster files ]]"
  dirAsk "$REMOTE_USER@${deploy_props[raster.source]}/" "$ABRAID_SUPPORT_PATH/rasters"
fi

# Git clone
REPO_DIR="https_github_com_SEEG_Oxford_seegSDM_git_cf0db3f50a25bea80468794281f22883"
if [[ ! -d "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR" ]]; then
  # Make an inital clone of the target repo
  # TODO Move to java-side context initialization.
  mkdir -p "$ABRAID_SUPPORT_PATH/repos/"
  git clone "https://github.com/SEEG-Oxford/seegSDM.git" "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR"
  permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR"
  ( cd "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR" && git config core.sharedRepository group )
  permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR"
else
  # Update the repo
  ( cd "$ABRAID_SUPPORT_PATH/repos/$REPO_DIR" && git pull origin master )
fi

echo "[[ DM | Ensuring correct file permissions ]]"
permissionFix "abraid" "abraid" "$ABRAID_SUPPORT_PATH/datamanager/"
chmod +x "$ABRAID_SUPPORT_PATH/datamanager/datamanager.sh"
permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/covariates/"
permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/rasters/"
permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/repos/"

echo "[[ DM | Done ]]"
cd "../config/deploy/"
