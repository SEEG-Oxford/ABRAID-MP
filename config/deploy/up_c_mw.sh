#!/usr/bin/env bash
set -e

# Setup ModelWrapper
unzip ../../ABRAID-MP_ModelWrapper.war -d /var/lib/tomcat7/webapps/ROOT
# Configure
sed -i "s/auth\.username\=username/auth.username=$MODELWRAPPER_USER/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/modelwrapper.properties
sed -i "s/auth\.password\_hash\=.*/auth.password_hash=$MODELWRAPPER_HASH/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/modelwrapper.properties
sed -i "s/model\.output\.handler\.root\.url\=.*/model.output.handler.root.url=$MAIN_URL\/modeloutput/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/modelwrapper.properties
sed -i "s/\#\ model\.dry\.run\=true/model.dry.run=$MW_DRY_RUN/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/modelwrapper.properties
sed -i "s/\#\ model\.verbose\=true/model.verbose=true/g" /var/lib/tomcat7/webapps/ROOT/WEB-INF/modelwrapper.properties
# Add supporting files
mkdir $ABRAID_SUPPORT_PATH/modelwrapper
cp -r $BASE/external/rasters $ABRAID_SUPPORT_PATH/modelwrapper/rasters
cp -r $BASE/external/covariates $ABRAID_SUPPORT_PATH/modelwrapper/covariates

# Make an inital clone of the target repo
# This should probably be moved to java-side context initialization.
mkdir $ABRAID_SUPPORT_PATH/modelwrapper/repos/
git clone "https://github.com/laurence-hudson-tessella/seegSDM.git" "$ABRAID_SUPPORT_PATH/modelwrapper/repos/https_github_com_laurence_hudson_tessella_seegSDM_git_126ac02d0f87a06a3691f8c43fed14e2"

# R library directory
# Note this must be specified in /etc/default/R/Renvironment.site
mkdir -p $ABRAID_SUPPORT_PATH/r/libs