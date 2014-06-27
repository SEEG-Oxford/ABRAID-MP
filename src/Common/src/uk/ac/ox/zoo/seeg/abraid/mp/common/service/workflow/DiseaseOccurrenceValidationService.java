package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOccurrenceValidationService {
    /**
     * Adds validation parameters to a disease occurrence.
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    boolean addValidationParameters(DiseaseOccurrence occurrence);
}
