package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedGlobal;

/**
 * The AdminUnitSimplifiedGlobal entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitSimplifiedGlobalDaoImpl extends AbstractDao<AdminUnitSimplifiedGlobal, Integer>
        implements AdminUnitSimplifiedGlobalDao  {
    public AdminUnitSimplifiedGlobalDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
