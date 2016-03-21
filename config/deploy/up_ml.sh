#!/usr/bin/env bash
set -e
cd "../../MachineLearning/"

if [[ ! -d "$ABRAID_SUPPORT_PATH/machinelearning/" ]]; then
  echo "[[ ML | Creating ml directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/machinelearning/"
fi

echo "[[ ML | Checking if update required ]]"
if [[ ! -z "$(rsync --dry-run -crmi --delete "." "$ABRAID_SUPPORT_PATH/machinelearning/" --include="*.py" --exclude "pickles/" --include="*/" --exclude="*")" ]]; then
  echo "[[ ML | Updating python files ]]"
  rsync -crm --delete "." "$ABRAID_SUPPORT_PATH/machinelearning/" --include="*.py" --exclude="pickles/" --include="*/" --exclude="*"
  echo "[[ ML | Removing old python byte-code ]]"
  find "$ABRAID_SUPPORT_PATH/machinelearning/" -name "*.pyc" -exec rm -f {} \;
else
  echo "[[ ML | No update required ]]"
fi

if [[ ! -d "$ABRAID_SUPPORT_PATH/machinelearning/pickles/" ]]; then
  echo "[[ ML | Creating pickles directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH/machinelearning/pickles/"
fi

echo "[[ ML | Ensuring correct file permissions ]]"
permissionFix "www-data" "www-data" "$ABRAID_SUPPORT_PATH/machinelearning/"

echo "[[ ML | Done ]]"
cd "../config/deploy/"
