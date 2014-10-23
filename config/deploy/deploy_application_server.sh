#!/usr/bin/env bash
echo "[[ Performing prechecks ]]"
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASE

if [ "$(whoami)" != "root" ]; then
  echo "Needs sudo"
  exit 1
fi

# Fix bash files
find ../.. -name "*.sh" ! -name "up_app.sh" -exec dos2unix -q {} \;
find ../.. -name "*.sh" ! -name "up_app.sh" -exec chmod +x {} \;

echo "[[ Loading config ]]"
# Export useful constants
export ABRAID_SUPPORT_PATH='/var/lib/abraid'
# Source site specific variables
source site.conf.sh
# Source useful functions
source functions.sh

# Apply under-construction page
echo "[[ Applying under-construction page ]]"
rm -f /etc/nginx/sites-enabled/proxy
rm -f /etc/nginx/sites-enabled/maintenance
ln -s /etc/nginx/sites-available/maintenance /etc/nginx/sites-enabled/maintenance
service nginx restart > /dev/null

# Stop servlet containers
echo "[[ Stopping services ]]"
service tomcat7 stop > /dev/null
service gunicorn stop > /dev/null
echo -e "#\x21/bin/sh\n\n:" > /etc/cron.hourly/abraid

# Upgrading database
echo "[[ Upgrading database ]]"
# . up_c_db.sh

# Upgrading geoserver
echo "[[ Upgrading geoserver ]]"
# . up_c_geoserver.sh

# Upgrading modeloutput
echo "[[ Upgrading modeloutput ]]"
# . up_c_modeloutput.sh

# Upgrading publicsite
echo "[[ Upgrading publicsite ]]"
# . up_c_publicsite.sh

# Bring services back up
echo "[[ Restarting services ]]"
service gunicorn start > /dev/null
service tomcat7 start > /dev/null
echo -e "#\x21/bin/sh\n\n/var/lib/abraid/datamanager/datamanager.sh" > /etc/cron.hourly/abraid

# Remove under-construction page
echo "[[ Removing under-construction page ]]"
rm -f /etc/nginx/sites-enabled/proxy
rm -f /etc/nginx/sites-enabled/maintenance
ln -s /etc/nginx/sites-available/proxy /etc/nginx/sites-enabled/proxy
service nginx restart > /dev/null
