package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
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
    private DiseaseGroupDao diseaseGroupDao;
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    public ExpertServiceImpl(AdminUnitReviewDao adminUnitReviewDao,
                             ExpertDao expertDao,
                             DiseaseGroupDao diseaseGroupDao,
                             DiseaseOccurrenceDao diseaseOccurrenceDao,
                             DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao) {
        this.adminUnitReviewDao = adminUnitReviewDao;
        this.expertDao = expertDao;
        this.diseaseGroupDao = diseaseGroupDao;
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
     * Gets an expert by ID.
     * @param expertId The ID of the expert.
     * @return The expert, or null if not found.
     */
    @Override
    public Expert getExpertById(int expertId) {
        return expertDao.getById(expertId);
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
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review. Only SEEG users may view occurrences of disease groups during setup phase.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param expertId The id of the specified expert.
     * @param userIsSeeg Whether the expert is a member of SEEG, and therefore should review more occurrences.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the
     * database.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId, boolean userIsSeeg,
                                                                                Integer validatorDiseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(
                expertId, userIsSeeg, validatorDiseaseGroupId);
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
     * Gets all reviews for the specified disease group, including repeat reviews.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    @Override
    public List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer diseaseGroupId) {
        return adminUnitReviewDao.getByDiseaseGroupId(diseaseGroupId);
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
     * Gets a page worth of publicly visible experts.
     * @param pageNumber The page number to return.
     * @param pageSize The size of the pages to split the visible experts into.
     * @return A page worth of publicly visible experts
     */
    @Override
    public List<Expert> getPageOfPubliclyVisibleExperts(int pageNumber, int pageSize) {
        return expertDao.getPageOfPubliclyVisible(pageNumber, pageSize);
    }

    /**
     * Gets a count of the publicly visible experts.
     * @return The count.
     */
    @Override
    public long getCountOfPubliclyVisibleExperts() {
        return expertDao.getCountOfPubliclyVisible();
    }

    /**
     * Saves the disease occurrence review.
     * @param expertId The id of the expert providing review.
     * @param occurrenceId The id of the disease occurrence.
     * @param response The expert's response.
     */
    @Override
    public void saveDiseaseOccurrenceReview(Integer expertId, Integer occurrenceId,
                                            DiseaseOccurrenceReviewResponse response) {
        Expert expert = getExpertById(expertId);
        DiseaseOccurrence diseaseOccurrence = diseaseOccurrenceDao.getById(occurrenceId);

        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, diseaseOccurrence, response);
        diseaseOccurrenceReviewDao.save(review);
    }

    /**
     * Saves the review of the administrative unit.
     * @param expertId The id of the expert providing review.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The gaulCode of the administrative unit.
     * @param response The expert's response.
     */
    @Override
    public void saveAdminUnitReview(Integer expertId, Integer diseaseGroupId, Integer gaulCode,
                                    DiseaseExtentClass response) {
        Expert expert = getExpertById(expertId);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        AdminUnitReview review;
        if (diseaseGroup.isGlobal()) {
            review = new AdminUnitReview(expert, gaulCode, null, diseaseGroup, response);
        } else {
            review = new AdminUnitReview(expert, null, gaulCode, diseaseGroup, response);
        }
        adminUnitReviewDao.save(review);
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
