package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * The CovariateInfluence entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateInfluenceDaoImpl
        extends AbstractDao<CovariateInfluence, Integer> implements CovariateInfluenceDao {
    public CovariateInfluenceDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets all covariate influences for a model run.
     * @param modelRun The model run.
     * @return All covariate influences for the model run.
     */
    @Override
    public List<CovariateInfluence> getCovariateInfluencesForModelRun(ModelRun modelRun) {
        return listNamedQuery("getCovariateInfluencesForModelRun", "modelRun", modelRun);
    }
}
