package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

/**
 * Service interface for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ConfigurationService {
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
}
