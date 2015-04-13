package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

/**
 * Determines whether the steps in the daily disease process should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseProcessGatekeeper {
    private static final Logger LOGGER = Logger.getLogger(DiseaseProcessManager.class);
    private static final String DISEASE_GROUP_ID_MESSAGE = "MODEL RUN PREPARATION FOR DISEASE GROUP %d (%s)";
    private static final String NO_VALIDATION_PARAMETERS_THRESHOLDS =
            "Threshold (minNewLocationsTrigger, minEnvSuitability or minDistanceFromExtent) has not been defined";
    private static final String NEVER_BEEN_EXECUTED_BEFORE =
            "Model run has never been executed before for this disease group";
    private static final String NOT_ELAPSED = "Not enough days have elapsed since last model run preparation on %s";
    private static final String ELAPSED = "Enough days have elapsed since last model run preparation on %s";
    private static final String ENOUGH_NEW_LOCATIONS = "Number of new locations has exceeded minimum required";
    private static final String NOT_ENOUGH_NEW_LOCATIONS = "Number of new locations has not exceeded minimum value";
    private static final String STARTING_MODEL_RUN_PREP = "Starting model run preparation";
    private static final String NOT_STARTING_MODEL_RUN_PREP = "Model run preparation will not be executed";

    private DiseaseService diseaseService;
    private ModelRunService modelRunService;

    public DiseaseProcessGatekeeper(DiseaseService diseaseService, ModelRunService modelRunService) {
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
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
     *  - there have been more new locations (more than a week ago) than the minimum required for the disease group.
     * Note that "a week" is configurable (i.e. days between model runs).
     * False if any of the 3 thresholds required for performing the number of distinct new locations check
     * (minNewLocationsTrigger, minEnvSuitability, minDistanceFromExtent) are not specified for the disease group.
     */
    public boolean modelShouldRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId, diseaseGroup.getName()));

        boolean dueToRun = neverBeenRunOrDaysBetweenRunsElapsed(diseaseGroup) || enoughNewLocations(diseaseGroup);

        LOGGER.info(dueToRun ? STARTING_MODEL_RUN_PREP : NOT_STARTING_MODEL_RUN_PREP);
        return dueToRun;
    }

    /**
     * Determines whether a disease extent update should be carried out.
     * NB. This method is only ever called for disease groups that have automatic model runs enabled, as a result of
     * diseaseService.getDiseaseGroupIdsForAutomaticModelRuns() in Main.
     *
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return For now this just returns the same as modelShouldRun.
     */
    public boolean extentShouldRun(int diseaseGroupId) {
        return modelShouldRun(diseaseGroupId);
    }

    private boolean neverBeenRunOrDaysBetweenRunsElapsed(DiseaseGroup diseaseGroup) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        if (lastModelRunPrepDate == null) {
            LOGGER.info(NEVER_BEEN_EXECUTED_BEFORE);
            return true;
        } else {
            DateTime comparisonDate = modelRunService.subtractDaysBetweenModelRuns(DateTime.now());
            DateTime lastModelRunPrepDay = lastModelRunPrepDate.withTimeAtStartOfDay();
            final boolean daysBetweenRunsElapsed = !comparisonDate.isBefore(lastModelRunPrepDay);
            LOGGER.info(String.format(daysBetweenRunsElapsed ? ELAPSED : NOT_ELAPSED, lastModelRunPrepDate));
            return daysBetweenRunsElapsed;
        }
    }

    private boolean enoughNewLocations(DiseaseGroup diseaseGroup) {
        if (thresholdsDefined(diseaseGroup)) {
            long count = getDistinctLocationsCount(diseaseGroup.getId());
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

    private long getDistinctLocationsCount(int diseaseGroupId) {
        DateTime endDate = modelRunService.subtractDaysBetweenModelRuns(DateTime.now());
        DateTime startDate = modelRunService.subtractDaysBetweenModelRuns(endDate);
        return diseaseService.getDistinctLocationsCountForTriggeringModelRun(diseaseGroupId, startDate, endDate);
    }
}
