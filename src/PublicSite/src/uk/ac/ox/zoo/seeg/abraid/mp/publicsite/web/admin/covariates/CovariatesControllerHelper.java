package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;

import java.io.IOException;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
public interface CovariatesControllerHelper {
    /**
     * Builds a covariate file storage location.
     * @param subdirectory The subdirectory of the covariate directory in which to store the file.
     * @param file The covariate file.
     * @return The covariate file storage location
     */
    String extractTargetPath(String subdirectory, MultipartFile file);

    /**
     * Gets the JSON version of the covariate configuration.
     * @return The covariate configuration
     * @throws java.io.IOException Thrown if the covariate directory can not be checked for new files.
     */
    JsonCovariateConfiguration getCovariateConfiguration() throws IOException;

    /**
     * Persist the JSON version of the covariate configuration into the database.
     * @param config The covariate configuration
     */
    void setCovariateConfiguration(JsonCovariateConfiguration config);

    /**
     * Persist a single new covariate file to the filesystem and database.
     * @param name The display name for the covariate.
     * @param isDiscrete True if this covariate contains discrete values.
     * @param path The location to store the covariate.
     * @param file The covariate.
     * @throws IOException Thrown if the covariate director can not be writen to.
     */
    void saveNewCovariateFile(String name, boolean isDiscrete, String path, MultipartFile file) throws IOException;
}
