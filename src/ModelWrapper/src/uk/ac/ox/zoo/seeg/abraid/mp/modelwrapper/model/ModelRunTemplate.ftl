<#--
    A templated run script for the ABRAID-MP disease risk model.
    Copyright (c) 2014 University of Oxford
-->
# A launch script for the ABRAID-MP disease risk model
# Copyright (c) 2014 University of Oxford

# Run as: $ R --no-save --quiet -f modelRun.R

# Run name = ${run}

# Model version = ${model_version}

# Set verbosity
verbose <- ${verbose?string("TRUE","FALSE")}

# Set max CPUs
max_cpus <- ${max_cpu?c}

# Set parallel execution
parallel_flag <- TRUE

# Set dry run
dry_run <- ${dry_run?string("TRUE","FALSE")}

# Define occurrence data
occurrence_path <- "${occurrence_file}"

# Define disease extent data
extent_path <- "${extent_file}"

# Define covariates to use.
# If you would like to use these covariate files please contact TBD@TBD.com, as we cannot release them in all circumstances.
covariate_paths <- c(
<#list covariate_files as covariate>
    "${covariate}"<#if covariate_has_next>,</#if>
</#list>
)

covariate_names <- c(
<#list covariate_names as covariate>
"${covariate}"<#if covariate_has_next>,</#if>
</#list>
)

# Define admin unit rasters to use.
# If you would like to use these admin unit rasters (or related shape files) please contact TBD@TBD.com, as we cannot release them in all circumstances.
admin1_path <- "${admin1_file}"
admin2_path <- "${admin2_file}"

# Set CRAN mirror
local({r <- getOption("repos")
    r["CRAN"] <- "http://cran.r-project.org"
    options(repos=r)
})

# Load devtools
if (!dry_run && !require('devtools', quietly=TRUE)) {
    install.packages('devtools', quiet=TRUE)
    library('devtools', quietly=TRUE)
}

# Load the model and its dependencies via devtools
# The full model is available from GitHub at https://github.com/SEEG-Oxford/seegSDM
if (!dry_run) {
    install_deps('model')
    load_all('model', recompile=TRUE)
}

# Define a function that can be used to load the model on cluster nodes
load_seegSDM <- function () {
    library('devtools', quietly=TRUE)
    load_all('model')
}

# Run the model
result <- tryCatch({
    innerResult <- -1
    if (!dry_run) {
        innerResult <- runABRAID(
            occurrence_path,
            extent_path,
            admin1_path,
            admin2_path,
            covariate_paths,
            rep(FALSE, length(covariate_paths)),
            verbose,
            max_cpus,
            load_seegSDM,
            parallel_flag)
    } else {
        # Create a small fake result set
        if (file.exists(extent_path)) {
            if (!require('raster', quietly=TRUE)) {
                install.packages('raster', quiet=TRUE)
                library('raster', quietly=TRUE)
            }
            dir.create('results')
            writeRaster(setExtent(raster(replicate(72, runif(29)), crs=crs("+proj=longlat +datum=WGS84 +no_defs")), extent(-180, 180, -60, 85)), filename="results/mean_prediction.tif", format="GTiff", NAflag=-9999, options=c("COMPRESS=DEFLATE","ZLEVEL=9"))
            writeRaster(setExtent(raster(replicate(72, runif(29)), crs=crs("+proj=longlat +datum=WGS84 +no_defs")), extent(-180, 180, -60, 85)), filename="results/prediction_uncertainty.tif", format="GTiff", NAflag=-9999, options=c("COMPRESS=DEFLATE","ZLEVEL=9"))
            fileConn <- file("results/statistics.csv")
            writeLines(c(
                '"deviance","rmse","kappa","auc","sens","spec","pcc","kappa_sd","auc_sd","sens_sd","spec_sd","pcc_sd","thresh"',
                '1,2,3,4,5,6,7,8,9,10,11,12,13'
            ), fileConn)
            close(fileConn)
            fileConn <- file("results/relative_influence.csv")
            writeLines(c(
                '"","mean","2.5%","97.5%"',
                '"1",2,3,4'
            ), fileConn)
            close(fileConn)
            fileConn <- file("results/effect_curves.csv")
            writeLines(c(
                '"","covariate","covariate","mean","2.5%","97.5%"',
                '"1","upr_u","0","-3","-5","0.3"'
            ), fileConn)
            close(fileConn)
        }
        innerResult <- 0
    }
    innerResult # return
}, error = function(e) {
    print(paste("Error:  ", e))
    return(1)
})

# Set exit code
print(paste("Exit code:  ", result))
quit(status=result)