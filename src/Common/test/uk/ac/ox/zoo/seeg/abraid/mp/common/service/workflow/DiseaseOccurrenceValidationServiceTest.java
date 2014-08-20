package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.geotools.coverage.grid.GridCoverage2D;
import org.joda.time.DateTime;
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
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, true);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isFalse();
    }
    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsIsNotEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, false);

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

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
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

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false);
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

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
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

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true);
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
        return new DiseaseOccurrence(1, diseaseGroup, new Location(), new Alert(), null, null, null);
    }
}
