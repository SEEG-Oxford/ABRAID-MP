package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ExpertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

/**
 * Service class for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class ExpertServiceImpl implements ExpertService {
    private ExpertDao expertDao;

    @Required
    public void setExpertDao(ExpertDao expertDao) {
        this.expertDao = expertDao;
    }

    /**
     * Gets a list of all experts.
     * @return A list of all experts.
     */
    public List<Expert> getAllExperts() {
        return expertDao.getAll();
    }

    /**
     * Gets an expert by name.
     * @param name The name.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public Expert getExpertByName(String name) {
        return expertDao.getByName(name);
    }

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    @Override
    @Transactional
    public void saveExpert(Expert expert) {
        expertDao.save(expert);
    }
}
