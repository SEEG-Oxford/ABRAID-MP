package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;

import java.util.List;

/**
 * Adds validation parameters to a disease occurrence. Marks it for manual validation (via the Data Validator GUI)
 * if appropriate.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DiseaseOccurrenceValidationServiceImpl implements DiseaseOccurrenceValidationService {
    private EnvironmentalSuitabilityHelper esHelper;
    private DistanceFromDiseaseExtentHelper dfdeHelper;
    private MachineWeightingPredictor mwPredictor;
    private ModelRunService modelRunService;

    public DiseaseOccurrenceValidationServiceImpl(EnvironmentalSuitabilityHelper esHelper,
                                                  DistanceFromDiseaseExtentHelper dfdeHelper,
                                                  MachineWeightingPredictor mwPredictor,
                                                  ModelRunService modelRunService) {
        this.esHelper = esHelper;
        this.dfdeHelper = dfdeHelper;
        this.mwPredictor = mwPredictor;
        this.modelRunService = modelRunService;
    }

    /**
     * Adds validation parameters to a disease occurrence, including various checks.
     * @param occurrence The disease occurrence.
     */
    @Override
    public void addValidationParametersWithChecks(DiseaseOccurrence occurrence) {
        // By default, set the occurrence to status READY
        clearAndSetToReady(occurrence);
        if (hasFailedQc(occurrence)) {
            setToFailedQc(occurrence);
        } else if (isBias(occurrence)) {
            setToBias(occurrence);
        } else if (isGoldStandard(occurrence)) {
            handleGoldStandard(occurrence);
        } else if (automaticModelRunsEnabled(occurrence)) {
            handleAutomaticModelRunsEnabled(occurrence);
        } else {
            handleAutomaticModelRunsDisabled(occurrence);
        }
    }

    /**
     * Adds validation parameters to a list of disease occurrences (without checks).
     * Every occurrence must belong to the same disease group.
     * @param occurrences The list of disease occurrences.
     */
    @Override
    public void addValidationParameters(List<DiseaseOccurrence> occurrences) {
        DiseaseGroup diseaseGroup = validateAndGetDiseaseGroup(occurrences);

        if (diseaseGroup != null) {
            // Get the latest mean prediction raster for the disease group, and then use it to add validation parameters
            // to all occurrences
            GridCoverage2D suitabilityRaster = null;
            GridCoverage2D[] adminRasters = null;
            try {
                suitabilityRaster = esHelper.getLatestMeanPredictionRaster(diseaseGroup);
                adminRasters = esHelper.getAdminRasters();
                for (DiseaseOccurrence occurrence : occurrences) {
                    clearAndSetToReady(occurrence);
                    addValidationParameters(occurrence, suitabilityRaster, adminRasters);
                }
            } finally {
                RasterUtils.disposeRaster(suitabilityRaster);
                RasterUtils.disposeRasters(adminRasters);
            }
        }
    }

    private void clearAndSetToReady(DiseaseOccurrence occurrence) {
        // By default, the occurrence avoids the validation process altogether (i.e. marked READY with no validation
        // parameters
        occurrence.setEnvironmentalSuitability(null);
        occurrence.setDistanceFromDiseaseExtent(null);
        occurrence.setMachineWeighting(null);
        occurrence.setValidationWeighting(null);
        occurrence.setFinalWeighting(null);
        occurrence.setFinalWeightingExcludingSpatial(null);
        occurrence.setStatus(DiseaseOccurrenceStatus.READY);
    }

    private boolean hasFailedQc(DiseaseOccurrence occurrence) {
        return (occurrence.getLocation() == null) || !occurrence.getLocation().hasPassedQc();
    }

    private boolean isBias(DiseaseOccurrence occurrence) {
        return occurrence.getBiasDisease() != null;
    }

    private boolean isGoldStandard(DiseaseOccurrence occurrence) {
        return occurrence.getAlert().getFeed().getProvenance().getName().equals(ProvenanceNames.MANUAL_GOLD_STANDARD);
    }

    private void setToFailedQc(DiseaseOccurrence occurrence) {
        occurrence.setStatus(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
    }

    private void setToBias(DiseaseOccurrence occurrence) {
        occurrence.setStatus(DiseaseOccurrenceStatus.BIAS);
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private void handleGoldStandard(DiseaseOccurrence occurrence) {
        // If the disease occurrence is from a "gold standard" data set, it should not be validated. So set its
        // final weightings to 1.
        occurrence.setFinalWeighting(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
        occurrence.setFinalWeightingExcludingSpatial(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
    }

    private void handleAutomaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        addValidationParameters(occurrence);
    }

    private void handleAutomaticModelRunsDisabled(DiseaseOccurrence occurrence) {
        if (modelRunService.hasBatchingEverCompleted(occurrence.getDiseaseGroup().getId())) {
            // We are in disease group set-up and points are being batched for validation, so we need to set this point
            // to AWAITING_BATCHING
            occurrence.setStatus(DiseaseOccurrenceStatus.AWAITING_BATCHING);
        }

        // If there is no batching for this disease group, leave the status at the default (READY) so that it can be
        // used in an initial model run and disease extent generation
    }

    private void addValidationParameters(DiseaseOccurrence occurrence) {
        GridCoverage2D suitabilityRaster = null;
        GridCoverage2D[] adminRasters = null;
        try {
            suitabilityRaster = esHelper.getLatestMeanPredictionRaster(occurrence.getDiseaseGroup());
            adminRasters = esHelper.getSingleAdminRaster(occurrence.getLocation().getPrecision());
            addValidationParameters(occurrence, suitabilityRaster, adminRasters);
        } finally {
            RasterUtils.disposeRaster(suitabilityRaster);
            RasterUtils.disposeRasters(adminRasters);
        }
    }

    private void addValidationParameters(
            DiseaseOccurrence occurrence, GridCoverage2D predictionRaster, GridCoverage2D[] adminRasters) {
        occurrence.setEnvironmentalSuitability(
                esHelper.findEnvironmentalSuitability(occurrence, predictionRaster, adminRasters));
        occurrence.setDistanceFromDiseaseExtent(dfdeHelper.findDistanceFromDiseaseExtent(occurrence));
        findAndSetMachineWeightingAndInReview(occurrence);
    }

    private void findAndSetMachineWeightingAndInReview(DiseaseOccurrence occurrence) {
        if (shouldGoDirectlyToDataValidator(occurrence)) {
            addOccurrenceToValidator(occurrence);
        } else {
            if (occurrence.getDiseaseGroup().useMachineLearning()) {
                Double machineWeighting = mwPredictor.findMachineWeighting(occurrence);
                if (machineWeighting == null) {
                    addOccurrenceToValidator(occurrence);
                } else {
                    occurrence.setMachineWeighting(machineWeighting);
                }
            } else {
                if (shouldSendToDataValidatorWithoutUsingMachineLearning(occurrence)) {
                    addOccurrenceToValidator(occurrence);
                } else {
                    occurrence.setMachineWeighting(1.0);
                }
            }
        }
    }

    private boolean shouldGoDirectlyToDataValidator(DiseaseOccurrence occurrence) {
        // Prevent MW before auto (batching)
        // Prevent null ES or DV in case something has gone wrong
        return
                (occurrence.getEnvironmentalSuitability() == null) ||
                (occurrence.getDistanceFromDiseaseExtent() == null) ||
                !occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private void addOccurrenceToValidator(DiseaseOccurrence occurrence) {
        if (occurrence.getLocation().isModelEligible()) {
            occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        }
    }

    private boolean shouldSendToDataValidatorWithoutUsingMachineLearning(DiseaseOccurrence occurrence) {
        return lowEnvironmentalSuitability(occurrence) || outsideExtent(occurrence);
    }

    private boolean lowEnvironmentalSuitability(DiseaseOccurrence occurrence) {
        Double maxEnvironmentalSuitability = occurrence.getDiseaseGroup().getMaxEnvironmentalSuitabilityWithoutML();
        return (maxEnvironmentalSuitability != null) &&
               (occurrence.getEnvironmentalSuitability() <= maxEnvironmentalSuitability);
    }

    private boolean outsideExtent(DiseaseOccurrence occurrence) {
        return (occurrence.getDistanceFromDiseaseExtent() > 0);
    }

    private DiseaseGroup validateAndGetDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        DiseaseGroup diseaseGroup = null;
        if (occurrences != null && occurrences.size() > 0) {
            // Get the disease group of the first occurrence in the list
            diseaseGroup = occurrences.get(0).getDiseaseGroup();
            // Ensure that all other occurrences have the same disease group
            for (int i = 1; i < occurrences.size(); i++) {
                DiseaseGroup otherDiseaseGroup = occurrences.get(i).getDiseaseGroup();
                if (otherDiseaseGroup == null || !diseaseGroup.getId().equals(otherDiseaseGroup.getId())) {
                    throw new RuntimeException("All occurrences must have the same disease group");
                }
            }
        }
        return diseaseGroup;
    }
}
