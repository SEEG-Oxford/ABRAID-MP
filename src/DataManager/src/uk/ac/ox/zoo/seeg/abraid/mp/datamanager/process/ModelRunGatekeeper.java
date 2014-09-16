package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
    private static final String AUTOMATIC_MODEL_RUNS_NOT_ENABLED =
            "Automatic model runs have not been enabled for this disease group";
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

    private DiseaseService diseaseService;

    public ModelRunGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run preparation tasks should be carried out.
     * NB: It is assumed that the specified disease group has automatic model runs enabled as a result of a
     * previous query.
     *
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if:
     *  - there is no lastModelRunPrepDate for the disease group (ie model has never been run) or
     *  - more than a week has passed since last run, or
     *  - there have been more new locations since the last run than the minimum required for the disease group.
     * False if:
     *  - automatic model runs are not enabled for the disease group, or
     *  - any of the 3 thresholds for determining whether the number of distinct new locations (minNewLocationsTrigger,
     *    minEnvSuitability, minDistanceFromExtent) are not specified for the disease group.
     */
    public boolean modelShouldRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId, diseaseGroup.getName()));

        boolean shouldRun = automaticModelRunsEnabled(diseaseGroup) && dueToRun(diseaseGroup);
        LOGGER.info(shouldRun ? STARTING_MODEL_RUN_PREP : NOT_STARTING_MODEL_RUN_PREP);
        return shouldRun;
    }

    private boolean automaticModelRunsEnabled(DiseaseGroup diseaseGroup) {
        boolean automaticModelRuns = diseaseGroup.isAutomaticModelRunsEnabled();
        if (!automaticModelRuns) {
            LOGGER.info(AUTOMATIC_MODEL_RUNS_NOT_ENABLED);
        }
        return automaticModelRuns;
    }

    private boolean dueToRun(DiseaseGroup diseaseGroup) {
        return neverBeenRunOrWeekHasElapsed(diseaseGroup) || enoughNewLocations(diseaseGroup);
    }

    private boolean neverBeenRunOrWeekHasElapsed(DiseaseGroup diseaseGroup) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        if (lastModelRunPrepDate == null) {
            LOGGER.info(NEVER_BEEN_EXECUTED_BEFORE);
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastModelRunPrepDate.toLocalDate().plusWeeks(1);
            final boolean weekHasElapsed = !comparisonDate.isAfter(today);
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
        List<DiseaseOccurrence> occurrences = diseaseService.getNewOccurrencesByDiseaseGroup(diseaseGroupId);
        Set<Location> locations = new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        return locations.size();
    }
}
