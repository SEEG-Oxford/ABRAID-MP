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
parallel_flag <- FALSE

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

# Define admin unit rasters to use.
# If you would like to use these admin unit rasters (or related shape files) please contact TBD@TBD.com, as we cannot release them in all circumstances.
admin_paths <- c(
<#list admin_files as admin_level_file>
    "${admin_level_file}"<#if admin_level_file_has_next>,</#if>
</#list>
)

# Set CRAN mirror
local({r <- getOption("repos")
    r["CRAN"] <- "http://cran.r-project.org"
    options(repos=r)
})

# Load devtools
if (!require('devtools', quietly=TRUE)) {
    install.packages('devtools', quiet=TRUE)
    library('devtools', quietly=TRUE)
}

# Load the model and it's dependencies via devtools
# The full model is available from GitHub at https://github.com/SEEG-Oxford/seegSDM
if (!dry_run) {
    install_deps('model')
    load_all('model')
}

# Define a function that can be used to load the model on cluster nodes
load_seegSDM <- function () {
    library('devtools', quietly=TRUE)
    load_all('model')
}

# Run the model
result <- tryCatch({
    if (!dry_run) {
        runABRAID(
            occurrence_path,
            extent_path,
            admin_paths,
            covariate_paths,
            rep(FALSE, length(covariate_paths)),
            verbose,
            max_cpus,
            load_seegSDM,
            parallel_flag)
    } else {
        0 # return 0
    }
}, warning = function(w) {
    print(paste("Warning:  ", w))
    return(1)
}, error = function(e) {
    print(paste("Error:  ", e))
    return(1)
})

# Set exit code
quit(status=result)