package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.Collection;

/**
 * A factory for DiseaseExtentGenerationInputData objects populated with the appropriate data.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationInputDataSelector {
    private static final Logger LOGGER = Logger.getLogger(DiseaseExtentGenerationInputDataSelector.class);
    private static final String OCCURRENCE_WARNING_MESSAGE =
            "Attempting to select occurrences used in last validator extent update, for a modelling extent update, " +
            "but no occurrences were found. Falling back to occurrences for the current validator extent update.";
    private final DiseaseService diseaseService;
    private final ExpertService expertService;

    public DiseaseExtentGenerationInputDataSelector(
            DiseaseService diseaseService, ExpertService expertService) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
    }

    /**
     * Collates the input data for an extent generation when updating the validator disease extent.
     * @param diseaseGroup The disease group.
     * @param adminUnits The reference set of admin units.
     * @param isInitial If this is an initial disease extent.
     * @param process The type of process that is being performed (auto/manual/gold).
     * @param minimumOccurrenceDate  The minimum occurrence date of the data to be selected.
     * @return The extent generation input data.
     */
    public DiseaseExtentGenerationInputData selectForValidatorExtent(
            DiseaseGroup diseaseGroup, Collection<? extends AdminUnitGlobalOrTropical> adminUnits,
            boolean isInitial, DiseaseProcessType process, DateTime minimumOccurrenceDate) {
        return new DiseaseExtentGenerationInputData(
                retrieveDiseaseExtentClasses(),
                adminUnits,
                retrieveReviews(diseaseGroup),
                retrieveOccurrencesForValidatorExtent(diseaseGroup, isInitial, minimumOccurrenceDate, process));
    }

    /**
     * Collates the input data for an extent generation when updating the modelling disease extent.
     * This copies all of the data off the validatorExtentInputs object, but replaces the occurrences with the
     * occurrences from the previous update of the validator disease update.
     * @param diseaseGroup The disease group.
     * @param validatorExtentInputs The inputs to the current validator extent update (to use most of the same data).
     * @return The extent generation input data.
     */
    public DiseaseExtentGenerationInputData selectForModellingExtent(
            DiseaseGroup diseaseGroup, DiseaseExtentGenerationInputData validatorExtentInputs) {
        return new DiseaseExtentGenerationInputData(
                validatorExtentInputs.getDiseaseExtentClasses(),
                validatorExtentInputs.getAdminUnits(),
                validatorExtentInputs.getReviews(),
                retrieveOccurrencesUsedInLastExtent(diseaseGroup, validatorExtentInputs));
    }

    private Collection<DiseaseExtentClass> retrieveDiseaseExtentClasses() {
        // Retrieve a lookup table of disease extent classes
        return diseaseService.getAllDiseaseExtentClasses();
    }

    private Collection<AdminUnitReview> retrieveReviews(DiseaseGroup diseaseGroup) {
        // For an initial run this is likely to be empty, but if they have been provided they might as well be used.
        return expertService.getCurrentAdminUnitReviewsForDiseaseGroup(diseaseGroup.getId());
    }

    private Collection<DiseaseOccurrence> retrieveOccurrencesForValidatorExtent(
            DiseaseGroup diseaseGroup, boolean isInitial, DateTime minimumDate, DiseaseProcessType process) {
        // The minimum occurrence date is only relevant for non-initial automatic model runs
        DateTime safeMinimumOccurrenceDate = (isInitial || !process.isAutomatic()) ? null : minimumDate;
        // The validation weighting is only relevant for non-initial model runs
        Double safeMinimumValidationWeighting =
                (isInitial) ? null : diseaseGroup.getDiseaseExtentParameters().getMinValidationWeighting();

        // Find the occurrences to use.
        return diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroup.getId(),
                safeMinimumValidationWeighting,
                safeMinimumOccurrenceDate,
                process.isGoldStandard()
        );
    }

    private Collection<DiseaseOccurrence> retrieveOccurrencesUsedInLastExtent(
            DiseaseGroup diseaseGroup, DiseaseExtentGenerationInputData validatorExtentInputs) {
        Collection<DiseaseOccurrence> occurrences =
                diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences();
        if (occurrences.isEmpty()) {
            LOGGER.warn(OCCURRENCE_WARNING_MESSAGE);
            return validatorExtentInputs.getOccurrences();
        }

        return occurrences;
    }
}
