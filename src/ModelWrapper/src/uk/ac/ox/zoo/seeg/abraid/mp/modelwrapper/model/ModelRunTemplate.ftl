<#--
    A templated run script for the ABRAID-MP disease risk model.
    Copyright (c) 2014 University of Oxford
-->
# A launch script for the ABRAID-MP disease risk model
# Copyright (c) 2014 University of Oxford

# Run as: $ R --no-save --quiet -f modelRun.R

# Run name = ${run}

# Running for disease = ${disease}
# Model version = ${model_version}

# Set verbosity
verbosity <- ${verbosity}

# Define outbreak data
outbreakData <- "${outbreak_file}"

# Define covariates to use.
# If you would like to use these covariate files please contact TBD@TBD.com, as we can not release them in all circumstances.
covariates <- c(
<#list covariates as covariate>
    "${covariate}"<#if covariate_has_next>,</#if>
</#list>
)

# Define disease extent data
extentData <- "${extent_file}"

# Load the model
# The full model is available from GitHub at https://github.com/SEEG-Oxford/seegSDM
# source(model/seegSDM.R)

# Run the model
result <- tryCatch({
    # seegSDM.run(outbreakData, extentData, covariates, verbosity, ${dry_run?c})
    write(paste("I'm running the model using: ", outbreakData), file="echo")
    # Temp POC
    print(covariates)
    0 # return 0
}, warning = function(w) {
    print(paste("Warning:  ", w))
    return(0)
}, error = function(e) {
    print(paste("Error:  ", e))
    return(1)
})

# Set exit code
quit(status=result)