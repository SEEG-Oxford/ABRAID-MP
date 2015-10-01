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

echo "[[ MW | Ensuring correct file permissions ]]"
if [[ ! -d "$ABRAID_SUPPORT_PATH/modelwrapper/" ]]; then
  mkdir -p "$ABRAID_SUPPORT_PATH/modelwrapper/"
fi
permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/modelwrapper/"

echo "[[ MW | Done ]]"
