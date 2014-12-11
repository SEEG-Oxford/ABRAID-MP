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
     * Adds validation parameters to a disease occurrence, including various checks.
     * @param occurrence The disease occurrence.
     */
    void addValidationParametersWithChecks(DiseaseOccurrence occurrence);

    /**
     * Adds validation parameters to a list of disease occurrences (without checks). Each occurrence must belong to
     * the same disease group.
     * @param occurrences The list of disease occurrences.
     */
    void addValidationParameters(List<DiseaseOccurrence> occurrences);
}
