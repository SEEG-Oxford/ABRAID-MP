package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.BaseConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.File;
import java.nio.file.Paths;

/**
 * Service class for mutable model wrapper configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperConfigurationServiceImpl
        extends BaseConfigurationService implements ModelWrapperConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(ModelWrapperConfigurationServiceImpl.class);
    private static final String LOG_UPDATING_AUTH_CONFIGURATION =
            "Updating auth configuration: %s %s";
    private static final String LOG_UPDATING_R_PATH =
            "Updating R path configuration: %s";
    private static final String LOG_UPDATING_RUN_DURATION =
            "Updating max run duration configuration: %s";

    private static final String DEFAULT_LINUX_CACHE_DIR = "/var/lib/abraid/modelwrapper";
    private static final String DEFAULT_WINDOWS_CACHE_DIR = System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper";
    private static final String DEFAULT_LINUX_R_PATH = "/usr/bin/R";
    private static final String DEFAULT_WINDOWS_R_PATH = System.getenv("R_HOME") + "\\bin\\x64\\R.exe";

    private static final String USERNAME_KEY = "auth.username";
    private static final String PASSWORD_KEY = "auth.password_hash";
    private static final String CACHE_DIR_KEY = "cache.data.dir";
    private static final String R_EXECUTABLE_KEY = "r.executable.path";
    private static final String R_MAX_DURATION_KEY = "r.max.duration";
    private static final String MODEL_OUTPUT_HANDLER_ROOT_URL_KEY = "model.output.handler.root.url";

    private final OSChecker osChecker;

    public ModelWrapperConfigurationServiceImpl(File basicProperties, OSChecker osChecker)
            throws ConfigurationException {
        super(basicProperties);
        this.osChecker = osChecker;
    }

    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    @Override
    public void setAuthenticationDetails(String username, String passwordHash) {
        LOGGER.info(String.format(LOG_UPDATING_AUTH_CONFIGURATION, username, passwordHash));
        getConfigFile().setProperty(USERNAME_KEY, username);
        getConfigFile().setProperty(PASSWORD_KEY, passwordHash);
    }

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username
     */
    @Override
    public String getAuthenticationUsername() {
        return getConfigFile().getString(USERNAME_KEY);
    }

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    @Override
    public String getAuthenticationPasswordHash() {
        return getConfigFile().getString(PASSWORD_KEY);
    }

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    @Override
    public String getCacheDirectory() {
        String defaultDir = osChecker.isWindows() ? DEFAULT_WINDOWS_CACHE_DIR : DEFAULT_LINUX_CACHE_DIR;
        return getConfigFile().getString(CACHE_DIR_KEY, defaultDir);
    }

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     * @throws ConfigurationException When a value for the R path is not set and R is not present in default locations.
     */
    @Override
    public String getRExecutablePath() throws ConfigurationException {
        if (getConfigFile().containsKey(R_EXECUTABLE_KEY)) {
            return getConfigFile().getString(R_EXECUTABLE_KEY);
        } else {
            return findDefaultR();
        }
    }

    /**
     * Sets the current path to the R executable binary.
     * @param path The R path.
     */
    @Override
    public void setRExecutablePath(String path) {
        LOGGER.info(String.format(LOG_UPDATING_R_PATH, path));
        getConfigFile().setProperty(R_EXECUTABLE_KEY, path);
    }

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    @Override
    public int getMaxModelRunDuration() {
        return getConfigFile().getInt(R_MAX_DURATION_KEY, Integer.MAX_VALUE);
    }

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    @Override
    public void setMaxModelRunDuration(int value) {
        LOGGER.info(String.format(LOG_UPDATING_RUN_DURATION, value));
        getConfigFile().setProperty(R_MAX_DURATION_KEY, value);
    }

    @Override
    public String getModelOutputHandlerRootUrl() {
        return getConfigFile().getString(MODEL_OUTPUT_HANDLER_ROOT_URL_KEY);
    }

    private String findDefaultR() throws ConfigurationException {
        String rPath = osChecker.isWindows() ? DEFAULT_WINDOWS_R_PATH : DEFAULT_LINUX_R_PATH;
        File r = Paths.get(rPath).toFile();
        if (r.exists() && r.canExecute()) {
            return r.getAbsolutePath();
        } else {
            throw new ConfigurationException("Could not find R.");
        }
    }
}

