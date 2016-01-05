package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.apache.commons.configuration.ConfigurationException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ModelWrapperConfigurationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Provides a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationFactoryImpl implements RunConfigurationFactory {

    private static final String RUNS_SUBDIRECTORY = "runs";

    private final ModelWrapperConfigurationService modelWrapperConfigurationService;

    public RunConfigurationFactoryImpl(ModelWrapperConfigurationService modelWrapperConfigurationService) {
        this.modelWrapperConfigurationService = modelWrapperConfigurationService;
    }

    /**
     * Creates a new RunConfiguration using the current defaults.
     * @param runName The name of the model run.
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable cannot be found.
     * @throws IOException When the covariate configuration cannot be read.
     */
    @Override
    public RunConfiguration createDefaultConfiguration(String runName)
            throws ConfigurationException, IOException {
        return new RunConfiguration(
                runName,
                buildBaseDir(),
                modelWrapperConfigurationService.getDeleteWorkspaces(),
                buildExecutionConfig());
    }

    private File buildBaseDir() {
        return Paths.get(modelWrapperConfigurationService.getCacheDirectory(), RUNS_SUBDIRECTORY).toFile();
    }

    private ExecutionRunConfiguration buildExecutionConfig() throws ConfigurationException {
        return new ExecutionRunConfiguration(
                Paths.get(modelWrapperConfigurationService.getRExecutablePath()).toFile(),
                modelWrapperConfigurationService.getMaxModelRunDuration()
        );
    }
}
