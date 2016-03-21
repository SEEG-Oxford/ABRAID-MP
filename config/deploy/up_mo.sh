#!/usr/bin/env bash
set -e

installWar "MO" "../../ABRAID-MP_ModelOutputHandler.war" "modeloutput"

if [[ ! -d "$ABRAID_SUPPORT_PATH/results/rasters" ]]; then
  echo "[[ MO | Creating raster output directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/results/rasters/"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/modeloutput" ]]; then
  echo "[[ MO | Creating model output support directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/modeloutput/"
fi

if [[ -f "$ABRAID_SUPPORT_PATH/modeloutput/waterbodies.tif"]]; then
  echo "[[ MW | Getting waterbodies mask raster file ]]"
  fileAsk "$REMOTE_USER@${deploy_props[waterbodies.source]}/waterbodies.tif" "$ABRAID_SUPPORT_PATH/modeloutput/waterbodies.tif" "waterbodies mask raster"
fi

echo "[[ MO | Ensuring correct file permissions ]]"
permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/results/"
permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH/modeloutput/"

echo "[[ MO | Done ]]"
