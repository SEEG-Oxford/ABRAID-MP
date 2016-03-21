package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Interface for the CovariateFile entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
public interface CovariateFileDao {
    /**
     * Gets all covariate files.
     * @return All covariate files.
     */
    List<CovariateFile> getAll();

    /**
     * Gets all of covariate files for a given disease group.
     * @param diseaseGroup The disease group.
     * @return All covariate files.
     */
    List<CovariateFile> getCovariateFilesByDiseaseGroup(DiseaseGroup diseaseGroup);

    /**
     * Saves the specified covariate file.
     * @param covariateFile The covariate file to save.
     */
    void save(CovariateFile covariateFile);

    /**
     * Gets a covariate file by ID.
     * @param id The ID.
     * @return The covariate file with the specified ID, or null if not found.
     */
    CovariateFile getById(Integer id);
}
