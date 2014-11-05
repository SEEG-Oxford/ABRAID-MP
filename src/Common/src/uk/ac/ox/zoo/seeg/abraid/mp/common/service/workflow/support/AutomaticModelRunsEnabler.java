package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Set model runs to be triggered automatically for a given disease group.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AutomaticModelRunsEnabler {

    private static final String ENABLING_AUTOMATIC_MODEL_RUNS =
            "Enabling automatic model runs for disease group %d (%s)";
    private static final String SAVING_AUTOMATIC_MODEL_RUNS_START_DATE =
            "Saving automatic model runs start date on disease group %d as %s";
    private static final String SETTING_CLASS_CHANGED_DATE =
            "Setting class changed date on all admin unit disease extent classes for disease group %d to %s";
    private static final String ADDING_VALIDATION_PARAMETERS =
            "Adding validation parameters to %d occurrence(s) currently without final weighting";
    private static final String ADDING_DEFAULT_PARAMETERS =
            "%d occurrence(s) occurring before %s will be ignored in future";

    private static final Logger LOGGER = Logger.getLogger(AutomaticModelRunsEnabler.class);

    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private ModelRunService modelRunService;

    public AutomaticModelRunsEnabler(DiseaseService diseaseService,
                                     DiseaseOccurrenceValidationService diseaseOccurrenceValidationService,
                                     ModelRunService modelRunService) {
        this.diseaseService = diseaseService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
        this.modelRunService = modelRunService;
    }

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    public void enable(int diseaseGroupId) {
        DateTime now = DateTime.now();
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        LOGGER.info(String.format(ENABLING_AUTOMATIC_MODEL_RUNS, diseaseGroupId, diseaseGroup.getName()));

        saveAutomaticModelRunsStartDate(diseaseGroup, now);

        List<DiseaseOccurrence> occurrences = getOccurrencesWithoutAFinalWeighting(diseaseGroupId);
        addValidationParametersOrWeightings(occurrences);
        saveOccurrences(occurrences);
    }

    private void saveAutomaticModelRunsStartDate(DiseaseGroup diseaseGroup, DateTime now) {
        LOGGER.info(String.format(SAVING_AUTOMATIC_MODEL_RUNS_START_DATE, diseaseGroup.getId(), now));
        diseaseGroup.setAutomaticModelRunsStartDate(now);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private List<DiseaseOccurrence> getOccurrencesWithoutAFinalWeighting(int diseaseGroupId) {
        return diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId);
    }

    private void addValidationParametersOrWeightings(List<DiseaseOccurrence> occurrences) {
        // Adds validation parameters for occurrences without a final weighting, using a cutoff date of the number of
        // days between model runs. This ensures that that experts are not overwhelmed with occurrences to validate.
        // Occurrences before the cutoff date are permanently ignored by setting their isValidated flag to null.
        DateTime earliestDateForValidationParameters = modelRunService.subtractDaysBetweenModelRuns(DateTime.now());
        List<DiseaseOccurrence> occurrencesForValidationParameters = new ArrayList<>();

        for (DiseaseOccurrence occurrence : occurrences) {
            if (occurrence.getOccurrenceDate().isBefore(earliestDateForValidationParameters)) {
                discardOccurrence(occurrence);
            } else {
                occurrencesForValidationParameters.add(occurrence);
            }
        }

        log(occurrences.size(), occurrencesForValidationParameters.size(), earliestDateForValidationParameters);
        diseaseOccurrenceValidationService.addValidationParameters(occurrencesForValidationParameters);
    }

    private void discardOccurrence(DiseaseOccurrence occurrence) {
        occurrence.setStatus(DiseaseOccurrenceStatus.DISCARDED_UNUSED);
        occurrence.setFinalWeighting(null);
        occurrence.setFinalWeightingExcludingSpatial(null);
    }

    private void log(int numOccurrences, int numForAddingParameters, DateTime date) {
        LOGGER.info(String.format(ADDING_VALIDATION_PARAMETERS, numForAddingParameters));
        int numCleared = numOccurrences - numForAddingParameters;
        LOGGER.info(String.format(ADDING_DEFAULT_PARAMETERS, numCleared, date));
    }

    private void saveOccurrences(List<DiseaseOccurrence> occurrences) {
        for (DiseaseOccurrence occurrence : occurrences) {
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }
}
