package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoNamesLocationPrecision;

/**
 * The GeoNamesLocationPrecisionDao entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNamesLocationPrecisionDaoImpl extends AbstractDao<GeoNamesLocationPrecision, String>
        implements GeoNamesLocationPrecisionDao {
    public GeoNamesLocationPrecisionDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
