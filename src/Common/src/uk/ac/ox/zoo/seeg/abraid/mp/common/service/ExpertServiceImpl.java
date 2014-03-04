package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseGroupDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ExpertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
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
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseGroupDao diseaseGroupDao;

    public ExpertServiceImpl(ExpertDao expertDao, DiseaseOccurrenceDao diseaseOccurrenceDao,
                             DiseaseGroupDao diseaseGroupDao) {
        this.expertDao = expertDao;
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseGroupDao = diseaseGroupDao;
    }

    /**
     * Gets a list of all experts.
     * @return A list of all experts.
     */
    public List<Expert> getAllExperts() {
        return expertDao.getAll();
    }

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    @Override
    public Expert getExpertByEmail(String email) {
        return expertDao.getByEmail(email);
    }

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the diseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or diseaseGroupId cannot be found in the database.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId, Integer diseaseGroupId)
            throws IllegalArgumentException {
        if (expertDao.getById(expertId) == null) {
            throw new IllegalArgumentException("Expert does not exist in database.");
        }
        if (diseaseGroupDao.getById(diseaseGroupId) == null) {
            throw new IllegalArgumentException("Disease Group does not exist in database.");
        }

        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, diseaseGroupId);
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
