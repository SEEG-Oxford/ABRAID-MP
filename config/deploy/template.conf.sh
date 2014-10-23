# ABRAID source site specific deployment configuration variables
# '/' char must always be escaped
export DB_NAME=''
export DB_ADDRESS='' # relative to "main server"
export DB_PORT=''
export PG_ADMIN_USER=''
export PG_ADMIN_PASS=''
export PG_ABRAID_USER=''
export PG_ABRAID_PASS=''
export ABRAID_USER_EMAIL=''
export ABRAID_USER_PASS='' # This should be specifed as a bcrypt hash
export MODELWRAPPER_USER=''
export MODELWRAPPER_HASH='' # This should be specifed as a bcrypt hash
export GEOSERVER_ROOT_PASSWORD='' # This should be specifed in the geoserver encrypted style
export GEOSERVER_ADMIN_PASSWORD='' # This should be specifed as a digest in the geoserver style, i.e. 'digest1:\/hash'
export GEONAMES_USER=''
export HEALTH_MAP_KEY=''

export ABRAID_SUPPORT_PATH=''

export MW_URL=''
export MAIN_URL=''

export SHAPEFILE_SOURCE=''
export RASTER_SOURCE=''
export COVARIATE_SOURCE=''
export HEALTHMAP_SOURCE=''
export GEONAMES_SOURCE=''
export EXPERTS_SOURCE=''
export REVIEWS_SOURCE=''