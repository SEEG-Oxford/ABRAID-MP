package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Service interface for covariate file data.
 * Copyright (c) 2014 University of Oxford
 */
public interface CovariateService {
    /**
     * Gets all covariate files.
     * @return All covariate files.
     */
    List<CovariateFile> getAllCovariateFiles();

    /**
     * Gets all of covariate files for a given disease group.
     * @param diseaseGroup The disease group.
     * @return All covariate files.
     */
    List<CovariateFile> getCovariateFilesByDiseaseGroup(DiseaseGroup diseaseGroup);

    /**
     * Gets a covariate file by its ID.
     * @param id The ID.
     * @return The covariate file with the specified ID, or null if not found.
     */
    CovariateFile getCovariateFileById(int id);

    /**
     * Saves the specified covariate file.
     * @param covariateFile The covariate file to save.
     */
    void saveCovariateFile(CovariateFile covariateFile);

    /**
     * Gets the directory in which covariate files are stored.
     * @return The covariate directory.
     */
    String getCovariateDirectory();
}
