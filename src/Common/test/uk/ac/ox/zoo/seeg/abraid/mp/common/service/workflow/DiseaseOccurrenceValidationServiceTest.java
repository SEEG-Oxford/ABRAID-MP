package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;

import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Before
    public void setUp() {
        esHelper = mock(EnvironmentalSuitabilityHelper.class);
        dfdeHelper = mock(DistanceFromDiseaseExtentHelper.class);
        mwPredictor = mock(MachineWeightingPredictor.class);
        service = new DiseaseOccurrenceValidationServiceImpl(esHelper, dfdeHelper, mwPredictor);
    }

    @Test
    public void addValidationParametersWithChecksDoesNotAddParametersIfOccurrenceLocationIsNull() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        service.addValidationParametersWithChecks(occurrence, false);
        assertThat(occurrence.isValidated()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksDoesNotAddParametersIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true);
        occurrence.getLocation().setHasPassedQc(false);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.isValidated()).isNull();
    }
    @Test
    public void addValidationParametersWithChecksDoesNotAddParametersIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsAreDisabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, false);
        occurrence.getLocation().setHasPassedQc(false);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.isValidated()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSetsValidationParametersWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksSetsOnlyIsValidatedWhenAutomaticModelRunsAreDisabled() {
        // Arrange
        int diseaseGroupId = 30;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertDefaultParameters(occurrence);
    }

    private void assertDefaultParameters(DiseaseOccurrence occurrence) {
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfESIsNullAndDistanceFromExtentIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfOutsideDiseaseExtentAndESIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(1.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorIfAboveMaxESAndDistanceFromExtentIsNull() {
        // Arrange
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.5);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.5);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSetsAllValidationParameters() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(distanceFromDiseaseExtent);
        when(mwPredictor.findMachineWeighting(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksSetsGoldStandardParametersWhenAutomaticModelRunsAreDisabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, false);
        occurrence.getLocation().setHasPassedQc(true);

        // Act
        service.addValidationParametersWithChecks(occurrence, true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
        assertThat(occurrence.getFinalWeighting()).isEqualTo(1.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
    }

    @Test
    public void addValidationParametersWithChecksSetsGoldStandardParametersWhenAutomaticModelRunsAreEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true);
        occurrence.getLocation().setHasPassedQc(true);

        // Act
        service.addValidationParametersWithChecks(occurrence, true);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
        assertThat(occurrence.getFinalWeighting()).isEqualTo(1.0);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfBelowMaxES() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.39);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(-300.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.39);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(-300.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtent() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.6);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(1.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.6);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfESIsNullAndDistanceFromExtentIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtentAndESIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(1.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfOutsideDiseaseExtentAndMaxESIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();
        occurrence.getDiseaseGroup().setMaxEnvironmentalSuitabilityWithoutML(null);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.6);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(1.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.6);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(1.0);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksDoesNotSendToValidatorWithoutMLIfWithinTolerances() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.41);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(-1000.0);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.41);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(-1000.0);
        assertThat(occurrence.getMachineWeighting()).isEqualTo(1);
        assertThat(occurrence.isValidated()).isTrue();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersWithChecksSendsToValidatorWithoutMLIfAboveMaxESAndDistanceFromExtentIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrenceWithoutMachineLearning();

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(0.5);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(null);

        // Act
        service.addValidationParametersWithChecks(occurrence, false);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(0.5);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isFalse();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void addValidationParametersSetsAllValidationParametersUsingRasterRegardlessOfOccurrenceValidity() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability1 = 0.42;
        double environmentalSuitability2 = 0.52;
        double distanceFromDiseaseExtent1 = 500;
        double distanceFromDiseaseExtent2 = 800;
        GridCoverage2D raster = mock(GridCoverage2D.class);

        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(diseaseGroupId, false);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(diseaseGroupId, true);
        DiseaseGroup diseaseGroup = occurrence1.getDiseaseGroup();
        occurrence2.setDiseaseGroup(diseaseGroup);
        occurrence1.getLocation().setHasPassedQc(false);
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2);

        when(esHelper.getLatestMeanPredictionRaster(diseaseGroup)).thenReturn(raster);
        when(esHelper.findEnvironmentalSuitability(same(occurrence1), same(raster))).thenReturn(environmentalSuitability1);
        when(esHelper.findEnvironmentalSuitability(same(occurrence2), same(raster))).thenReturn(environmentalSuitability2);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(occurrence1))).thenReturn(distanceFromDiseaseExtent1);
        when(dfdeHelper.findDistanceFromDiseaseExtent(same(occurrence2))).thenReturn(distanceFromDiseaseExtent2);
        when(mwPredictor.findMachineWeighting(same(occurrence1))).thenReturn(null);
        when(mwPredictor.findMachineWeighting(same(occurrence2))).thenReturn(null);

        // Act
        service.addValidationParameters(occurrences);

        // Assert
        assertThat(occurrence1.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability1);
        assertThat(occurrence1.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent1);
        assertThat(occurrence1.getFinalWeighting()).isNull();
        assertThat(occurrence1.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence1.getMachineWeighting()).isNull();
        assertThat(occurrence1.isValidated()).isFalse();

        assertThat(occurrence2.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability2);
        assertThat(occurrence2.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent2);
        assertThat(occurrence2.getFinalWeighting()).isNull();
        assertThat(occurrence2.getFinalWeightingExcludingSpatial()).isNull();
        // At present mwPredictor is only set up to return a null weighting, which means occurrence must go to validator
        assertThat(occurrence2.getMachineWeighting()).isNull();
        assertThat(occurrence2.isValidated()).isFalse();
    }

    @Test
    public void addValidationParametersThrowsExceptionIfOccurrencesHaveDifferentDiseaseGroups() {
        // Arrange
        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(1, true);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(1, true);
        DiseaseOccurrence occurrence3 = createDiseaseOccurrence(2, true);
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2, occurrence3);

        // Act
        catchException(service).addValidationParameters(occurrences);

        // Assert
        assertThat(caughtException()).isInstanceOf(RuntimeException.class);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, boolean isAutomaticModelRunsEnabled) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        DateTime automaticModelRunsStartDate = isAutomaticModelRunsEnabled ? DateTime.now() : null;
        diseaseGroup.setAutomaticModelRunsStartDate(automaticModelRunsStartDate);
        diseaseGroup.setGlobal(false);
        diseaseGroup.setUseMachineLearning(true);
        Location location = new Location();
        location.setHasPassedQc(true);
        return new DiseaseOccurrence(1, diseaseGroup, location, new Alert(), null, null, null);
    }

    private DiseaseOccurrence createDiseaseOccurrenceWithoutMachineLearning() {
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true);
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getDiseaseGroup().setUseMachineLearning(false);
        occurrence.getDiseaseGroup().setMaxEnvironmentalSuitabilityWithoutML(0.4);
        return occurrence;
    }
}
