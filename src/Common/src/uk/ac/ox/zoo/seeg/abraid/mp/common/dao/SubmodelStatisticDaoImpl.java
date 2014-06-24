package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.List;

/**
 * The SubmodelStatistic entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class SubmodelStatisticDaoImpl extends AbstractDao<SubmodelStatistic, Integer> implements SubmodelStatisticDao {
    public SubmodelStatisticDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets all submodel statistics for a model run.
     * @param modelRun The model run.
     * @return All submodel statistics for the model run.
     */
    @Override
    public List<SubmodelStatistic> getSubmodelStatisticsForModelRun(ModelRun modelRun) {
        return listNamedQuery("getSubmodelStatisticsForModelRun", "modelRun", modelRun);
    }
}
