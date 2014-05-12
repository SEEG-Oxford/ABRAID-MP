package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

import java.io.File;

/**
 * An immutable data structure to hold the configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfiguration {
    // The name of the run
    private final String runName;

    // The base directory into which the run directory should be created
    private final File baseDir;

    // Sub configuration objects
    private final CodeRunConfiguration codeConfig;
    private final ExecutionRunConfiguration executionConfig;
    private final CovariateRunConfiguration covariateConfig;
    private final AdminUnitRunConfiguration adminUnitConfig;

    public RunConfiguration(String runName, File baseDir,
                            CodeRunConfiguration codeConfig, ExecutionRunConfiguration executionConfig,
                            CovariateRunConfiguration covariateConfig, AdminUnitRunConfiguration adminUnitConfig) {
        this.runName = runName;
        this.baseDir = baseDir;

        this.codeConfig = codeConfig;
        this.executionConfig = executionConfig;
        this.covariateConfig = covariateConfig;
        this.adminUnitConfig = adminUnitConfig;
    }

    public String getRunName() {
        return runName;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public CodeRunConfiguration getCodeConfig() {
        return codeConfig;
    }

    public ExecutionRunConfiguration getExecutionConfig() {
        return executionConfig;
    }

    public CovariateRunConfiguration getCovariateConfig() {
        return covariateConfig;
    }

    public AdminUnitRunConfiguration getAdminUnitConfig() {
        return adminUnitConfig;
    }
}
