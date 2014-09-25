package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * The EffectCurveCovariateInfluence entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class EffectCurveCovariateInfluenceDaoImpl
        extends AbstractDao<EffectCurveCovariateInfluence, Integer> implements EffectCurveCovariateInfluenceDao {
    public EffectCurveCovariateInfluenceDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets all effect curve covariate influences for a model run.
     * @param modelRun The model run.
     * @return All effect curve covariate influences for the model run.
     */
    @Override
    public List<EffectCurveCovariateInfluence> getEffectCurveCovariateInfluencesForModelRun(ModelRun modelRun) {
        return listNamedQuery("getEffectCurveCovariateInfluencesForModelRun", "modelRun", modelRun);
    }
}
