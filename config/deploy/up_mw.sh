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
if [[ ! -d "$ABRAID_SUPPORT_PATH/modelwrapper/repos/https_github_com_laurence_hudson_tessella_seegSDM_git_126ac02d0f87a06a3691f8c43fed14e2" ]]; then
  # Make an inital clone of the target repo
  # TODO Move to java-side context initialization.
  mkdir -p "$ABRAID_SUPPORT_PATH/modelwrapper/repos/"
  git clone "https://github.com/laurence-hudson-tessella/seegSDM.git" "$ABRAID_SUPPORT_PATH/modelwrapper/repos/https_github_com_laurence_hudson_tessella_seegSDM_git_126ac02d0f87a06a3691f8c43fed14e2"
fi

echo "[[ MW | Checking raster input files ]]"
dirAsk "$REMOTE_USER@${deploy_props[raster.source]}/" "$ABRAID_SUPPORT_PATH/modelwrapper/rasters"

echo "[[ MW | Checking covariate input files ]]"
if [[ -d "$ABRAID_SUPPORT_PATH/modelwrapper/covariates/" ]]; then
  fileAsk "$REMOTE_USER@${deploy_props[covariate.source]}/abraid.json" "$ABRAID_SUPPORT_PATH/modelwrapper/covariates/abraid.json" "covariate configuration"
fi
dirAsk "$REMOTE_USER@${deploy_props[covariate.source]}/" "$ABRAID_SUPPORT_PATH/modelwrapper/covariates" "covariate file"


echo "[[ MW | Ensuring correct file permissions ]]"
permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/modelwrapper/"

echo "[[ MW | Done ]]"
