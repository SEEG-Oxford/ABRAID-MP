#!/usr/bin/env bash
set -e

echo "[[ MW | Loading configuration ]]"
TEMP_FILE=$(mktemp)
declare -A deploy_props
cat "$ABRAID_SUPPORT_PATH/conf/modelling/deployment.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  deploy_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
rm -f "$TEMP_FILE"

# Install war
installWar "MW" "../../ABRAID-MP_ModelWrapper.war" "${deploy_props[modelwrapper.install.dir]}"

# R library directory
# Note this must be specified in /etc/default/R/Renvironment.site
if [[ ! -d "$ABRAID_SUPPORT_PATH/r/libs/" ]]; then
  mkdir -p "$ABRAID_SUPPORT_PATH/r/libs/"
  permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/r/"
fi

# Git clone
REPO_DIR="https_github_com_SEEG_Oxford_seegSDM_git_cf0db3f50a25bea80468794281f22883"
if [[ ! -d "$ABRAID_SUPPORT_PATH/modelwrapper/repos/$REPO_DIR" ]]; then
  # Make an inital clone of the target repo
  # TODO Move to java-side context initialization.
  mkdir -p "$ABRAID_SUPPORT_PATH/modelwrapper/repos/"
  git clone "https://github.com/SEEG-Oxford/seegSDM.git" "$ABRAID_SUPPORT_PATH/modelwrapper/repos/$REPO_DIR"
else
  # Update the repo
  ( cd "$ABRAID_SUPPORT_PATH/modelwrapper/repos/$REPO_DIR" && git pull origin master )
fi

echo "[[ MW | Checking raster input files ]]"
dirAsk "$REMOTE_USER@${deploy_props[raster.source]}/" "$ABRAID_SUPPORT_PATH/modelwrapper/rasters"

echo "[[ MW | Ensuring correct file permissions ]]"
permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/modelwrapper/"

echo "[[ MW | Done ]]"
