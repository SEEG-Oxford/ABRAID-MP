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
max_cpu <- ${max_cpu?c}

# Define outbreak data
outbreakData <- "${outbreak_file}"

# Define disease extent data
extentData <- "${extent_file}"

# Define covariates to use.
# If you would like to use these covariate files please contact TBD@TBD.com, as we cannot release them in all circumstances.
covariates <- c(
<#list covariate_files as covariate>
    "${covariate}"<#if covariate_has_next>,</#if>
</#list>
)

# Define admin unit rasters to use.
# If you would like to use these admin unit rasters (or related shape files) please contact TBD@TBD.com, as we cannot release them in all circumstances.
admin_units <- c(
<#list admin_files as admin_level_file>
    "${admin_level_file}"<#if admin_level_file_has_next>,</#if>
</#list>
)

# Load the model
# The full model is available from GitHub at https://github.com/SEEG-Oxford/seegSDM
# source(model/seegSDM.R)

# Run the model
result <- tryCatch({
    if (${dry_run?string("TRUE","FALSE")}) {
        # Dry run?
        0 # return 0
    } else {
        # seegSDM.run(outbreakData, extentData, admin_units, covariates, verbosity, max_cpu:c)
        write(paste("I'm running the model using: ", outbreakData), file="echo")
        # Temp POC
        print(covariates)
        0 # return 0
    }
}, warning = function(w) {
    print(paste("Warning:  ", w))
    return(0)
}, error = function(e) {
    print(paste("Error:  ", e))
    return(1)
})

# Set exit code
quit(status=result)