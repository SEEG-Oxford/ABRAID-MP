package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Interface for the EffectCurveCovariateInfluence entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface EffectCurveCovariateInfluenceDao {
    /**
     * Gets all effect curve covariate influences.
     * @return All effect curve covariate influences.
     */
    List<EffectCurveCovariateInfluence> getAll();

    /**
     * Gets all effect curve covariate influences for a model run.
     * @param modelRun The model run.
     * @return All effect curve covariate influences for the model run.
     */
    List<EffectCurveCovariateInfluence> getEffectCurveCovariateInfluencesForModelRun(ModelRun modelRun);

    /**
     * Saves the specified effect curve covariate influence.
     * @param effectCurveCovariateInfluence The effect curve covariate influence to save.
     */
    void save(EffectCurveCovariateInfluence effectCurveCovariateInfluence);

    /**
     * Gets an effect curve covariate influence by ID.
     * @param id The ID.
     * @return The effect curve covariate influence with the specified ID, or null if not found.
     */
    EffectCurveCovariateInfluence getById(Integer id);
}
