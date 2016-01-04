package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Service interface for mutable modelwrapper configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelWrapperConfigurationService {
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
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    String getCacheDirectory();

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     * @throws ConfigurationException When a value for the R path is not set and R is not present in default locations.
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

    /**
     * Gets the root URL for the Model Output Handler web service.
     * @return The root URL for the Model Output Handler web service.
     */
    String getModelOutputHandlerRootUrl();

    /**
     * Gets whether the workspace for completed model runs should be deleted.
     * @return Whether the workspace for completed model runs should be deleted.
     */
    boolean getDeleteWorkspaces();
}
