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
    fileConn <- file("results/statistics.csv")
    writeLines(c(
        '"deviance","rmse","kappa","auc","sens","spec","pcc","kappa_sd","auc_sd","sens_sd","spec_sd","pcc_sd","thresh"',
        '11.6594680584662,0.321982740986221,0.827564935064935,0.926668487336777,0.872766955266955,0.95479797979798,0.913782467532468,0.108979438326179,0.0575426150001365,0.0897249720382768,0.0452020202020202,0.0571654781463352,0.513258333333333',
        '9.08399379702637,0.386047894618841,0.715357142857143,0.85606320861678,0.82047619047619,0.894880952380952,0.857678571428571,0.166865395174917,,,,0.0919734133841459,0.594784210526316',
        '17.4481222994735,0.399306659881532,0.699365079365079,0.860205341395818,0.744087301587302,0.955277777777778,0.84968253968254,0.154734375499861,0.0898706868215893,0.1446117995233,0.0447222222222222,0.0841244533880227,0.351814536340852',
        '49.4666462916365,0.457728112048421,0.67954939668175,0.855233204412858,0.776473730517848,0.903075666163901,0.839774698340875,0.123012128551306,0.0670573473631061,0.101707279563057,0.0574131997552302,0.0639130560016783,0.1657',
        ',,,,,,,,,,,,',
        ',,,,,,,,,,,,',
        '19.2560918745035,0.374242700473496,0.693293650793651,0.879075223607962,0.768055555555556,0.925238095238095,0.846646825396825,0.141727830076034,0.0816936779411817,0.125917653861513,0.0605716342678192,0.0758743778120778,0.323066413662239'
    ), fileConn)
    close(fileConn)
    fileConn <- file("results/relative_influence.csv")
    writeLines(c(
        '"","file_path","covariate","mean","2.5%","97.5%"',
        '"X4","/var/lib/abraid/modelwrapper/covariates/tempaucpv.tif","Temperature suitability index (Malaria Pv)","85.2443821174109","78.0962137739846","89.6437036968154"',
        '"X3","/var/lib/abraid/modelwrapper/covariates/wd0114a0.tif","AVHRR Normalized Difference Vegetation Index (mean)","10.446434302638","6.30709809904488","16.9330982142733"',
        '"X2","/var/lib/abraid/modelwrapper/covariates/upr_p.tif","GRUMP peri-urban surface","2.85156061558689","1.63273339493853","4.04396887681117"',
        '"X1","/var/lib/abraid/modelwrapper/covariates/upr_u.tif","GRUMP urban surface","1.45762296436416","0.717795627914255","2.70022249615236"'
    ), fileConn)
    close(fileConn)
    fileConn <- file("results/effect_curves.csv")
    writeLines(c(
        '"","file_path","covariate","covariate","mean","2.5%","97.5%"',
        '"1","/var/lib/abraid/modelwrapper/covariates/upr_u.tif","GRUMP urban surface","0","-2.37479404081078","-4.13549487661354","0.224254442751269"',
        '"2","/var/lib/abraid/modelwrapper/covariates/upr_u.tif","GRUMP urban surface","0.0101010101010101","-2.37479404081078","-4.13549487661354","0.224254442751269"',
        '"3","/var/lib/abraid/modelwrapper/covariates/upr_p.tif","GRUMP peri-urban surface","0","-2.38108299941873","-4.13584392766574","0.215995702108742"',
        '"4","/var/lib/abraid/modelwrapper/covariates/upr_p.tif","GRUMP peri-urban surface","0.0101010101010101","-1.80231212304095","-3.80175127097161","0.808562197891076"',
        '"5","/var/lib/abraid/modelwrapper/covariates/wd0114a0.tif","AVHRR Normalized Difference Vegetation Index (mean)","969.880004882812","-3.58659144060993","-4.8945367703539","-1.17563310838305"',
        '"6","/var/lib/abraid/modelwrapper/covariates/wd0114a0.tif","AVHRR Normalized Difference Vegetation Index (mean)","977.92646928267","-3.58659144060993","-4.8945367703539","-1.17563310838305"',
        '"7","/var/lib/abraid/modelwrapper/covariates/tempaucpv.tif","Temperature suitability index (Malaria Pv)","0","-4.53333317408311","-5.60473819969926","-3.12325201620901"',
        '"8","/var/lib/abraid/modelwrapper/covariates/tempaucpv.tif","Temperature suitability index (Malaria Pv)","974.333333333333","-4.52391279204434","-5.60473819969926","-3.00559026414409"'
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
