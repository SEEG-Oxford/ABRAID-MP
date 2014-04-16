package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;

/**
 * The LandSeaBorder entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LandSeaBorderDaoImpl extends AbstractDao<LandSeaBorder, Integer> implements LandSeaBorderDao {
    public LandSeaBorderDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
