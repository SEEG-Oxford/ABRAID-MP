package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Interface for the CovariateInfluence entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface CovariateInfluenceDao {
    /**
     * Gets all covariate influences.
     * @return All covariate influences.
     */
    List<CovariateInfluence> getAll();

    /**
     * Gets all covariate influences for a model run.
     * @param modelRun The model run.
     * @return All covariate influences for the model run.
     */
    List<CovariateInfluence> getCovariateInfluencesForModelRun(ModelRun modelRun);

    /**
     * Saves the specified covariate influence.
     * @param covariateInfluence The covariate influence to save.
     */
    void save(CovariateInfluence covariateInfluence);

    /**
     * Gets a covariate influence by ID.
     * @param id The ID.
     * @return The covariate influence with the specified ID, or null if not found.
     */
    CovariateInfluence getById(Integer id);
}
