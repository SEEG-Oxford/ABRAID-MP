package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Determines whether the model run should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeper {
    private static final Logger LOGGER = Logger.getLogger(ModelRunManager.class);
    private static final String DISEASE_GROUP_ID_MESSAGE = "MODEL RUN PREPARATION FOR DISEASE GROUP %d (%s)";
    private static final String NO_VALIDATION_PARAMETERS_THRESHOLDS =
            "Threshold (minNewLocationsTrigger, minEnvSuitability or minDistanceFromExtent) has not been defined";
    private static final String NEVER_BEEN_EXECUTED_BEFORE =
            "Model run has never been executed before for this disease group";
    private static final String WEEK_HAS_NOT_ELAPSED = "A week has not elapsed since last model run preparation on %s";
    private static final String WEEK_HAS_ELAPSED = "At least a week has elapsed since last model run preparation on %s";
    private static final String ENOUGH_NEW_LOCATIONS = "Number of new locations has exceeded minimum required";
    private static final String NOT_ENOUGH_NEW_LOCATIONS = "Number of new locations has not exceeded minimum value";
    private static final String STARTING_MODEL_RUN_PREP = "Starting model run preparation";
    private static final String NOT_STARTING_MODEL_RUN_PREP = "Model run preparation will not be executed";

    private static final int DAYS_BETWEEN_MODEL_RUNS = 7;

    private DiseaseService diseaseService;

    public ModelRunGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run preparation tasks should be carried out.
     * NB. This method is only ever called for disease groups that have automatic model runs enabled, as a result of
     * modelRunManager.getDiseaseGroupIdsForAutomaticModelRuns() in Main.
     *
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if:
     *  - there is no lastModelRunPrepDate for the disease group (ie model has never been run) or
     *  - more than a week has passed since last run, or
     *  - there have been more new locations (more than a week ago) than the minimum required for the disease group.
     * False if any of the 3 thresholds required for performing the number of distinct new locations check
     * (minNewLocationsTrigger, minEnvSuitability, minDistanceFromExtent) are not specified for the disease group.
     */
    public boolean modelShouldRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId, diseaseGroup.getName()));

        boolean dueToRun = neverBeenRunOrWeekHasElapsed(diseaseGroup) || enoughNewLocations(diseaseGroup);

        LOGGER.info(dueToRun ? STARTING_MODEL_RUN_PREP : NOT_STARTING_MODEL_RUN_PREP);
        return dueToRun;
    }

    private boolean neverBeenRunOrWeekHasElapsed(DiseaseGroup diseaseGroup) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        if (lastModelRunPrepDate == null) {
            LOGGER.info(NEVER_BEEN_EXECUTED_BEFORE);
            return true;
        } else {
            DateTime comparisonDate = subtractDaysBetweenModelRuns(DateTime.now());
            DateTime lastModelRunPrepDay = lastModelRunPrepDate.withTimeAtStartOfDay();
            final boolean weekHasElapsed = !comparisonDate.isBefore(lastModelRunPrepDay);
            LOGGER.info(String.format(weekHasElapsed ? WEEK_HAS_ELAPSED : WEEK_HAS_NOT_ELAPSED, lastModelRunPrepDate));
            return weekHasElapsed;
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
        return (diseaseGroup.getMinNewLocationsTrigger() != null) &&
               (diseaseGroup.getMinEnvironmentalSuitability() != null) &&
               (diseaseGroup.getMinDistanceFromDiseaseExtent() != null);
    }

    private long getDistinctLocationsCount(int diseaseGroupId) {
        DateTime endDate = subtractDaysBetweenModelRuns(DateTime.now());
        DateTime startDate = subtractDaysBetweenModelRuns(endDate);

        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForTriggeringModelRun(diseaseGroupId, startDate, endDate);
        Set<Location> locations = new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        return locations.size();
    }

    private DateTime subtractDaysBetweenModelRuns(DateTime endDate) {
        return endDate.minusDays(DAYS_BETWEEN_MODEL_RUNS).withTimeAtStartOfDay();
    }
}
