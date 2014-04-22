package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;

/**
 * The GlobalAdminUnit entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class GlobalAdminUnitDaoImpl extends AbstractDao<GlobalAdminUnit, Integer> implements GlobalAdminUnitDao {
    public GlobalAdminUnitDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the global admin unit, specified by its GAUL code.
     * @param gaulCode The gaul code of the admin unit.
     * @return The global admin unit.
     */
    @Override
    public GlobalAdminUnit getByGaulCode(Integer gaulCode) {
        return uniqueResultNamedQuery("getGlobalAdminUnitByGaulCode", "gaulCode", gaulCode);
    }
}
