package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class ExpertServiceImpl implements ExpertService {
    private ExpertDao expertDao;
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;

    public ExpertServiceImpl(ExpertDao expertDao,
                             DiseaseOccurrenceDao diseaseOccurrenceDao,
                             DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao,
                             ValidatorDiseaseGroupDao validatorDiseaseGroupDao) {
        this.expertDao = expertDao;
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseOccurrenceReviewDao = diseaseOccurrenceReviewDao;
        this.validatorDiseaseGroupDao = validatorDiseaseGroupDao;
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
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the
     * database.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId,
                                                                        Integer validatorDiseaseGroupId)
            throws IllegalArgumentException {
        if (expertDao.getById(expertId) == null) {
            throw new IllegalArgumentException("Expert does not exist in database.");
        }
        if (validatorDiseaseGroupDao.getById(validatorDiseaseGroupId) == null) {
            throw new IllegalArgumentException("Validator Disease Group does not exist in database.");
        }

        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, validatorDiseaseGroupId);
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
     * Gets the number of disease occurrence reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of disease occurrence reviews for the specified expert.
     */
    @Override
    public Integer getDiseaseOccurrenceReviewCount(Integer expertId) {
        return diseaseOccurrenceReviewDao.getByExpertId(expertId).size();
    }

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, per validator disease group.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroups The list of an expert's disease interests.
     * @return The map from the name of the validator disease group to its corresponding diseaseOccurrenceReviewCount.
     */
    public Map<String, Integer> getDiseaseOccurrenceReviewCountPerValidatorDiseaseGroup(Integer expertId,
                                                                   List<ValidatorDiseaseGroup> validatorDiseaseGroups) {
        Map<String, Integer> map = initialiseValidatorDiseaseGroupsMap(validatorDiseaseGroups);
        List<DiseaseOccurrenceReview> allReviews = diseaseOccurrenceReviewDao.getByExpertIdAndValidatorDiseaseGroups(
                expertId, validatorDiseaseGroups);
        int count;
        for (DiseaseOccurrenceReview review : allReviews) {
            String validatorDiseaseGroupName = review.getValidatorDiseaseGroupName();
            if (validatorDiseaseGroupName != null) {
                count = map.get(validatorDiseaseGroupName);
                map.put(validatorDiseaseGroupName, count + 1);
            }
        }
        return map;
    }

    /**
     * Gets the number of disease occurrences, per validator disease group.
     * @param validatorDiseaseGroups The list of an expert's disease interests.
     * @return The map from validatorDiseaseGroupName to its corresponding count of disease occurrences.
     */
    public Map<String, Integer> getDiseaseOccurrenceCountPerValidatorDiseaseGroup(List<ValidatorDiseaseGroup>
                                                                                          validatorDiseaseGroups) {
        Map<String, Integer> map = initialiseValidatorDiseaseGroupsMap(validatorDiseaseGroups);
        List<DiseaseOccurrence> allOccurrences =
                diseaseOccurrenceDao.getByValidatorDiseaseGroups(validatorDiseaseGroups);
        int count;
        for (DiseaseOccurrence occurrence : allOccurrences) {
            String validatorDiseaseGroupName = occurrence.getValidatorDiseaseGroupName();
            count = map.containsKey(validatorDiseaseGroupName) ? map.get(validatorDiseaseGroupName) : 0;
            map.put(validatorDiseaseGroupName, count + 1);
        }
        return map;
    }

    /**
     * Create a map where the key is the name of each validator disease group provided,
     * and its corresponding value is initialised to 0.
     * @param validatorDiseaseGroups The validator disease groups to input to the map as keys.
     * @return The map from validator disease group name to an integer count.
     */
    Map<String, Integer> initialiseValidatorDiseaseGroupsMap(List<ValidatorDiseaseGroup> validatorDiseaseGroups) {
        Map<String, Integer> map = new HashMap<>();
        for (ValidatorDiseaseGroup validatorDiseaseGroup : validatorDiseaseGroups) {
            map.put(validatorDiseaseGroup.getName(), 0);
        }
        return map;
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
}
