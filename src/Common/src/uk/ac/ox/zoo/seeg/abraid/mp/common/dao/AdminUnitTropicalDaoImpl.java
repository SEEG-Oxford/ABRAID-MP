package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitTropical;

/**
 *  The TropcalAdminUnit entity's Data Access Object.
 *  Copyright (c) 2014 University of Oxford
 */
public class AdminUnitTropicalDaoImpl extends AbstractDao<AdminUnitTropical, Integer> implements AdminUnitTropicalDao {
    public AdminUnitTropicalDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
