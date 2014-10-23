package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;

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

    public DiseaseOccurrenceValidationServiceImpl(EnvironmentalSuitabilityHelper esHelper,
                                                  DistanceFromDiseaseExtentHelper dfdeHelper,
                                                  MachineWeightingPredictor mwPredictor) {
        this.esHelper = esHelper;
        this.dfdeHelper = dfdeHelper;
        this.mwPredictor = mwPredictor;
    }

    /**
     * Adds validation parameters to a disease occurrence, including various checks.
     * @param occurrence The disease occurrence.
     * @param isGoldStandard Whether or not this is a "gold standard" disease occurrence (i.e. should not be validated).
     */
    @Override
    public void addValidationParametersWithChecks(DiseaseOccurrence occurrence, boolean isGoldStandard) {
        // By default, set the occurrence to status READY
        clearAndSetToReady(occurrence);
        if (hasPassedQc(occurrence)) {
            if (isGoldStandard) {
                // Passed QC and is a gold standard occurrence
                addGoldStandardParameters(occurrence);
            } else if (automaticModelRunsEnabled(occurrence)) {
                // Passed QC and automatic model runs are enabled, so add validation parameters. If automatic model
                // runs are disabled, leave the status at READY so that it can be used in an initial model run and
                // disease extent generation.
                addValidationParameters(occurrence);
            }
        } else {
            // Failed QC, so set the status accordingly
            setToFailedQc(occurrence);
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
            GridCoverage2D raster = esHelper.getLatestMeanPredictionRaster(diseaseGroup);
            for (DiseaseOccurrence occurrence : occurrences) {
                clearAndSetToReady(occurrence);
                addValidationParameters(occurrence, raster);
            }
        }
    }

    private boolean hasPassedQc(DiseaseOccurrence occurrence) {
        return (occurrence.getLocation() != null) && occurrence.getLocation().hasPassedQc();
    }

    private void clearAndSetToReady(DiseaseOccurrence occurrence) {
        // By default, the occurrence avoids the validation process altogether (i.e. marked READY with no validation
        // parameters
        occurrence.setEnvironmentalSuitability(null);
        occurrence.setDistanceFromDiseaseExtent(null);
        occurrence.setMachineWeighting(null);
        occurrence.setStatus(DiseaseOccurrenceStatus.READY);
    }

    private void setToFailedQc(DiseaseOccurrence occurrence) {
        occurrence.setStatus(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
    }

    private void addGoldStandardParameters(DiseaseOccurrence occurrence) {
        // If the disease occurrence is from a "gold standard" data set, it should not be validated. So set its
        // final weightings to 1.
        occurrence.setFinalWeighting(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
        occurrence.setFinalWeightingExcludingSpatial(DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING);
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    private void addValidationParameters(DiseaseOccurrence occurrence) {
        GridCoverage2D raster = esHelper.getLatestMeanPredictionRaster(occurrence.getDiseaseGroup());
        addValidationParameters(occurrence, raster);
    }

    private void addValidationParameters(DiseaseOccurrence occurrence, GridCoverage2D raster) {
        if (!isCountryPoint(occurrence)) {
            occurrence.setEnvironmentalSuitability(esHelper.findEnvironmentalSuitability(occurrence, raster));
            occurrence.setDistanceFromDiseaseExtent(dfdeHelper.findDistanceFromDiseaseExtent(occurrence));
            findAndSetMachineWeightingAndInReview(occurrence);
        }
    }

    private boolean isCountryPoint(DiseaseOccurrence occurrence) {
        return occurrence.getLocation().getPrecision() == LocationPrecision.COUNTRY;
    }

    private void findAndSetMachineWeightingAndInReview(DiseaseOccurrence occurrence) {
        if ((occurrence.getEnvironmentalSuitability() == null) || (occurrence.getDistanceFromDiseaseExtent() == null)) {
            occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        } else {
            if (occurrence.getDiseaseGroup().useMachineLearning()) {
                Double machineWeighting = mwPredictor.findMachineWeighting(occurrence);
                if (machineWeighting == null) {
                    occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
                } else {
                    occurrence.setMachineWeighting(machineWeighting);
                }
            } else {
                if (shouldSendToDataValidatorWithoutUsingMachineLearning(occurrence)) {
                    occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
                } else {
                    occurrence.setMachineWeighting(1.0);
                }
            }
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
