package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.nio.file.Paths;

/**
 * Provides a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationFactoryImpl implements RunConfigurationFactory {
    private static final String DEFAULT_RUN_NAME = "run";
    private final ConfigurationService configurationService;

    public RunConfigurationFactoryImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Creates a new RunConfiguration using the current defaults.
     * @return The new RunConfiguration
     */
    @Override
    public RunConfiguration createDefaultConfiguration() {
        return new RunConfiguration(
                Paths.get(tempFindR()).toFile(), // move to conf service
                Paths.get(configurationService.getCacheDirectory()).toFile(),
                DEFAULT_RUN_NAME, // move to conf service
                Integer.MAX_VALUE, // move to conf service
                configurationService.getModelRepositoryVersion());
    }


    ///COVERAGE:OFF - - temp
    // CHECKSTYLE.OFF - temp
    private static String tempFindR() {
        OSChecker osChecker = new OSCheckerImpl();
        if (osChecker.isWindows()) {
            return "C:\\Program Files\\R\\R-3.0.2\\bin\\x64\\R.exe";
        } else {
            return "/usr/bin/R";
        }
    }
    ///CHECKSTYLE:ON
    //COVERAGE:ON
}
