package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

/**
 * Determines whether the steps in the daily disease process should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseProcessGatekeeper {
    private static final Logger LOGGER = Logger.getLogger(DiseaseProcessManager.class);
    private static final String MODEL_RUN_MESSAGE = "MODEL RUN FOR DISEASE GROUP %d (%s)";
    private static final String EXTENT_GENERATION_MESSAGE = "EXTENT GENERATION FOR DISEASE GROUP %d (%s)";
    private static final String NO_VALIDATION_PARAMETERS_THRESHOLDS =
            "Threshold (minNewLocationsTrigger, minEnvSuitability or minDistanceFromExtent) has not been defined";
    private static final String NEVER_BEEN_EXECUTED_BEFORE =
            "Process run has never been executed before for this disease group";
    private static final String NOT_ELAPSED = "Not enough days have elapsed since last process run on %s";
    private static final String ELAPSED = "Enough days have elapsed since last process run on %s";
    private static final String ENOUGH_NEW_LOCATIONS = "Number of new locations has exceeded minimum required";
    private static final String NOT_ENOUGH_NEW_LOCATIONS = "Number of new locations has not exceeded minimum value";
    private static final String STARTING_EXTENT_GENERATION = "Starting extent generation";
    private static final String NOT_STARTING_EXTENT_GENERATION = "Extent generation will not be executed";
    private static final String STARTING_MODEL_RUN = "Starting model run";
    private static final String NOT_STARTING_MODEL_RUN = "Model run will not be executed";

    private DiseaseService diseaseService;

    public DiseaseProcessGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run should be carried out.
     * NB. This method is only ever called for disease groups that have automatic model runs enabled, as a result of
     * diseaseService.getDiseaseGroupIdsForAutomaticModelRuns() in Main.
     *
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if:
     *  - there is no lastModelRunPrepDate for the disease group (ie model has never been run) or
     *  - more than a week has passed since last run, or
     *  - there have been more new locations than the minimum required for the disease group, since the last model run
     * Note that "a week" is configurable (i.e. days between model runs).
     * False if any of the 3 thresholds required for performing the number of distinct new locations check
     * (minNewLocationsTrigger, minEnvSuitability, minDistanceFromExtent) are not specified for the disease group.
     */
    public boolean modelShouldRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(MODEL_RUN_MESSAGE, diseaseGroupId, diseaseGroup.getName()));
        DateTime lastModelRunTimestamp = diseaseGroup.getLastModelRunPrepDate();

        boolean dueToRun =
                neverBeenRun(lastModelRunTimestamp) ||
                maxDaysBetweenProcessingElapsed(lastModelRunTimestamp) ||
                extentHasChanged(lastModelRunTimestamp, diseaseGroup) ||
                enoughNewLocations(lastModelRunTimestamp, diseaseGroup);

        LOGGER.info(dueToRun ? STARTING_MODEL_RUN : NOT_STARTING_MODEL_RUN);
        return dueToRun;
    }

    /**
     * Determines whether a disease extent update should be carried out.
     * NB. This method is only ever called for disease groups that have automatic model runs enabled, as a result of
     * diseaseService.getDiseaseGroupIdsForAutomaticModelRuns() in Main.
     *
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if:
     *  - there is no lastExtentGenerationDate for the disease group (ie extent generation has never been run) or
     *  - more than a week has passed since last extent generation, or
     *  - there have been more new locations than the minimum required for the disease group, since the last model run
     * Note that "a week" is configurable (i.e. days between model runs).
     * False if any of the 3 thresholds required for performing the number of distinct new locations check
     * (minNewLocationsTrigger, minEnvSuitability, minDistanceFromExtent) are not specified for the disease group.
     */
    public boolean extentShouldRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(EXTENT_GENERATION_MESSAGE, diseaseGroupId, diseaseGroup.getName()));
        DateTime lastExtentGenerationTimestamp = diseaseGroup.getLastExtentGenerationDate();
        DateTime lastModelRunTimestamp = diseaseGroup.getLastModelRunPrepDate();

        boolean dueToRun =
                neverBeenRun(lastExtentGenerationTimestamp) ||
                maxDaysBetweenProcessingElapsed(lastExtentGenerationTimestamp) ||
                enoughNewLocations(lastModelRunTimestamp, diseaseGroup);

        LOGGER.info(dueToRun ? STARTING_EXTENT_GENERATION : NOT_STARTING_EXTENT_GENERATION);
        return dueToRun;
    }

    private boolean neverBeenRun(DateTime lastProcessTimestamp) {
        if (lastProcessTimestamp == null) {
            LOGGER.info(NEVER_BEEN_EXECUTED_BEFORE);
            return true;
        }
        return false;
    }

    private boolean maxDaysBetweenProcessingElapsed(DateTime lastProcessTimestamp) {
        LocalDate comparisonDate = diseaseService.subtractDaysBetweenModelRuns(DateTime.now());
        LocalDate lastProcessDate = lastProcessTimestamp.toLocalDate();
        boolean maxDaysBetweenRunsElapsed = !comparisonDate.isBefore(lastProcessDate);
        LOGGER.info(String.format(maxDaysBetweenRunsElapsed ? ELAPSED : NOT_ELAPSED, lastProcessDate));
        return maxDaysBetweenRunsElapsed;
    }

    private boolean enoughNewLocations(DateTime lastProcessTimestamp, DiseaseGroup diseaseGroup) {
        // In this context "new" means locations that were not used in the last model run for this disease.
        if (thresholdsDefined(diseaseGroup)) {
            long count = getDistinctLocationsCount(lastProcessTimestamp, diseaseGroup);
            int minimum = diseaseGroup.getMinNewLocationsTrigger();

            boolean hasEnoughNewLocations = count >= minimum;
            LOGGER.info(hasEnoughNewLocations ? ENOUGH_NEW_LOCATIONS : NOT_ENOUGH_NEW_LOCATIONS);
            return hasEnoughNewLocations;
        } else {
            LOGGER.info(NO_VALIDATION_PARAMETERS_THRESHOLDS);
            return false;
        }
    }

    private boolean thresholdsDefined(DiseaseGroup diseaseGroup) {
        // minEnvironmentalSuitability and minDistanceFromDiseaseExtent thresholds are used when selecting the "new"
        // occurrences in getDistinctLocationsCount query, so we must ensure they are defined before continuing
        return (diseaseGroup.getMinNewLocationsTrigger() != null) &&
               (diseaseGroup.getMinEnvironmentalSuitability() != null) &&
               (diseaseGroup.getMinDistanceFromDiseaseExtent() != null);
    }

    private long getDistinctLocationsCount(DateTime lastProcessTimestamp, DiseaseGroup diseaseGroup) {
        return diseaseService.getDistinctLocationsCountForTriggeringModelRun(
                diseaseGroup, lastProcessTimestamp);
    }

    private boolean extentHasChanged(DateTime lastProcessTimestamp, DiseaseGroup diseaseGroup) {
        DateTime lastChangeTimestamp =
                diseaseService.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(diseaseGroup.getId());
        return lastChangeTimestamp != null && lastChangeTimestamp.isAfter(lastProcessTimestamp);
    }
}
