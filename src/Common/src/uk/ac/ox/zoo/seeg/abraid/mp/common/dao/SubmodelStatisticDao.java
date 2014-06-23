package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.List;

/**
 * Interface for the SubmodelStatistic entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface SubmodelStatisticDao {
    /**
     * Gets all submodel statistics.
     * @return All submodel statistics.
     */
    List<SubmodelStatistic> getAll();

    /**
     * Gets a submodel statistic by ID.
     * @param id The ID.
     * @return The submodel statistic with the specified ID, or null if not found.
     */
    SubmodelStatistic getById(Integer id);

    /**
     * Gets all submodel statistics for a model run.
     * @param modelRun The model run.
     * @return All submodel statistics for the model run.
     */
    List<SubmodelStatistic> getSubmodelStatisticsForModelRun(ModelRun modelRun);

    /**
     * Saves the specified submodel statistic.
     * @param covariateInfluence The submodel statistic to save.
     */
    void save(SubmodelStatistic submodelStatistic);
}
