package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static java.util.Map.Entry;

/**
 * Service class for diseases, including disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
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
    private NativeSQL nativeSQL;

    public DiseaseServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao,
                              DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao,
                              DiseaseGroupDao diseaseGroupDao,
                              HealthMapDiseaseDao healthMapDiseaseDao,
                              ValidatorDiseaseGroupDao validatorDiseaseGroupDao,
                              AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao,
                              AdminUnitGlobalDao adminUnitGlobalDao,
                              AdminUnitTropicalDao adminUnitTropicalDao,
                              DiseaseExtentClassDao diseaseExtentClassDao,
                              NativeSQL nativeSQL) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseOccurrenceReviewDao = diseaseOccurrenceReviewDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.healthMapDiseaseDao = healthMapDiseaseDao;
        this.validatorDiseaseGroupDao = validatorDiseaseGroupDao;
        this.adminUnitDiseaseExtentClassDao = adminUnitDiseaseExtentClassDao;
        this.adminUnitGlobalDao = adminUnitGlobalDao;
        this.adminUnitTropicalDao = adminUnitTropicalDao;
        this.diseaseExtentClassDao = diseaseExtentClassDao;
        this.nativeSQL = nativeSQL;
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
     * Gets the validator disease group by its id.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @return The validator disease group.
     */
    @Override
    public ValidatorDiseaseGroup getValidatorDiseaseGroupById(Integer validatorDiseaseGroupId) {
        return validatorDiseaseGroupDao.getById(validatorDiseaseGroupId);
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
        sortMapValueListsByName(map);
        return map;
    }

    private void sortMapValueListsByName(Map<String, List<DiseaseGroup>> map) {
        for (Entry<String, List<DiseaseGroup>> entry : map.entrySet()) {
            sortOnName(entry.getValue());
        }
    }

    private void sortOnName(List<DiseaseGroup> diseaseGroups) {
        Collections.sort(diseaseGroups, new Comparator<DiseaseGroup>() {
            @Override
            public int compare(DiseaseGroup o1, DiseaseGroup o2) {
                return o1.getShortNameForDisplay().toLowerCase().compareTo(o2.getShortNameForDisplay().toLowerCase());
            }
        });
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
     * Gets disease occurrences with the specified IDs.
     * @param diseaseOccurrenceIds The disease occurrence IDs.
     * @return The disease occurrences with the specified IDs.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesById(List<Integer> diseaseOccurrenceIds) {
        return diseaseOccurrenceDao.getByIds(diseaseOccurrenceIds);
    }

    /**
     * Gets all disease occurrences for the specified disease group.
     * @param diseaseGroupId The disease group's ID.
     * @return all disease occurrences for the specified disease group.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesByDiseaseGroupId(int diseaseGroupId) {
        return diseaseOccurrenceDao.getByDiseaseGroupId(diseaseGroupId);
    }

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     * value, and must have a final weighting. If null, the validation and final weightings are ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean useGoldStandardOccurrences) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(
            diseaseGroupId, minimumValidationWeighting, minimumOccurrenceDate, isDiseaseGroupGlobal(diseaseGroupId),
            useGoldStandardOccurrences);
    }

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is false.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);
    }

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is true
     * and finalWeighting is currently null.
     *
     * @param diseaseGroupId The ID of the disease group.
     * @param mustHaveEnvironmentalSuitability True if the occurrence's environmental suitability must be non-null.
     *                                         False if it doesn't matter either way.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
            Integer diseaseGroupId, boolean mustHaveEnvironmentalSuitability) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
                diseaseGroupId, mustHaveEnvironmentalSuitability);
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                           boolean useGoldStandardOccurrences) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, useGoldStandardOccurrences);
    }

    /**
     * Gets the list of new disease occurrences for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param startDate Occurrences must be newer than this date.
     * @param endDate Occurrences must be older than this date, to ensure they have had ample time in validation.
     * @return The list of relevant new occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForTriggeringModelRun(int diseaseGroupId,
                                                                              DateTime startDate, DateTime endDate) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForTriggeringModelRun(diseaseGroupId, startDate, endDate);
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

    @Override
    public List<DiseaseExtentClass> getAllDiseaseExtentClasses() {
        return diseaseExtentClassDao.getAll();
    }

    /**
     * Gets a list of all the disease occurrence reviews in the database.
     * @return The disease occurrence reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getAllDiseaseOccurrenceReviews() {
        return diseaseOccurrenceReviewDao.getAll();
    }

    /**
     *  Gets a list of all the disease occurrence reviews in the database for the specified disease group.
     *  @param diseaseGroupId The ID of the disease group.
     *  @return The disease occurrence reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getAllDiseaseOccurrenceReviewsByDiseaseGroupId(Integer diseaseGroupId) {
        return diseaseOccurrenceReviewDao.getAllReviewsByDiseaseGroupId(diseaseGroupId);
    }

    /**
     * Gets all reviews (for all time) for the disease occurrences which have new reviews.
     * @param lastModelRunPrepDate The date on which the disease occurrence reviews were last retrieved.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of the reviews of disease occurrences whose weightings needs updating.
     */
    @Override
    public List<DiseaseOccurrenceReview> getDiseaseOccurrenceReviewsForModelRunPrep(DateTime lastModelRunPrepDate,
                                                                                       Integer diseaseGroupId) {
        return diseaseOccurrenceReviewDao.getDiseaseOccurrenceReviewsForModelRunPrep(lastModelRunPrepDate,
                diseaseGroupId);
    }

    /**
     * Determines whether the specified disease occurrence already exists in the database. This is true if an
     * occurrence exists with the same disease group, location, alert and occurrence start date.
     * @param occurrence The disease occurrence.
     * @return True if the occurrence already exists in the database, otherwise false.
     */
    @Override
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
    @Override
    public boolean doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(Integer diseaseOccurrenceId,
                                                                                  Integer validatorDiseaseGroupId) {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(diseaseOccurrenceId);
        return validatorDiseaseGroupId.equals(occurrence.getValidatorDiseaseGroup().getId());
    }


    /**
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    @Override
    public void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        diseaseOccurrenceDao.save(diseaseOccurrence);
    }

    /**
     * Saves a disease group.
     * @param diseaseGroup The disease group to save.
     */
    @Override
    public void saveDiseaseGroup(DiseaseGroup diseaseGroup) {
        diseaseGroupDao.save(diseaseGroup);
    }

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    @Override
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

    /**
     * Updates the aggregated disease extent that is stored in the disease_extent table, for the specified disease.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    @Override
    public void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal) {
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, isGlobal);
    }

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    @Override
    public DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrenceStatistics(diseaseGroupId);
    }

    /**
     * Gets the IDs of disease groups that have automatic model runs enabled.
     * @return The IDs of relevant disease groups.
     */
    @Override
    public List<Integer> getDiseaseGroupIdsForAutomaticModelRuns() {
        return diseaseGroupDao.getIdsForAutomaticModelRuns();
    }

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForBatching(int diseaseGroupId, DateTime batchEndDate) {
        return diseaseOccurrenceDao.getOccurrencesForBatching(diseaseGroupId, batchEndDate);
    }

    private boolean isDiseaseGroupGlobal(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = getDiseaseGroupById(diseaseGroupId);
        return (diseaseGroup.isGlobal() != null && diseaseGroup.isGlobal());
    }
}
