# A launch script for the ABRAID-MP disease risk model
# Copyright (c) 2014 University of Oxford

# Run as: $ R --no-save --quiet -f modelRun.R

# Get warnings as they occur (not batched)
options(warn=1)

# Stash a list of the default packages
default.pkgs <- search()

# Define a function to setup and execute the model
attempt_model_run <- function() {
    # Set verbosity
    verbose <- TRUE

    # Set max CPUs
    max_cpus <- 64

    # Set parallel execution
    parallel_flag <- TRUE

    # Disease ID
    disease <- 1234

    # Model mode
    mode <- "bhatt"

    # Define occurrence data
    occurrence_path <- "data/occurrences.csv"

    # Define disease extent data
    extent_path <- "data/extent.tif"

    # Define bias occurrence data
    sample_bias_path <- "data/sample_bias.csv"

    # Define covariates to use.
    # If you would like to use these covariate files please contact abraid@zoo.ox.ac.uk, as we cannot release them in all circumstances.
    covariate_paths <- list(
        "id1"="covariates/c1.tif",
        "id2"=list(
            "2015-01"="covariates/c2.tif_1",
            "2015-02"="covariates/c2.tif_2"
        ),
        "id3"="covariates/sub/c3.tif"
    )
    covariate_factors <- list(
        "id1"=TRUE,
        "id2"=FALSE,
        "id3"=TRUE
    )

    # Define admin unit rasters to use.
    # If you would like to use these admin unit rasters (or related shape files) please contact abraid@zoo.ox.ac.uk, as we cannot release them in all circumstances.
    admin_paths <- list(
        "admin0"="admins/admin0.tif",
        "admin1"="admins/admin1.tif",
        "admin2"="admins/admin2.tif",
        "admin3"="admins/admin2.tif"  # This one wont be used, but is needed for compatablity with older bits of seegSDM
    )

    # Create a temp dir for intermediate rasters
    dir.create('temp')

    # Set CRAN mirror
    local({r <- getOption("repos")
        r["CRAN"] <- "http://cran.r-project.org"
        options(repos=r)
    })


    # Define a function that can be used to load the model on cluster nodes
    load_seegSDM <- function () {
        library('devtools', quietly=TRUE)
        load_all('model')
        rasterOptions(tmpdir="temp")
    }

    find_dry_run_file <- function(suffix, extension) {
        # Get disease abbreviation (working directory prefix before underscore)
        install.packages('stringr', quiet=TRUE)
        library('stringr', quietly=TRUE)
        disease_abbreviation <- str_extract(basename(getwd()),"^[^_]*")

        # Get files in the dry run outputs directory that are predefined output files for the specified disease and suffix
        # e.g. "mal_2014-11-18-18-02-04_c7a1950d-ecd0-4720-8edd-4432cdcae08a_mean.tif" matches if "mal" is the disease
        # abbreviation and "mean" is the suffix
        predefined_file_pattern <- paste(disease_abbreviation, "_.*", suffix, "\\.", extension, sep = "")
        predefined_files <- list.files(path="../dry_run_outputs/", pattern=predefined_file_pattern, full.names=TRUE)

        if (length(predefined_files) >= 1) {
            # Predefined output file found, so copy it to the desired output path
            return(predefined_files[1])
        } else {
            return(NA)
        }
    }

    # Define a function to create an output raster for model dry runs
    create_dry_run_raster <- function(suffix, output_path) {
        predefined_file <- find_dry_run_file(suffix, "tif")
        if (!is.na(predefined_file)) {
            # Predefined output raster file found, so copy it to the desired output path
            file.copy(predefined_file, output_path)
        } else {
            # Predefined output raster not found, so generate a raster with random pixels
            writeRaster(raster(matrix(rep(runif(10), each = 3480 * 864), nrow = 3480, ncol = 8640), crs=crs("+proj=longlat +datum=WGS84 +no_defs"), xmn=-180, xmx=180, ymn=-60, ymx=85),
                        filename=output_path, format="GTiff", NAflag=-9999, options=c("COMPRESS=DEFLATE","ZLEVEL=9"))
        }
    }

    create_dry_run_csv <- function(suffix, output_path, fallback_data) {
        predefined_file <- find_dry_run_file(suffix, "csv")
        if (!is.na(predefined_file)) {
            # Predefined output csv file found, so copy it to the desired output path
            file.copy(predefined_file, output_path)
        } else {
            # Predefined output csv not found, so generate a csv from fallback_data
            fileConn <- file(output_path)
            writeLines(fallback_data, fileConn)
            close(fileConn)
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
        create_dry_run_csv("statistics", "results/statistics.csv", c(
            '"auc","sens","spec","pcc","kappa","auc_sd","sens_sd","spec_sd","pcc_sd","kappa_sd"',
            '1,2,3,4,5,6,7,8,9,10'))
        create_dry_run_csv("influence", "results/relative_influence.csv", c(
            '"","file_path","mean","2.5%","97.5%"',
            '"X","covariates/upr_u.tif","1","2","3"'))
        create_dry_run_csv("effect", "results/effect_curves.csv", c(
            '"","file_path","covariate","mean","2.5%","97.5%"',
            '"1","covariates/upr_u.tif","0","-3","-5","0.3"'))
    }

    # Run the model
    result <- tryCatch({
        innerResult <- -1
        do_dry_run()
        innerResult <- 0
        innerResult # return
    }, error = function(e) {
        write(paste("Error:  ", e), stdout())
        return(1)
    })

    # Delete temp dir for intermediate rasters
    unlink("temp", TRUE)

    # Set exit code
    write(paste("Exit code:  ", result), stdout())
    return(result)
}

attempt_model_run_with_clean_environment <- function() {
    # Replace the environment of the attempt_model_run function, to ensure runs are completely independent
    environment(attempt_model_run) <- new.env()

    # Perform model run
    attempt_model_run()
}

clean_up_model_run <- function() {
    write("=== First model run attempt failed. Cleaning. ===", stdout())
    write("=== First model run attempt failed. Cleaning. ===", stderr())

    # Move the results directory
    if (file.exists("results")) {
        file.rename("results", "results_first")
    }

    # Unset result variable in the global environment
    if (exists("result", inherits = TRUE)) {
        rm("result", inherits = TRUE)
    }

    # Detach all non-default packages
    all.pkgs <- search()
    detach.pkgs <- setdiff(all.pkgs, default.pkgs)
    suppressWarnings(lapply(detach.pkgs, detach, character.only = TRUE, unload = TRUE, force = TRUE))

    write("=== First model run attempt failed. Retrying. ===", stdout())
    write("=== First model run attempt failed. Retrying. ===", stderr())
}

# Attempt the model run
result <- attempt_model_run_with_clean_environment()

if (result == 1) {
    # Set up for a 2nd attempt at running the model
    clean_up_model_run()

    # Reattempt the model run
    result <- attempt_model_run_with_clean_environment()
}

quit(status=result)
