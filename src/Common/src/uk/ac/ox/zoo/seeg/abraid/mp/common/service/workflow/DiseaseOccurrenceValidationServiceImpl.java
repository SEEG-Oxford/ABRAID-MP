package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;

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
     * Adds validation parameters to a disease occurrence, if the occurrence is eligible for validation.
     * If automatic model runs are enabled, all validation parameters are set. If they are disabled, then only
     * isValidated is set to true which marks it as ready for an initial model run (when requested).
     * @param occurrence The disease occurrence.
     * @return True if the disease occurrence is eligible for validation, otherwise false.
     */
    public boolean addValidationParametersWithChecks(DiseaseOccurrence occurrence) {
        if (isEligibleForValidation(occurrence)) {
            if (automaticModelRunsEnabled(occurrence)) {
                addValidationParameters(occurrence);
            } else {
                occurrence.setValidated(true);
            }
            return true;
        }
        return false;
    }

    /**
     * Adds validation parameters to a disease occurrence (without checks).
     * @param occurrence The disease occurrence.
     */
    public void addValidationParameters(DiseaseOccurrence occurrence) {
        occurrence.setEnvironmentalSuitability(findEnvironmentalSuitability(occurrence));
        occurrence.setDistanceFromDiseaseExtent(findDistanceFromDiseaseExtent(occurrence));
        occurrence.setMachineWeighting(findMachineWeighting(occurrence));
        occurrence.setValidated(findIsValidated(occurrence));
    }

    private boolean isEligibleForValidation(DiseaseOccurrence occurrence) {
        return (occurrence != null) && (occurrence.getLocation() != null) && occurrence.getLocation().hasPassedQc();
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private Double findEnvironmentalSuitability(DiseaseOccurrence occurrence) {
        return nativeSQL.findEnvironmentalSuitability(occurrence.getDiseaseGroup().getId(),
                occurrence.getLocation().getGeom());
    }

    private Double findDistanceFromDiseaseExtent(DiseaseOccurrence occurrence) {
        DistanceFromDiseaseExtentHelper helper = new DistanceFromDiseaseExtentHelper(nativeSQL);
        return helper.findDistanceFromDiseaseExtent(occurrence);
    }

    private Double findMachineWeighting(DiseaseOccurrence occurrence) {
        if (noModelRunsYet(occurrence)) {
            return null;
        }
        // For now, all machine weightings are null
        return null;
    }

    private boolean findIsValidated(DiseaseOccurrence occurrence) {
        if (noModelRunsYet(occurrence)) {
            return true;
        }
        // For now hardcode to true, but the proper behaviour will be implemented in a future story.
        return true;
    }

    private boolean noModelRunsYet(DiseaseOccurrence occurrence) {
        return (occurrence.getEnvironmentalSuitability() == null) &&
               (occurrence.getDistanceFromDiseaseExtent() == null);
    }
}
