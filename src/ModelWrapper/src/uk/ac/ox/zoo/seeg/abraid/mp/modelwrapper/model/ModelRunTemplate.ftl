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

# Define occurrence data
occurrence_path <- "${occurrence_file}"

# Define disease extent data
extent_path <- "${extent_file}"

# Define covariates to use.
# If you would like to use these covariate files please contact abraid@zoo.ox.ac.uk, as we cannot release them in all circumstances.
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
# If you would like to use these admin unit rasters (or related shape files) please contact abraid@zoo.ox.ac.uk, as we cannot release them in all circumstances.
admin1_path <- "${admin1_file}"
admin2_path <- "${admin2_file}"

# Create a temp dir for intermediate rasters
dir.create('temp')

# Set CRAN mirror
local({r <- getOption("repos")
    r["CRAN"] <- "http://cran.r-project.org"
    options(repos=r)
})

<#if !dry_run>
# Load devtools
if (!require('devtools', quietly=TRUE)) {
    install.packages('devtools', quiet=TRUE)
    library('devtools', quietly=TRUE)
}

# Load the model and its dependencies via devtools
# The full model is available from GitHub at https://github.com/SEEG-Oxford/seegSDM
install_deps('model')
load_all('model', recompile=TRUE)
rasterOptions(tmpdir="temp")
</#if>

# Define a function that can be used to load the model on cluster nodes
load_seegSDM <- function () {
    library('devtools', quietly=TRUE)
    load_all('model')
    rasterOptions(tmpdir="temp")
}

<#if dry_run>
# Define a function to create an output raster for model dry runs
create_dry_run_raster <- function(suffix, output_path) {
    # Get disease abbreviation (working directory prefix before underscore)
    install.packages('stringr', quiet=TRUE)
    library('stringr', quietly=TRUE)
    disease_abbreviation <- str_extract(basename(getwd()),"^[^_]*")

    # Get files in the dry run outputs directory that are predefined output rasters for the specified disease and suffix
    # e.g. "mal_2014-11-18-18-02-04_c7a1950d-ecd0-4720-8edd-4432cdcae08a_mean.tif" matches if "mal" is the disease
    # abbreviation and "mean" is the suffix
    predefined_raster_file_pattern <- paste(disease_abbreviation, "_.*", suffix, "\\.tif", sep = "")
    predefined_raster_files <- list.files(path="../dry_run_outputs/", pattern=predefined_raster_file_pattern, full.names=TRUE)

    if (length(predefined_raster_files) >= 1) {
        # Predefined output raster found, so copy it to the desired output path
        file.copy(predefined_raster_files[1], output_path)
    } else {
        # Predefined output raster not found, so generate a raster with random pixels
        writeRaster(setExtent(raster(replicate(72, runif(29)), crs=crs("+proj=longlat +datum=WGS84 +no_defs")), extent(-180, 180, -60, 85)),
                    filename=output_path, format="GTiff", NAflag=-9999, options=c("COMPRESS=DEFLATE","ZLEVEL=9"))
    }
}

do_dry_run <- function() {
    # Skip dry run on travis
    if (!is.na(Sys.getenv("CONTINUOUS_INTEGRATION", unset = NA))) {
        return()
    }
    # Create a small fake result set
    if (!require('rgdal', quietly=TRUE)) {
        install.packages('rgdal', quiet=TRUE)
        library('rgdal', quietly=TRUE)
    }
    if (!require('raster', quietly=TRUE)) {
        install.packages('raster', quiet=TRUE)
        library('raster', quietly=TRUE)
    }
    rasterOptions(tmpdir="temp")
    dir.create('results')
    create_dry_run_raster("mean", "results/mean_prediction.tif")
    create_dry_run_raster("uncertainty", "results/prediction_uncertainty.tif")
    create_dry_run_raster("extent", "results/extent.tif")
    fileConn <- file("results/statistics.csv")
    writeLines(c(
        '"deviance","rmse","kappa","auc","sens","spec","pcc","kappa_sd","auc_sd","sens_sd","spec_sd","pcc_sd","thresh"',
        '1,2,3,4,5,6,7,8,9,10,11,12,13'
    ), fileConn)
    close(fileConn)
    fileConn <- file("results/relative_influence.csv")
    writeLines(c(
        '"","file_path","covariate","mean","2.5%","97.5%"',
        '"X","path/upr_u.tif","GRUMP urban surface","1","2","3"'
    ), fileConn)
    close(fileConn)
    fileConn <- file("results/effect_curves.csv")
    writeLines(c(
        '"","file_path","covariate","covariate","mean","2.5%","97.5%"',
        '"1","path/upr_u.tif","GRUMP urban surface","0","-3","-5","0.3"'
    ), fileConn)
    close(fileConn)
}
</#if>

# Run the model
result <- tryCatch({
    innerResult <- -1
    <#if dry_run>
    do_dry_run()
    innerResult <- 0
    <#else>
    innerResult <- runABRAID(
        occurrence_path,
        extent_path,
        admin1_path,
        admin2_path,
        covariate_paths,
        covariate_names,
        rep(FALSE, length(covariate_paths)),
        verbose,
        max_cpus,
        load_seegSDM,
        parallel_flag)
    </#if>
    innerResult # return
}, error = function(e) {
    print(paste("Error:  ", e))
    return(1)
})

# Delete temp dir for intermediate rasters
unlink("temp", TRUE)

# Set exit code
print(paste("Exit code:  ", result))
quit(status=result)
