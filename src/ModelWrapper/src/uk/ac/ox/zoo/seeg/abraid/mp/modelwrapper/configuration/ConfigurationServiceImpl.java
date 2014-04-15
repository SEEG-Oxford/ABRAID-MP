package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import freemarker.template.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

/**
 * Service class for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final String DEFAULT_LINUX_CACHE_DIR = "/var/lib/abraid/modelwrapper";
    private static final String DEFAULT_WINDOWS_CACHE_DIR = System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper";

    private static final String USERNAME_KEY = "auth.username";
    private static final String PASSWORD_KEY = "auth.password_hash";
    private static final String CACHE_DIR_KEY = "cache.data.dir";
    private static final String MODEL_REPOSITORY_KEY = "model.repo.url";
    private static final String MODEL_VERSION_KEY = "model.repo.version";
    private static final String R_EXECUTABLE_KEY = "r.executable.path";
    private static final String R_MAX_DURATION_KEY = "r.max.duration";

    private final FileConfiguration basicProperties;
    private final OSChecker osChecker;

    public ConfigurationServiceImpl(File basicProperties, OSChecker osChecker) throws ConfigurationException {
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

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     */
    @Override
    public String getRExecutablePath() throws ConfigurationException {
        if (basicProperties.containsKey(R_EXECUTABLE_KEY)) {
            return basicProperties.getString(R_EXECUTABLE_KEY);
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
        basicProperties.setProperty(R_EXECUTABLE_KEY, path);
    }

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    @Override
    public int getMaxModelRunDuration() {
        return basicProperties.getInt(R_MAX_DURATION_KEY, Integer.MAX_VALUE);
    }

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    @Override
    public void setMaxModelRunDuration(int value) {
        basicProperties.setProperty(R_MAX_DURATION_KEY, value);
    }

    private String findDefaultR() throws ConfigurationException {
        File r = null;
        if (osChecker.isWindows()) {
            String rHome = System.getenv("R_HOME");
            r = Paths.get(rHome, "bin/R.exe").toFile();
        } else {
            r = new File("/usr/bin/R");
        }

        if (r.exists() && r.canExecute()) {
            return r.getAbsolutePath();
        } else {
            throw new ConfigurationException("Could not find R.");
        }
    }
}

