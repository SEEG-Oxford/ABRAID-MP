package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.File;

/**
 * Service class for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationServiceImpl.class);
    private static final String LOG_LOADING_CONFIGURATION_FILE = "Loading configuration file %s";
    private static final String LOG_UPDATING_AUTH_CONFIGURATION = "Updating auth configuration: %s %s";
    private static final String LOG_UPDATING_REPOSITORY_URL_CONFIGURATION = "Updating repository url configuration: %s";
    private static final String LOG_UPDATING_VERSION_CONFIGURATION = "Updating repository version configuration: %s";

    private static final String DEFAULT_LINUX_CACHE_DIR = "/var/lib/abraid/modelwrapper";
    private static final String DEFAULT_WINDOWS_CACHE_DIR = System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper";

    private static final String USERNAME_KEY = "auth.username";
    private static final String PASSWORD_KEY = "auth.password_hash";
    private static final String CACHE_DIR_KEY = "cache.data.dir";
    private static final String MODEL_REPOSITORY_KEY = "model.repo.url";
    private static final String MODEL_VERSION_KEY = "model.repo.version";

    private final FileConfiguration basicProperties;
    private final OSChecker osChecker;

    public ConfigurationServiceImpl(File basicProperties, OSChecker osChecker) throws ConfigurationException {
        LOGGER.info(String.format(LOG_LOADING_CONFIGURATION_FILE, basicProperties.toString()));
        this.basicProperties = new PropertiesConfiguration(basicProperties);
        this.basicProperties.setAutoSave(true);
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
        basicProperties.setProperty(USERNAME_KEY, username);
        basicProperties.setProperty(PASSWORD_KEY, passwordHash);
    }

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username
     */
    @Override
    public String getAuthenticationUsername() {
        return basicProperties.getString(USERNAME_KEY);
    }

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    @Override
    public String getAuthenticationPasswordHash() {
        return basicProperties.getString(PASSWORD_KEY);
    }

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    @Override
    public String getModelRepositoryUrl() {
        return basicProperties.getString(MODEL_REPOSITORY_KEY);
    }

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    @Override
    public void setModelRepositoryUrl(String repositoryUrl) {
        LOGGER.info(String.format(LOG_UPDATING_REPOSITORY_URL_CONFIGURATION, repositoryUrl));
        basicProperties.setProperty(MODEL_REPOSITORY_KEY, repositoryUrl);
    }

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    @Override
    public String getModelRepositoryVersion() {
        return basicProperties.getString(MODEL_VERSION_KEY);
    }

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    @Override
    public void setModelRepositoryVersion(String version) {
        LOGGER.info(String.format(LOG_UPDATING_VERSION_CONFIGURATION, version));
        basicProperties.setProperty(MODEL_VERSION_KEY, version);
    }

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    @Override
    public String getCacheDirectory() {
        String defaultDir = osChecker.isWindows() ? DEFAULT_WINDOWS_CACHE_DIR : DEFAULT_LINUX_CACHE_DIR;
        return basicProperties.getString(CACHE_DIR_KEY, defaultDir);
    }
}

