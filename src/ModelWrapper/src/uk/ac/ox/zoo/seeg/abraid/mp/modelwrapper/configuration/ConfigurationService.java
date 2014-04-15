package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;

import java.io.File;

/**
 * Service interface for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ConfigurationService {
    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    void setAuthenticationDetails(String username, String passwordHash);

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username.
     */
    String getAuthenticationUsername();

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    String getAuthenticationPasswordHash();

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    String getModelRepositoryUrl();

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    void setModelRepositoryUrl(String repositoryUrl);

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    String getModelRepositoryVersion();

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    void setModelRepositoryVersion(String version);

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    String getCacheDirectory();

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     */
    String getRExecutablePath() throws ConfigurationException;

    /**
     * Sets the current path to the R executable binary.
     * @param path The R path.
     */
    void setRExecutablePath(String path);

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    int getMaxModelRunDuration();

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    void setMaxModelRunDuration(int value);
}
