package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

/**
 * Determines whether the model run should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeper {
    private static final Logger LOGGER = Logger.getLogger(ModelRunManager.class);
    private static final String DISEASE_GROUP_ID_MESSAGE = "MODEL RUN PREPARATION FOR DISEASE GROUP %d";
    private static final String NO_MODEL_RUN_MIN_NEW_OCCURRENCES =
            "No min new occurrences threshold has been defined for this disease group";
    private static final String NEVER_BEEN_EXECUTED_BEFORE =
            "Model run has never been executed before for this disease group";
    private static final String WEEK_HAS_NOT_ELAPSED = "A week has not elapsed since last model run preparation on %s";
    private static final String WEEK_HAS_ELAPSED = "At least a week has elapsed since last model run preparation on %s";
    private static final String ENOUGH_NEW_OCCURRENCES = "Number of new occurrences has exceeded minimum required";
    private static final String NOT_ENOUGH_NEW_OCCURRENCES = "Number of new occurrences has not exceeded minimum value";
    private static final String AUTOMATIC_RUNS_NOT_ENABLED =
            "Skipping model run preparation and request for disease group %d - Automatic model runs are not enabled";
    private static final String STARTING_MODEL_RUN_PREP = "Starting model run preparation";
    private static final String NOT_STARTING_MODEL_RUN_PREP = "Model run preparation will not be executed";

    private DiseaseService diseaseService;

    ModelRunGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run preparation tasks should be carried out.
     * @param lastModelRunPrepDate The date on which the model preparation tasks were last executed.
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if there is no lastModelRunPrepDate for disease, or more than a week has passed since last run, or
     * there have been more new occurrences since the last run than the minimum required for the disease group.
     * False if the minimum number of new occurrences value is not specified for the disease group.
     */
    public boolean modelShouldRun(int diseaseGroupId, DateTime lastModelRunPrepDate) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            LOGGER.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId));
            boolean dueToRun = dueToRun(lastModelRunPrepDate, diseaseGroup);
            LOGGER.info(dueToRun ? STARTING_MODEL_RUN_PREP : NOT_STARTING_MODEL_RUN_PREP);
            return dueToRun;
        } else {
            LOGGER.info(String.format(AUTOMATIC_RUNS_NOT_ENABLED, diseaseGroupId));
            return false;
        }
    }

    private boolean dueToRun(DateTime lastModelRunPrepDate, DiseaseGroup diseaseGroup) {
        if (diseaseGroup.getModelRunMinNewOccurrences() == null) {
            LOGGER.info(NO_MODEL_RUN_MIN_NEW_OCCURRENCES);
            return false;
        } else {
            return weekHasElapsed(lastModelRunPrepDate) || enoughNewOccurrences(diseaseGroup);
        }
    }

    private boolean weekHasElapsed(DateTime lastModelRunPrepDate) {
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

    private boolean enoughNewOccurrences(DiseaseGroup diseaseGroup) {
        long count = diseaseService.getNewOccurrencesCountByDiseaseGroup(diseaseGroup.getId());
        int min = diseaseGroup.getModelRunMinNewOccurrences();
        final boolean hasEnoughNewOccurrences = count > min;
        LOGGER.info(hasEnoughNewOccurrences ? ENOUGH_NEW_OCCURRENCES : NOT_ENOUGH_NEW_OCCURRENCES);
        return hasEnoughNewOccurrences;
    }
}
