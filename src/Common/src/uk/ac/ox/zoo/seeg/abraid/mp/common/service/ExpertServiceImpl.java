package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AdminUnitReviewDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceReviewDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ExpertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service class for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ExpertServiceImpl implements ExpertService {
    private AdminUnitReviewDao adminUnitReviewDao;
    private ExpertDao expertDao;
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    public ExpertServiceImpl(AdminUnitReviewDao adminUnitReviewDao,
                             ExpertDao expertDao,
                             DiseaseOccurrenceDao diseaseOccurrenceDao,
                             DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao) {
        this.adminUnitReviewDao = adminUnitReviewDao;
        this.expertDao = expertDao;
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
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
     * Gets a set of the specified expert's disease interests.
     * @param expertId The id of the specified expert.
     * @return The list of validator disease groups the expert can validate.
     */
    @Override
    public List<ValidatorDiseaseGroup> getDiseaseInterests(Integer expertId) {
        Expert expert = expertDao.getById(expertId);
        return expert.getValidatorDiseaseGroups();
    }

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the
     * database.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId,
                                                                                Integer validatorDiseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, validatorDiseaseGroupId);
    }

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of disease occurrence reviews for the specified expert.
     */
    @Override
    public Long getDiseaseOccurrenceReviewCount(Integer expertId) {
        return diseaseOccurrenceReviewDao.getCountByExpertId(expertId);
    }

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param diseaseOccurrenceId The id of the disease group.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    @Override
    public boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId) {
        return diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expertId, diseaseOccurrenceId);
    }

    /**
     * Gets all reviews submitted by the specified expert, for the specified disease group.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    @Override
    public List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer expertId, Integer diseaseGroupId) {
        return adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(expertId, diseaseGroupId);
    }

    /**
     * Gets the number of admin unit reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of admin unit reviews for the specified expert.
     */
    @Override
    public Long getAdminUnitReviewCount(Integer expertId) {
        return adminUnitReviewDao.getCountByExpertId(expertId);
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
    public void saveExpert(Expert expert) {
        expertDao.save(expert);
    }
}
