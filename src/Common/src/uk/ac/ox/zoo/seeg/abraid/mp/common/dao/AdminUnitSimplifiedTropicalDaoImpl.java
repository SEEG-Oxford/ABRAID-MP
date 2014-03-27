package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedTropical;

/**
 * The AdminUnitSimplifiedTropical entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitSimplifiedTropicalDaoImpl extends AbstractDao<AdminUnitSimplifiedTropical, Integer>
        implements AdminUnitSimplifiedTropicalDao  {
    public AdminUnitSimplifiedTropicalDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
