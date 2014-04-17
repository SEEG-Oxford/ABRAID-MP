package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.TropicalAdminUnit;

/**
 *  The TropcalAdminUnit entity's Data Access Object.
 *  Copyright (c) 2014 University of Oxford
 */
public class TropicalAdminUnitDaoImpl extends AbstractDao<TropicalAdminUnit, Integer> implements TropicalAdminUnitDao {
    public TropicalAdminUnitDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
