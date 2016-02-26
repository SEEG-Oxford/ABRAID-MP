package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
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
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    private ModelRunDao modelRunDao;
    private DiseaseExtentClassDao diseaseExtentClassDao;
    private int maxDaysOnValidator;
    private NativeSQL nativeSQL;

    public DiseaseServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao,
                              DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao,
                              DiseaseGroupDao diseaseGroupDao,
                              ValidatorDiseaseGroupDao validatorDiseaseGroupDao,
                              AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao,
                              ModelRunDao modelRunDao,
                              DiseaseExtentClassDao diseaseExtentClassDao,
                              int maxDaysOnValidator,
                              NativeSQL nativeSQL) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseOccurrenceReviewDao = diseaseOccurrenceReviewDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.validatorDiseaseGroupDao = validatorDiseaseGroupDao;
        this.adminUnitDiseaseExtentClassDao = adminUnitDiseaseExtentClassDao;
        this.modelRunDao = modelRunDao;
        this.diseaseExtentClassDao = diseaseExtentClassDao;
        this.maxDaysOnValidator = maxDaysOnValidator;
        this.nativeSQL = nativeSQL;
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

    /**
     * Gets a list of the disease groups for which there are occurrences waiting to be reviewed, by the given expert.
     * @param expert The expert.
     * @return A list of disease groups.
     */
    @Override
    public List<DiseaseGroup> getDiseaseGroupsNeedingOccurrenceReviewByExpert(Expert expert) {
        return diseaseGroupDao.getDiseaseGroupsNeedingOccurrenceReviewByExpert(expert.getId());
    }

    /**
     * Gets a list of the disease groups for which there are admin units waiting to be reviewed, by the given expert.
     * @param expert The expert.
     * @return A list of disease groups.
     */
    @Override
    public List<DiseaseGroup> getDiseaseGroupsNeedingExtentReviewByExpert(Expert expert) {
        return diseaseGroupDao.getDiseaseGroupsNeedingExtentReviewByExpert(expert.getId());
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
     * Gets the disease occurrence with the specified ID.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @return The disease occurrence.
     */
    @Override
    public DiseaseOccurrence getDiseaseOccurrenceById(Integer diseaseOccurrenceId) {
        return diseaseOccurrenceDao.getById(diseaseOccurrenceId);
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
     * Gets all disease occurrences for the specified disease group and occurrence status.
     * @param diseaseGroupId The disease group's ID.
     * @param statuses The disease occurrence's status.
     * @return All disease occurrences for the specified disease group and status.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(int diseaseGroupId,
            DiseaseOccurrenceStatus... statuses) {
        return diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId, statuses);
    }

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     * value, and must have a final weighting. If null, the validation and final weightings are ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean onlyUseGoldStandardOccurrences) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroupId, minimumValidationWeighting, minimumOccurrenceDate, onlyUseGoldStandardOccurrences);
    }

    /**
     * Gets disease occurrences currently in validation, for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);
    }

    /**
     * Gets disease occurrences for the specified disease group which are yet to have a final weighting assigned.
     * @param diseaseGroupId The ID of the disease group.
     * @param statuses A set of disease occurrence statuses from which to return occurrences.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
            Integer diseaseGroupId, DiseaseOccurrenceStatus... statuses) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, statuses);
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                           boolean onlyUseGoldStandardOccurrences) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId,
                onlyUseGoldStandardOccurrences);
    }

    /**
     * Gets the number of distinct locations from the new disease occurrences for the specified disease group.
     * @param diseaseGroup The disease group.
     * @param cutoffDateForOccurrences Occurrences must be newer than this date
     *                  (or N days prior, if they went through manual validation).
     * @return The number of locations.
     */
    @Override
    public long getDistinctLocationsCountForTriggeringModelRun(
            DiseaseGroup diseaseGroup, DateTime cutoffDateForOccurrences) {
        Double minDistanceFromDiseaseExtent = diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering();
        Double maxEnvironmentalSuitability = diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering();
        Set<Integer> locationIdsUsedInLastModelRun = getLocationIdsFromLastModelRun(diseaseGroup);

        return diseaseOccurrenceDao.getDistinctLocationsCountForTriggeringModelRun(diseaseGroup.getId(),
                locationIdsUsedInLastModelRun,
                cutoffDateForOccurrences,
                subtractMaxDaysOnValidator(cutoffDateForOccurrences).toDateTimeAtStartOfDay(),
                maxEnvironmentalSuitability,
                minDistanceFromDiseaseExtent);
    }

    private HashSet<Integer> getLocationIdsFromLastModelRun(DiseaseGroup diseaseGroup) {
        ModelRun lastModelRun = modelRunDao.getLastRequestedModelRun(diseaseGroup.getId());
        if (lastModelRun == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(
                // Note that if the last model run was non-automatic then the input occurrences will be empty (not
                // stored). This means that for the first auto model run the trigger may fire slightly early. CM says
                // that this is the correct behavior.
                extract(lastModelRun.getInputDiseaseOccurrences(), on(DiseaseOccurrence.class).getLocation().getId())
            );
        }
    }

    /**
     * Gets the list of most recent disease occurrences on the admin unit disease extent class (defined by the disease
     * group and admin unit gaul code pair).
     * @param diseaseGroup The disease group the admin unit disease extent class represents.
     * @param gaulCode The gaul code the admin unit disease extent class represents.
     * @return The list of latest disease occurrences for the specified admin unit disease extent class.
     */
    @Override
    public List<DiseaseOccurrence> getLatestValidatorOccurrencesForAdminUnitDiseaseExtentClass(
            DiseaseGroup diseaseGroup, Integer gaulCode) {
        return adminUnitDiseaseExtentClassDao.getLatestValidatorOccurrencesForAdminUnitDiseaseExtentClass(
                diseaseGroup.getId(), diseaseGroup.isGlobal(), gaulCode);
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
     * Gets the latest disease extent class change date for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The latest change date.
     */
    @Override
    public DateTime getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(Integer diseaseGroupId) {
        return adminUnitDiseaseExtentClassDao.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(diseaseGroupId);
    }

    /**
     * Gets a disease extent class by name.
     * @param name The disease extent class name.
     * @return The corresponding disease extent class, or null if it does not exist.
     */
    @Override
    public DiseaseExtentClass getDiseaseExtentClass(String name) {
        return (name == null) ? null : diseaseExtentClassDao.getByName(name);
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
     * Gets the reviews submitted by reliable experts (whose weighting is greater than the threshold) for the disease
     * occurrences which are in review.  "I don't know" response are excluded.
     * @param diseaseGroupId The ID of the disease group.
     * @param expertWeightingThreshold Reviews by experts with a weighting greater than this value will be considered.
     * @return A list of disease occurrence reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getDiseaseOccurrenceReviewsForOccurrencesInValidationForUpdatingWeightings(
            Integer diseaseGroupId, Double expertWeightingThreshold) {
        return diseaseOccurrenceReviewDao.getDiseaseOccurrenceReviewsForOccurrencesInValidationForUpdatingWeightings(
                diseaseGroupId, expertWeightingThreshold);
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
     * Saves a disease extent class that is associated with an admin unit (global or tropical).
     * @param adminUnitDiseaseExtentClass The object to save.
     */
    @Override
    public void saveAdminUnitDiseaseExtentClass(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass) {
        adminUnitDiseaseExtentClassDao.save(adminUnitDiseaseExtentClass);
    }

    /**
     * Updates the aggregated disease extent that is stored in the disease_extent table, for the specified disease.
     * @param diseaseGroup The disease group.
     */
    @Override
    public void updateAggregatedDiseaseExtent(DiseaseGroup diseaseGroup) {
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroup.getId(), diseaseGroup.isGlobal());
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
     * Gets a list of disease occurrences for batching initialisation, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForBatchingInitialisation(int diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId);
    }

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The start date of the batch.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForBatching(int diseaseGroupId,
                                                                    DateTime batchStartDate, DateTime batchEndDate) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate);
    }

    /**
     * Gets a list of recent disease occurrences that have been validated (they have a target expert weighting).
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForTrainingPredictor(int diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId);
    }

    /**
     * Gets the number of occurrences that are eligible for being sent to the model, between the start and end batch
     * date. This helps to estimate whether the number of occurrences in a batch will be sufficient for a model run.
     * @param diseaseGroupId The disease group ID.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The number of occurrences that are eligible for being sent to the model.
     */
    @Override
    public long getNumberOfDiseaseOccurrencesEligibleForModelRun(int diseaseGroupId, DateTime startDate,
                                                                 DateTime endDate) {
        return diseaseOccurrenceDao.getNumberOfOccurrencesEligibleForModelRun(diseaseGroupId, startDate,
                endDate);
    }

    /**
     * Gets the bias occurrences that are should be used with a model run (for sample bias).
     * @param diseaseGroupId The disease group ID being modelled (will be excluded from bias set).
     * @param startDate The start date of the model run input data range.
     * @param endDate The end date  of the model run input data range.
     * @return The bias occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getBiasOccurrencesForModelRun(
            int diseaseGroupId, DateTime startDate, DateTime endDate) {
        return diseaseOccurrenceDao.getBiasOccurrencesForModelRun(diseaseGroupId, startDate, endDate);
    }

    /**
     * Gets the names of all disease groups to be shown in the HealthMap disease report (sorted).
     * @return The disease groups names.
     */
    public List<String> getDiseaseGroupNamesForHealthMapReport() {
        return diseaseGroupDao.getDiseaseGroupNamesForHealthMapReport();
    }

    /**
     * Returns the input date, with the number of days between scheduled model runs subtracted.
     * @param dateTime The input date.
     * @return The input date minus the number of days between scheduled model runs.
     */
    @Override
    public LocalDate subtractMaxDaysOnValidator(DateTime dateTime) {
        return dateTime.toLocalDate().minusDays(maxDaysOnValidator);
    }

    /**
     * Delete all of the occurrence that are labelled as bias for the specified disease group.
     * @param biasDisease Disease group for which to remove the bias points
     *                       (i.e. bias_disease_group_id, not disease_group_id).
     */
    @Override
    public void deleteBiasDiseaseOccurrencesForDisease(DiseaseGroup biasDisease) {
        diseaseOccurrenceDao.deleteDiseaseOccurrencesByBiasDiseaseId(biasDisease.getId());
    }

    private boolean isDiseaseGroupGlobal(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = getDiseaseGroupById(diseaseGroupId);
        return (diseaseGroup.isGlobal() != null && diseaseGroup.isGlobal());
    }
}
