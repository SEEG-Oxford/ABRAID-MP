#!/usr/bin/env bash
set -e

# Setup ModelWrapper
unzip ../../ABRAID-MP_ModelWrapper.war -d $MW_TC_PATH/modelwrapper
# Configure
sed -i "s/auth\.username\=username/auth.username=$MODELWRAPPER_USER/g" $MW_TC_PATH/modelwrapper/WEB-INF/modelwrapper.properties
sed -i "s/auth\.password\_hash\=.*/auth.password_hash=$MODELWRAPPER_HASH/g" $MW_TC_PATH/modelwrapper/WEB-INF/modelwrapper.properties
sed -i "s/model\.output\.handler\.root\.url\=.*/model.output.handler.root.url=$MAIN_URL\/modeloutput/g" $MW_TC_PATH/modelwrapper/WEB-INF/modelwrapper.properties
sed -i "s/\#\ model\.dry\.run\=true/model.dry.run=true/g" $MW_TC_PATH/modelwrapper/WEB-INF/modelwrapper.properties
# Add supporting files
mkdir $ABRAID_SUPPORT_PATH/modelwrapper
cp -r $BASE/external/rasters $ABRAID_SUPPORT_PATH/modelwrapper/rasters
cp -r $BASE/external/covariates $ABRAID_SUPPORT_PATH/modelwrapper/covariates

# R library directory
# Note this must be specified in /etc/default/R/Renvironment.site
mkdir -p $ABRAID_SUPPORT_PATH/r/libs