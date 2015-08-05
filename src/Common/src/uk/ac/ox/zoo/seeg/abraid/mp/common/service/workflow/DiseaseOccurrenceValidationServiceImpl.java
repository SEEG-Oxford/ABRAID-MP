package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import ch.lambdaj.function.matcher.LambdaJMatcher;
import ch.lambdaj.group.Group;
import org.geotools.coverage.grid.GridCoverage2D;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.hamcrest.CoreMatchers.equalTo;

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
     * Adds validation parameters to a list of disease occurrences.
     * @param occurrences The list of disease occurrences.
     * @param performChecks If status checks should be performed.
     */
    @Override
    public void addValidationParameters(List<DiseaseOccurrence> occurrences, boolean performChecks) {
        // By default, set the occurrence to status READY
        clearAndSetToReady(occurrences);

        Group<DiseaseOccurrence> byDisease = group(occurrences, by(on(DiseaseOccurrence.class).getDiseaseGroup()));

        for (Group<DiseaseOccurrence> diseaseGrouping : byDisease.subgroups()) {
            DiseaseGroup disease = (DiseaseGroup) diseaseGrouping.key();
            List<DiseaseOccurrence> occurrencesForDisease = diseaseGrouping.findAll();

            if (performChecks) {
                // Discard QC failures
                List<DiseaseOccurrence> failedQC = filter(failedQC(), occurrencesForDisease);
                processFailedQc(failedQC);
                occurrencesForDisease.removeAll(failedQC);

                // Fast forward Gold Standard - passed QC
                List<DiseaseOccurrence> goldStandard = filter(isGoldStandard(), occurrencesForDisease);
                processGoldStandard(goldStandard);
                occurrencesForDisease.removeAll(goldStandard);

                if (!occurrencesForDisease.isEmpty()) {
                    if (!disease.isAutomaticModelRunsEnabled()) {
                        if (modelRunService.hasBatchingEverCompleted(disease.getId())) {
                            processAwaitingBatching(occurrencesForDisease);
                        }
                        // If there is no batching for this disease group, leave the status at the default (READY)
                        // so that it can be used in an initial model run and disease extent generation
                    } else {
                        // Add properties to automatic, non-gold standard, passed qc
                        processNeedsValidationParameters(occurrencesForDisease, disease);
                    }
                }
            } else {
                processNeedsValidationParameters(occurrencesForDisease, disease);
            }
        }
    }

    private void clearAndSetToReady(List<DiseaseOccurrence> occurrences) {
        // By default, the occurrence avoids the validation process altogether (i.e. marked READY with no validation
        // parameters
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setEnvironmentalSuitability(null);
            occurrence.setDistanceFromDiseaseExtent(null);
            occurrence.setMachineWeighting(null);
            occurrence.setValidationWeighting(null);
            occurrence.setFinalWeighting(null);
            occurrence.setFinalWeightingExcludingSpatial(null);
            occurrence.setStatus(DiseaseOccurrenceStatus.READY);
        }
    }

    private LambdaJMatcher<DiseaseOccurrence> failedQC() {
        return having(on(DiseaseOccurrence.class).getLocation().hasPassedQc(), equalTo(false));
    }

    private LambdaJMatcher<DiseaseOccurrence> isGoldStandard() {
        return having(on(DiseaseOccurrence.class).getAlert().getFeed().getProvenance().getName(),
                equalTo(ProvenanceNames.MANUAL_GOLD_STANDARD));
    }

    private void processFailedQc(List<DiseaseOccurrence> occurrences) {
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setStatus(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
        }
    }

    private void processGoldStandard(List<DiseaseOccurrence> occurrences) {
        // If the disease occurrence is from a "gold standard" data set, it should not be validated. So set its
        // final weightings to 1.
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setFinalWeighting(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
            occurrence.setFinalWeightingExcludingSpatial(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
        }
    }

    private void processAwaitingBatching(List<DiseaseOccurrence> occurrences) {
        // We are in disease group set-up and points are being batched for validation, so we need to set this point
        // to AWAITING_BATCHING
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setStatus(DiseaseOccurrenceStatus.AWAITING_BATCHING);
        }
    }

    private void processNeedsValidationParameters(List<DiseaseOccurrence> occurrences, DiseaseGroup diseaseGroup) {
        GridCoverage2D suitabilityRaster = null;
        GridCoverage2D[] adminRasters = null;
        try {
            suitabilityRaster = esHelper.getLatestMeanPredictionRaster(diseaseGroup);
            adminRasters = esHelper.getAdminRasters();
            // Group by location to avoid doing repeated (expensive) calculation for the same location
            Group<DiseaseOccurrence> byLocation = group(occurrences, by(on(DiseaseOccurrence.class).getLocation()));
            for (Group<DiseaseOccurrence> locationGrouping : byLocation.subgroups()) {
                Location location = (Location) locationGrouping.key();
                List<DiseaseOccurrence> locationOccurrences = locationGrouping.findAll();
                calculateAndSetValidationParametersAtLocation(
                        location, diseaseGroup, locationOccurrences, suitabilityRaster, adminRasters);
            }
        } finally {
            RasterUtils.disposeRaster(suitabilityRaster);
            RasterUtils.disposeRasters(adminRasters);
        }
    }

    private void calculateAndSetValidationParametersAtLocation(Location location, DiseaseGroup diseaseGroup,
            List<DiseaseOccurrence> occurrences, GridCoverage2D predictionRaster, GridCoverage2D[] adminRasters) {
        Double suitability = esHelper.findEnvironmentalSuitability(location, predictionRaster, adminRasters);
        Double distance = dfdeHelper.findDistanceFromDiseaseExtent(diseaseGroup, location);
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setEnvironmentalSuitability(suitability);
            occurrence.setDistanceFromDiseaseExtent(distance);
            findAndSetMachineWeightingAndInReview(occurrence);
        }
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

}
