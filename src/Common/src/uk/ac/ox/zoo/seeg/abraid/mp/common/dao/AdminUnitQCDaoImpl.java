package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;

/**
 * The AdminUnitQC entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitQCDaoImpl extends AbstractDao<AdminUnitQC, Integer> implements AdminUnitQCDao {
    public AdminUnitQCDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets an administrative unit by GAUL code.
     * @param gaulCode The GAUL code.
     * @return The administrative unit with the specified GAUL code, or null if it does not exist.
     */
    public AdminUnitQC getByGaulCode(Integer gaulCode) {
        return getById(gaulCode);
    }
}
