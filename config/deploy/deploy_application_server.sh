#!/usr/bin/env bash
echo "[[ Performing prechecks ]]"
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$BASE"

if [ "$(whoami)" != "root" ]; then
  echo "This script requires root permissions (sudo)"
  exit 1
fi

# Fix bash files
find ../.. -name "*.sh" ! -name "deploy_application_server.sh" -exec dos2unix -q {} \;
find ../.. -name "*.sh" ! -name "deploy_application_server.sh" -exec chmod +x {} \;

echo "[[ Loading config ]]"
# Export useful constants
export ABRAID_SUPPORT_PATH='/var/lib/abraid'
declare -r ABRAID_SUPPORT_PATH
# Source site specific variables
source "site.conf.sh"
# Source useful functions
source "functions.sh"

# Apply under-construction page
echo "[[ Applying under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/maintenance" "/etc/nginx/sites-enabled/maintenance"
service nginx restart > /dev/null

# Stop servlet containers
echo "[[ Stopping services ]]"
service tomcat7 stop > /dev/null
service gunicorn stop > /dev/null
echo -e "#\x21/bin/sh\n\n:" > "/etc/cron.hourly/abraid"

# Upgrading database
echo "[[ Upgrading database ]]"
. up_db.sh

# Upgrading machinelearning
echo "[[ Upgrading machinelearning ]]"
. up_ml.sh

# Upgrading geoserver
echo "[[ Upgrading geoserver ]]"
#. up_gs.sh

# Upgrading modeloutput
echo "[[ Upgrading modeloutput ]]"
# . up_mo.sh

# Upgrading publicsite
echo "[[ Upgrading publicsite ]]"
# . up_ps.sh

# Bring services back up
echo "[[ Restarting services ]]"
service gunicorn start > /dev/null
service tomcat7 start > /dev/null
echo -e "#\x21/bin/sh\n\n/var/lib/abraid/datamanager/datamanager.sh" > "/etc/cron.hourly/abraid"

# Remove under-construction page
echo "[[ Removing under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/proxy /etc/nginx/sites-enabled/proxy"
service nginx restart > /dev/null

echo "[[ Done ]]"
echo "You may now delete the artifacts directory"