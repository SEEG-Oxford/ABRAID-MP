package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.DistanceFromDiseaseExtentHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;

import java.util.Arrays;
import java.util.List;

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

    @Before
    public void setUp() {
        esHelper = mock(EnvironmentalSuitabilityHelper.class);
        dfdeHelper = mock(DistanceFromDiseaseExtentHelper.class);
        service = new DiseaseOccurrenceValidationServiceImpl(esHelper, dfdeHelper);
    }

    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceIsNull() {
        boolean result = service.addValidationParametersWithChecks(null);
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceLocationIsNull() {
        boolean result = service.addValidationParametersWithChecks(new DiseaseOccurrence());
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsIsEnabled() {
        // Arrange
        boolean automaticModelRuns = true;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, automaticModelRuns);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isFalse();
    }
    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsIsNotEnabled() {
        // Arrange
        boolean automaticModelRuns = false;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, automaticModelRuns);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksSetsValidationParametersAndReturnsTrueWhenAutomaticModelRunsIsEnabled() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;
        boolean automaticModelRuns = true;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, automaticModelRuns);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(distanceFromDiseaseExtent);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isTrue();
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
    }

    @Test
    public void addValidationParametersWithChecksSetsOnlyIsValidatedAndReturnsTrueWhenAutomaticModelRunsIsNotEnabled() {
        // Arrange
        int diseaseGroupId = 30;
        boolean automaticModelRuns = false;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, automaticModelRuns);
        occurrence.getLocation().setHasPassedQc(true);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isTrue();
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
    }

    @Test
    public void addValidationParametersWithChecksSetsAppropriateValidationParametersAndReturnsTrue() {
        // Arrange - If environmental suitability and distance from disease extent are both null,
        // only set the values of: machine weighting to null, and is validated to true
        int diseaseGroupId = 30;

        boolean automaticModelRuns = true;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, automaticModelRuns);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(null);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(null);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isTrue();
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
    }

    @Test
    public void addValidationParametersWithChecksSetsAllValidationParametersAndReturnsTrue() {
        // Arrange
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        boolean automaticModelRuns = true;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, automaticModelRuns);
        occurrence.getLocation().setHasPassedQc(true);

        when(esHelper.findEnvironmentalSuitability(occurrence, null)).thenReturn(environmentalSuitability);
        when(dfdeHelper.findDistanceFromDiseaseExtent(occurrence)).thenReturn(distanceFromDiseaseExtent);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isTrue();
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
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

        // Act
        service.addValidationParameters(occurrences);

        // Assert
        assertThat(occurrence1.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability1);
        assertThat(occurrence1.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent1);
        assertThat(occurrence1.getMachineWeighting()).isNull();
        assertThat(occurrence1.isValidated()).isTrue();

        assertThat(occurrence2.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability2);
        assertThat(occurrence2.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent2);
        assertThat(occurrence2.getMachineWeighting()).isNull();
        assertThat(occurrence2.isValidated()).isTrue();
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, boolean automaticModelRuns) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRuns(automaticModelRuns);
        diseaseGroup.setGlobal(false);
        return new DiseaseOccurrence(1, diseaseGroup, new Location(), new Alert(), null, null,
                null);
    }
}
