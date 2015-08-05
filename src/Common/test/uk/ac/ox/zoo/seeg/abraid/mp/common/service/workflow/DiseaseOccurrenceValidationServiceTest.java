package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the DiseaseOccurrenceValidationService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceValidationServiceTest {
    private DiseaseOccurrenceValidationService service;
    private EnvironmentalSuitabilityHelper esHelper;
    private DistanceFromDiseaseExtentHelper dfdeHelper;
    private MachineWeightingPredictor mwPredictor;
    private ModelRunService modelRunService;

    @Before
    public void setUp() {
        esHelper = mock(EnvironmentalSuitabilityHelper.class);
        dfdeHelper = mock(DistanceFromDiseaseExtentHelper.class);
        mwPredictor = mock(MachineWeightingPredictor.class);
        modelRunService = mock(ModelRunService.class);
        service = new DiseaseOccurrenceValidationServiceImpl(esHelper, dfdeHelper, mwPredictor, modelRunService);
    }

    private void setIsGoldStandardProvenance(DiseaseOccurrence occurrence, boolean isGoldStandard) {
        String provenanceName = isGoldStandard ? ProvenanceNames.MANUAL_GOLD_STANDARD : ProvenanceNames.MANUAL;
        Provenance provenance = new Provenance(provenanceName);

        Feed feed = new Feed("Test feed", provenance);
        Alert alert = new Alert();
        alert.setFeed(feed);
        occurrence.setAlert(alert);
    }

    @Test
    public void addValidationParametersWithChecksDiscardsOccurrenceIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true, false);
        occurrence.getLocation().setHasPassedQc(false);
        setIsGoldStandardProvenance(occurrence, false);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksDiscardsOccurrenceIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsAreDisabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, false, false);
        occurrence.getLocation().setHasPassedQc(false);
        setIsGoldStandardProvenance(occurrence, false);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSetsValidationParametersWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(occurrence)).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSetsStatusToReadyWhenAutomaticModelRunsAreDisabledAndNoBatching() {
        // Arrange
        int diseaseGroupId = 30;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false, false);
        setIsGoldStandardProvenance(occurrence, false);
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(false);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertDefaultParameters(occurrence, DiseaseOccurrenceStatus.READY);
    }

    @Test
    public void addValidationParametersWithChecksSetsStatusToAwaitingBatchingWhenAutomaticModelRunsAreDisabledAndBatching() {
        // Arrange
        int diseaseGroupId = 30;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false, false);
        setIsGoldStandardProvenance(occurrence, false);
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(true);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertDefaultParameters(occurrence, DiseaseOccurrenceStatus.AWAITING_BATCHING);
    }

    private void assertDefaultParameters(DiseaseOccurrence occurrence, DiseaseOccurrenceStatus status) {
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(status);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSetsStatusToReadyForLargeCountryPointWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        setIsGoldStandardProvenance(occurrence, false);
        occurrence.getLocation().setPrecision(LocationPrecision.COUNTRY);
        occurrence.getLocation().setIsModelEligible(false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(occurrence)).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertParameterValues(occurrence, environmentalSuitability, distanceFromDiseaseExtent, DiseaseOccurrenceStatus.READY);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfESIsNullAndDistanceFromExtentIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertDefaultParameters(occurrence, DiseaseOccurrenceStatus.IN_REVIEW);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfOutsideDiseaseExtentAndESIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(1.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfAboveMaxESAndDistanceFromExtentIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.5);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.5);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSetsAllValidationParameters() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        GridCoverage2D suitabilityRaster = mock(GridCoverage2D.class);
        GridCoverage2D[] adminRasters = new GridCoverage2D[] {mock(GridCoverage2D.class), mock(GridCoverage2D.class), mock(GridCoverage2D.class)};

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, false);
        occurrence.getLocation().setPrecision(LocationPrecision.ADMIN1);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, false);
        when(esHelper.getLatestMeanPredictionRaster(occurrence.getDiseaseGroup())).thenReturn(suitabilityRaster);
        when(esHelper.getAdminRasters()).thenReturn(adminRasters);
        when(esHelper.findEnvironmentalSuitability(same(occurrence.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(occurrence)).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSetsGoldStandardParametersWhenAutomaticModelRunsAreDisabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, false, false);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, true);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence.getFinalWeighting()).isEqualTo(1.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSetsGoldStandardParametersWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true, false);
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, true);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence.getFinalWeighting()).isEqualTo(1.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfBelowMaxES() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.39);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(-300.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.39);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(-300.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtent() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.6);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(1.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.6);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfESIsNullAndDistanceFromExtentIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        occurrence.getLocation().setHasPassedQc(true);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtentAndESIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(1.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtentAndMaxESIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        occurrence.getDiseaseGroup().setMaxEnvironmentalSuitabilityWithoutML(null);
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.6);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(1.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.6);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksDoesNotSendToValidatorWithoutMLIfWithinTolerances() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.41);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(-1000.0);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.41);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(-1000.0);
        assertThat(occurrence.getMachineWeighting()).isEqualTo(1);
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfAboveMaxESAndDistanceFromExtentIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        setIsGoldStandardProvenance(occurrence, false);

        when(esHelper.findEnvironmentalSuitability(occurrence.getLocation(), null, null)).thenReturn(0.5);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence.getDiseaseGroup(), occurrence.getLocation())).thenReturn(null);

        // Act
        service.addValidationParameters(Arrays.asList(occurrence), true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.5);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
    }

    @Test
    public void addValidationParametersSetsAllValidationParametersOnUsingRasterRegardlessOfOccurrenceValidityExceptGoldStandard() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability1 = 0.42;
        double environmentalSuitability2 = 0.52;
        double environmentalSuitability3 = 0.62;
        double distanceFromDiseaseExtent1 = 500;
        double distanceFromDiseaseExtent2 = 800;
        double distanceFromDiseaseExtent3 = 900;
        GridCoverage2D suitabilityRaster = mock(GridCoverage2D.class);
        GridCoverage2D[] adminRasters = new GridCoverage2D[] {mock(GridCoverage2D.class)};


        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(diseaseGroupId, false, false);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(diseaseGroupId, true, false);
        DiseaseOccurrence occurrence3 = createDiseaseOccurrence(diseaseGroupId, true, true);
        DiseaseGroup diseaseGroup = occurrence1.getDiseaseGroup();
        occurrence2.setDiseaseGroup(diseaseGroup);
        occurrence1.getLocation().setHasPassedQc(false);
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2);

        when(esHelper.getLatestMeanPredictionRaster(diseaseGroup)).thenReturn(suitabilityRaster);
        when(esHelper.getAdminRasters()).thenReturn(adminRasters);
        when(esHelper.findEnvironmentalSuitability(same(occurrence1.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability1);
        when(esHelper.findEnvironmentalSuitability(same(occurrence2.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability2);
        when(esHelper.findEnvironmentalSuitability(same(occurrence3.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability3);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(occurrence1.getLocation()))).thenReturn(distanceFromDiseaseExtent1);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(occurrence2.getLocation()))).thenReturn(distanceFromDiseaseExtent2);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(occurrence3.getLocation()))).thenReturn(distanceFromDiseaseExtent3);
        when(mwPredictor.findMachineWeighting(same(occurrence1))).thenReturn(null);
        when(mwPredictor.findMachineWeighting(same(occurrence2))).thenReturn(null);
        when(mwPredictor.findMachineWeighting(same(occurrence3))).thenReturn(null);

        // Act
        service.addValidationParameters(occurrences, false);

        // Assert
        assertParameterValues(occurrence1, environmentalSuitability1, distanceFromDiseaseExtent1, DiseaseOccurrenceStatus.IN_REVIEW);
        assertParameterValues(occurrence2, environmentalSuitability2, distanceFromDiseaseExtent2, DiseaseOccurrenceStatus.IN_REVIEW);
        assertDefaultParameters(occurrence3, DiseaseOccurrenceStatus.READY);
    }

    private void assertParameterValues(DiseaseOccurrence occurrence, double environmentalSuitability, double distanceFromDiseaseExtent, DiseaseOccurrenceStatus status) {
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getStatus()).isEqualTo(status);
    }

    @Test
    public void addValidationParametersOnlyAddsModelEligibleOccurrencesToValidator() {
        // Arrange
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;
        GridCoverage2D suitabilityRaster = mock(GridCoverage2D.class);
        GridCoverage2D[] adminRasters = new GridCoverage2D[] {mock(GridCoverage2D.class)};

        DiseaseGroup diseaseGroup = createDiseaseGroup();
        DiseaseOccurrence admin1Occurrence = createAdmin1Occurrence(1, diseaseGroup);
        DiseaseOccurrence countryOccurrence = createCountryOccurrence(2, diseaseGroup, true);
        DiseaseOccurrence largeCountryOccurrence = createCountryOccurrence(3, diseaseGroup, false);
        List<DiseaseOccurrence> occurrences = Arrays.asList(admin1Occurrence, countryOccurrence, largeCountryOccurrence);

        when(esHelper.getLatestMeanPredictionRaster(diseaseGroup)).thenReturn(suitabilityRaster);
        when(esHelper.getAdminRasters()).thenReturn(adminRasters);
        when(esHelper.findEnvironmentalSuitability(same(admin1Occurrence.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability);
        when(esHelper.findEnvironmentalSuitability(same(countryOccurrence.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability);
        when(esHelper.findEnvironmentalSuitability(same(largeCountryOccurrence.getLocation()), same(suitabilityRaster), same(adminRasters))).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(admin1Occurrence.getLocation()))).thenReturn(distanceFromDiseaseExtent);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(countryOccurrence.getLocation()))).thenReturn(distanceFromDiseaseExtent);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(diseaseGroup), same(largeCountryOccurrence.getLocation()))).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(same(admin1Occurrence))).thenReturn(null);
        when(mwPredictor.findMachineWeighting(same(countryOccurrence))).thenReturn(null);
        when(mwPredictor.findMachineWeighting(same(largeCountryOccurrence))).thenReturn(null);

        // Act
        service.addValidationParameters(occurrences, false);

        // Assert
        assertParameterValues(admin1Occurrence, environmentalSuitability, distanceFromDiseaseExtent, DiseaseOccurrenceStatus.IN_REVIEW);
        assertParameterValues(countryOccurrence, environmentalSuitability, distanceFromDiseaseExtent, DiseaseOccurrenceStatus.IN_REVIEW);
        assertParameterValues(largeCountryOccurrence, environmentalSuitability, distanceFromDiseaseExtent, DiseaseOccurrenceStatus.READY);
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorForNonAutomaticDisease() {
        // Arrange
        int diseaseGroupId = 30;
        GridCoverage2D suitabilityRaster = mock(GridCoverage2D.class);
        GridCoverage2D[] adminRasters = new GridCoverage2D[] {mock(GridCoverage2D.class)};

        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(diseaseGroupId, false, false);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(diseaseGroupId, false, false);
        DiseaseOccurrence occurrence3 = createDiseaseOccurrence(diseaseGroupId, false, true); // GOLD
        DiseaseGroup diseaseGroup = occurrence1.getDiseaseGroup();
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2);

        when(esHelper.getLatestMeanPredictionRaster(diseaseGroup)).thenReturn(suitabilityRaster);
        when(esHelper.getAdminRasters()).thenReturn(adminRasters);
        when(esHelper.findEnvironmentalSuitability(any(Location.class), same(suitabilityRaster), same(adminRasters))).thenReturn(0.62);
        when(dfdeHelper.findDistanceFromDiseaseExtent(any(DiseaseGroup.class), any(Location.class))).thenReturn(900.0);
        when(mwPredictor.findMachineWeighting(any(DiseaseOccurrence.class))).thenReturn(1.0);

        // Act
        service.addValidationParameters(occurrences, false);

        // Assert
        assertParameterValues(occurrence1, 0.62, 900.0, DiseaseOccurrenceStatus.IN_REVIEW);
        assertParameterValues(occurrence2, 0.62, 900.0, DiseaseOccurrenceStatus.IN_REVIEW);
        assertDefaultParameters(occurrence3, DiseaseOccurrenceStatus.READY);
        verify(mwPredictor, never()).findMachineWeighting(any(DiseaseOccurrence.class));
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        diseaseGroup.setGlobal(false);
        diseaseGroup.setUseMachineLearning(true);
        return diseaseGroup;
    }

    private DiseaseOccurrence createAdmin1Occurrence(int id, DiseaseGroup diseaseGroup) {
        Location location = new Location();
        location.setHasPassedQc(true);
        location.setPrecision(LocationPrecision.ADMIN1);
        location.setIsModelEligible(true);
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence(id, diseaseGroup, location, new Alert(), null, null, null);
        setIsGoldStandardProvenance(diseaseOccurrence, false);
        return diseaseOccurrence;
    }

    private DiseaseOccurrence createCountryOccurrence(int id, DiseaseGroup diseaseGroup, boolean modelEligible) {
        Location location = new Location();
        location.setHasPassedQc(true);
        location.setPrecision(LocationPrecision.COUNTRY);
        location.setIsModelEligible(modelEligible);
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence(id, diseaseGroup, location, new Alert(), null, null, null);
        setIsGoldStandardProvenance(diseaseOccurrence, false);
        return diseaseOccurrence;
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, boolean isAutomaticModelRunsEnabled, boolean isGoldStandard) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        DateTime automaticModelRunsStartDate = isAutomaticModelRunsEnabled ? DateTime.now() : null;
        diseaseGroup.setAutomaticModelRunsStartDate(automaticModelRunsStartDate);
        diseaseGroup.setGlobal(false);
        diseaseGroup.setUseMachineLearning(true);
        Location location = new Location();
        location.setHasPassedQc(true);
        location.setIsModelEligible(true);

        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence(1, diseaseGroup, location, new Alert(), null, null, null);
        setIsGoldStandardProvenance(diseaseOccurrence, isGoldStandard);
        diseaseOccurrence.setStatus(DiseaseOccurrenceStatus.READY);
        return diseaseOccurrence;
    }

    private DiseaseOccurrence createDiseaseOccurrenceWithoutMachineLearning() {
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true, false);
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getDiseaseGroup().setUseMachineLearning(false);
        occurrence.getDiseaseGroup().setMaxEnvironmentalSuitabilityWithoutML(0.4);
        return occurrence;
    }
}
