package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

/**
 * Service class for diseases, including disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class DiseaseServiceImpl implements DiseaseService {
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;
    private DiseaseGroupDao diseaseGroupDao;
    private HealthMapDiseaseDao healthMapDiseaseDao;
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;
    private AdminUnitGlobalDao adminUnitGlobalDao;
    private AdminUnitTropicalDao adminUnitTropicalDao;
    private DiseaseExtentClassDao diseaseExtentClassDao;

    public DiseaseServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao,
                              DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao,
                              DiseaseGroupDao diseaseGroupDao,
                              HealthMapDiseaseDao healthMapDiseaseDao,
                              ValidatorDiseaseGroupDao validatorDiseaseGroupDao,
                              AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao,
                              AdminUnitGlobalDao adminUnitGlobalDao,
                              AdminUnitTropicalDao adminUnitTropicalDao,
                              DiseaseExtentClassDao diseaseExtentClassDao) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseOccurrenceReviewDao = diseaseOccurrenceReviewDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.healthMapDiseaseDao = healthMapDiseaseDao;
        this.validatorDiseaseGroupDao = validatorDiseaseGroupDao;
        this.adminUnitDiseaseExtentClassDao = adminUnitDiseaseExtentClassDao;
        this.adminUnitGlobalDao = adminUnitGlobalDao;
        this.adminUnitTropicalDao = adminUnitTropicalDao;
        this.diseaseExtentClassDao = diseaseExtentClassDao;
    }

    /**
     * Gets all HealthMap diseases.
     * @return All HealthMap diseases.
     */
    @Override
    public List<HealthMapDisease> getAllHealthMapDiseases() {
        return healthMapDiseaseDao.getAll();
    }

    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    @Override
    public List<DiseaseGroup> getAllDiseaseGroups() {
        return diseaseGroupDao.getAll();
    }

    /**
     * Gets all the validator disease groups.
     * @return A list of all validator disease groups.
     */
    @Override
    public List<ValidatorDiseaseGroup> getAllValidatorDiseaseGroups() {
        return validatorDiseaseGroupDao.getAll();
    }

    /**
     * Gets the disease group by its id.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease group.
     */
    @Override
    public DiseaseGroup getDiseaseGroupById(Integer diseaseGroupId) {
        return diseaseGroupDao.getById(diseaseGroupId);
    }

    /**
     * Gets the list of disease groups, for each validator disease group.
     * @return The map, from the name of the validator disease group, to the disease groups belonging to it.
     */
    @Override
    public Map<String, List<DiseaseGroup>> getValidatorDiseaseGroupMap() {
        List<DiseaseGroup> allDiseaseGroups = getAllDiseaseGroups();
        Map<String, List<DiseaseGroup>> map = new HashMap<>();
        for (DiseaseGroup diseaseGroup : allDiseaseGroups) {
            if (diseaseGroup.getValidatorDiseaseGroup() != null) {
                String name = diseaseGroup.getValidatorDiseaseGroup().getName();
                if (map.containsKey(name)) {
                    map.get(name).add(diseaseGroup);
                } else {
                    map.put(name, new ArrayList<>(Arrays.asList(diseaseGroup)));
                }
            }
        }
        return map;
    }

    /**
     * Gets a list of admin units for global or tropical diseases, depending on whether the specified disease group
     * is a global or a tropical disease.
     * @param diseaseGroupId The ID of the disease group.
     * @return The disease extent.
     */
    @Override
    public List<? extends AdminUnitGlobalOrTropical> getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(
            Integer diseaseGroupId) {
        if (isDiseaseGroupGlobal(diseaseGroupId)) {
            return adminUnitGlobalDao.getAll();
        } else {
            return adminUnitTropicalDao.getAll();
        }
    }

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     *                                   value.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value.
     * @param feedIds All disease occurrences must result from one of these feeds. If feed IDs is null or zero,
     *                accepts all feeds.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            List<Integer> feedIds) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroupId, minimumValidationWeighting, minimumOccurrenceDate, feedIds,
                isDiseaseGroupGlobal(diseaseGroupId));
    }

    /**
     * Gets the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The disease extent.
     */
    @Override
    public List<AdminUnitDiseaseExtentClass> getDiseaseExtentByDiseaseGroupId(Integer diseaseGroupId) {
        if (isDiseaseGroupGlobal(diseaseGroupId)) {
            return adminUnitDiseaseExtentClassDao.getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(
                    diseaseGroupId);
        } else {
            return adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(
                    diseaseGroupId);
        }
    }

    /**
     * Gets a disease extent class by name.
     * @param name The disease extent class name.
     * @return The corresponding disease extent class, or null if it does not exist.
     */
    @Override
    public DiseaseExtentClass getDiseaseExtentClass(String name) {
        return diseaseExtentClassDao.getByName(name);
    }

    /**
     *  Gets a list of all the disease occurrence reviews in the database.
     *  @return The disease occurrence reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getAllDiseaseOccurrenceReviews() {
        return diseaseOccurrenceReviewDao.getAll();
    }

    /**
     * Gets all reviews (for all time) for the disease occurrences which have new reviews.
     * @param lastRetrieval The date on which the disease occurrence reviews were last retrieved.
     * @return A list of the reviews of disease occurrences whose weightings needs updating.
     */
    @Override
    public List<DiseaseOccurrenceReview> getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(
            LocalDateTime lastRetrieval) {
        return diseaseOccurrenceReviewDao.getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(
            lastRetrieval);
    }

    /**
     * Determines whether the specified disease occurrence already exists in the database. This is true if an
     * occurrence exists with the same disease group, location, alert and occurrence start date.
     * @param occurrence The disease occurrence.
     * @return True if the occurrence already exists in the database, otherwise false.
     */
    public boolean doesDiseaseOccurrenceExist(DiseaseOccurrence occurrence) {
        // These are not-null fields in the database, so if any of them are null then there cannot possibly be a
        // matching disease occurrence in the database
        if (occurrence.getDiseaseGroup() == null ||
                occurrence.getDiseaseGroup().getId() == null ||
                occurrence.getLocation() == null ||
                occurrence.getLocation().getId() == null ||
                occurrence.getAlert() == null ||
                occurrence.getAlert().getId() == null) {
            return false;
        }

        List<DiseaseOccurrence> matchingOccurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceDate());

        return matchingOccurrences.size() > 0;
    }

    /**
     * Determines whether the specified occurrence's disease id belongs to the corresponding validator disease group.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @return True if the occurrence refers to a disease in the validator disease group, otherwise false.
     */
    public boolean doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(Integer diseaseOccurrenceId,
                                                                                  Integer validatorDiseaseGroupId) {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(diseaseOccurrenceId);
        return validatorDiseaseGroupId.equals(occurrence.getValidatorDiseaseGroup().getId());
    }

    private boolean isDiseaseGroupGlobal(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = getDiseaseGroupById(diseaseGroupId);
        return (diseaseGroup.isGlobal() != null && diseaseGroup.isGlobal());
    }

    /**
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    @Override
    @Transactional
    public void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        diseaseOccurrenceDao.save(diseaseOccurrence);
    }

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    @Override
    @Transactional
    public void saveHealthMapDisease(HealthMapDisease disease) {
        healthMapDiseaseDao.save(disease);
    }

    /**
     * Saves a disease extent class that is associated with an admin unit (global or tropical).
     * @param adminUnitDiseaseExtentClass The object to save.
     */
    @Override
    public void saveAdminUnitDiseaseExtentClass(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass) {
        adminUnitDiseaseExtentClassDao.save(adminUnitDiseaseExtentClass);
    }
}
