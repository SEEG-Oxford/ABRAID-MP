package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.List;

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

    /**
     * Gets a validator disease group by name.
     * @param name The name.
     * @return The validator disease group with the specified name, or null if not found.
     */
    @Override
    public ValidatorDiseaseGroup getByName(String name) {
        return uniqueResultNamedQuery("getValidatorDiseaseGroup", "name", name);
    }
}
