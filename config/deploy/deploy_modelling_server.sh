#!/usr/bin/env bash
echo "[[ Performing prechecks ]]"
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$BASE"

if [ "$(whoami)" != "root" ]; then
  echo "This script requires root permissions (sudo)"
  exit 1
fi

if [[ ! $# -eq 3 ]]; then
  echo "Usage: deploy_modelling_server.sh 'remote_user_name' 'remote_config_repo_path' 'remote_config_repo_branch'"
  exit 1
fi

# SSH keys
echo "[[ Enabling passwordless remote access ]]"
eval "$(ssh-agent)" > /dev/null
ssh-add "$HOME/.ssh/id_rsa" > /dev/null

# Fix bash files
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec dos2unix -q {} \;
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec chmod +x {} \;

# Export useful constants
export ABRAID_SUPPORT_PATH='/var/lib/abraid'
declare -r ABRAID_SUPPORT_PATH
export WEBAPP_PATH='/var/lib/tomcat7/webapps'
declare -r WEBAPP_PATH
export REMOTE_USER="$1"
declare -r REMOTE_USER
export CONFIG_PATH="$2"
declare -r CONFIG_PATH
export CONFIG_BRANCH="$3"
declare -r CONFIG_BRANCH

# Stop servlet containers
echo "[[ Stopping services ]]"
service nginx stop > /dev/null
service tomcat7 stop > /dev/null

# Source useful functions
source "functions.sh"

# Checking for dir
if [[ ! -d "$ABRAID_SUPPORT_PATH" ]]; then
  echo "[[ Creating support directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH"
  permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH"
fi

# Getting config
echo "[[ Updating ABRAID configuration ]]"
. up_config.sh "shared" "modelling"

# Upgrading modelwrapper
echo "[[ Upgrading modelwrapper ]]"
. up_mw.sh

# Bring services back up
echo "[[ Restarting services ]]"
service tomcat7 start > /dev/null
service nginx start > /dev/null

echo "[[ Done ]]"
echo "You may now delete the artifacts directory"