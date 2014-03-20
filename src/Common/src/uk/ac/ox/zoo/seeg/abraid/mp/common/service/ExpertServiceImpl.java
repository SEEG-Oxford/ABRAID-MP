package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseGroupDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceReviewDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ExpertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Set;

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
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    public ExpertServiceImpl(ExpertDao expertDao, DiseaseOccurrenceDao diseaseOccurrenceDao,
                             DiseaseGroupDao diseaseGroupDao, DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao) {
        this.expertDao = expertDao;
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.diseaseOccurrenceReviewDao = diseaseOccurrenceReviewDao;
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
     * Gets a set of the specified expert's disease interests.
     * @param expertId The id of the specified expert.
     * @return The list of disease groups the expert can validate.
     */
    @Override
    public Set<DiseaseGroup> getDiseaseInterests(Integer expertId) {
        return expertDao.getById(expertId).getDiseaseGroups();
    }

    /**
     * Saves the disease occurrence review.
     * @param expertEmail The email address of the expert providing review.
     * @param occurrenceId The id of the disease occurrence.
     * @param response The expert's response.
     */
    @Override
    public void saveDiseaseOccurrenceReview(String expertEmail, Integer occurrenceId,
                                            DiseaseOccurrenceReviewResponse response) {
        Expert expert = getExpertByEmail(expertEmail);
        DiseaseOccurrence diseaseOccurrence = diseaseOccurrenceDao.getById(occurrenceId);

        DiseaseOccurrenceReview diseaseOccurrenceReview = new DiseaseOccurrenceReview(expert, diseaseOccurrence,
                response);
        diseaseOccurrenceReviewDao.save(diseaseOccurrenceReview);
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

    /**
     * Determines whether specified disease group is in expert's set of disease interests.
     * @param diseaseGroupId The id of the disease group.
     * @param expertId The id of the specified expert.
     * @return True if disease is an expert's interest, otherwise false.
     */
    public boolean isDiseaseGroupInExpertsDiseaseInterests(Integer diseaseGroupId, Integer expertId) {
        Set<DiseaseGroup> diseaseInterests = getDiseaseInterests(expertId);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        return diseaseInterests.contains(diseaseGroup);
    }

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert, already exists in the database.
     * @param diseaseOccurrenceId The id of the disease group.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    public boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId) {
        return diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expertId, diseaseOccurrenceId);
    }
}
