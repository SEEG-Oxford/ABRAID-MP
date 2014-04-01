package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;

/**
 * The GeoName entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNameDaoImpl extends AbstractDao<GeoName, Integer> implements GeoNameDao {
    public GeoNameDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
