package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DiseaseOccurrenceValidationServiceImpl implements DiseaseOccurrenceValidationService {
    private NativeSQL nativeSQL;

    public DiseaseOccurrenceValidationServiceImpl(NativeSQL nativeSQL) {
        this.nativeSQL = nativeSQL;
    }

    /**
     * Adds validation parameters to a disease occurrence.
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    public boolean addValidationParameters(DiseaseOccurrence occurrence) {
        if (!isEligibleForValidation(occurrence)) {
            return false;
        }

        occurrence.setEnvironmentalSuitability(findEnvironmentalSuitability(occurrence));
        occurrence.setMachineWeighting(findMachineWeighting(occurrence));
        occurrence.setValidated(findIsValidated(occurrence));

        return true;
    }

    private boolean isEligibleForValidation(DiseaseOccurrence occurrence) {
        return (occurrence != null) && (occurrence.getLocation() != null) && occurrence.getLocation().hasPassedQc();
    }

    private Double findEnvironmentalSuitability(DiseaseOccurrence occurrence) {
        return nativeSQL.findEnvironmentalSuitability(occurrence.getDiseaseGroup().getId(),
                occurrence.getLocation().getGeom());
    }

    private Double findMachineWeighting(DiseaseOccurrence occurrence) {
        // For now, hardcode all machine weightings to 0.7
        return 0.7; ///CHECKSTYLE:SUPPRESS MagicNumberCheck
    }

    private boolean findIsValidated(DiseaseOccurrence occurrence) {
        return true;
    }
}
