#!/usr/bin/env bash
set -e

installWar "MO" "../../ABRAID-MP_ModelOutputHandler.war" "modeloutput"

if [[ ! -d "$ABRAID_SUPPORT_PATH/results/rasters" ]]; then
  echo "[[ MO | Creating raster output directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/results/rasters/"
  echo "[[ MO | Ensuring correct file permissions ]]"
  permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/results/"
fi

echo "[[ MO | Done ]]"
