package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

/**
 * The ValidatorDiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidatorDiseaseGroupDaoImpl extends AbstractDao<ValidatorDiseaseGroup, Integer>
        implements ValidatorDiseaseGroupDao {
    public ValidatorDiseaseGroupDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
