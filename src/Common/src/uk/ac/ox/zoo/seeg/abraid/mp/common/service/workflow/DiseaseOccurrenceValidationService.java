package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.List;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOccurrenceValidationService {
    /**
     * Adds validation parameters to a disease occurrence, if the occurrence is eligible for validation.
     * If automatic model runs are enabled, all validation parameters are set. If they are disabled, then only
     * isValidated is set to true which marks it as ready for an initial model run (when requested).
     * @param occurrence The disease occurrence.
     * @param isGoldStandard Whether or not this is a "gold standard" disease occurrence (i.e. should not be validated).
     */
    void addValidationParametersWithChecks(DiseaseOccurrence occurrence, boolean isGoldStandard);

    /**
     * Adds validation parameters to a list of disease occurrences (without checks). Each occurrence must belong to
     * the same disease group.
     * @param occurrences The list of disease occurrences.
     */
    void addValidationParameters(List<DiseaseOccurrence> occurrences);
}
