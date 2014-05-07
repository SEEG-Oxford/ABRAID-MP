package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobal;

/**
 * The AdminUnitGlobal entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitGlobalDaoImpl extends AbstractDao<AdminUnitGlobal, Integer> implements AdminUnitGlobalDao {
    public AdminUnitGlobalDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the global admin unit, specified by its GAUL code.
     * @param gaulCode The gaul code of the admin unit.
     * @return The global admin unit.
     */
    @Override
    public AdminUnitGlobal getByGaulCode(Integer gaulCode) {
        return getById(gaulCode);
    }
}
