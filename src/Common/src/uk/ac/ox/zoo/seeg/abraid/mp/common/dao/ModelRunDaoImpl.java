package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

/**
 * The ModelRun entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoImpl extends AbstractDao<ModelRun, Integer> implements ModelRunDao {
    public ModelRunDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ModelRun getByName(String name) {
        return uniqueResultNamedQuery("getModelRunByName", "name", name);
    }
}
