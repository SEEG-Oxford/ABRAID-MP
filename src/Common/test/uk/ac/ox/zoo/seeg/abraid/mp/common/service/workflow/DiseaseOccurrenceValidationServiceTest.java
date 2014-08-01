package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the DiseaseOccurrenceValidationService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceValidationServiceTest extends AbstractCommonSpringUnitTests {
    @Autowired
    private DiseaseOccurrenceValidationService service;

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
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, DateTime.now());

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isFalse();
    }
    @Test
    public void addValidationParametersWithChecksReturnsFalseIfOccurrenceLocationHasNotPassedQCWhenAutomaticModelRunsIsNotEnabled() {
        // Arrange
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, null);

        // Act
        boolean result = service.addValidationParametersWithChecks(occurrence);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersWithChecksSetsValidationParametersAndReturnsTrueWhenAutomaticModelRunsIsEnabled() {
        // Arrange
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, DateTime.now());
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getLocation().setGeom(point);

        when(nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point)).thenReturn(environmentalSuitability);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point)).thenReturn(distanceFromDiseaseExtent);

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
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, null);
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getLocation().setGeom(point);

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
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, DateTime.now());
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getLocation().setGeom(point);

        when(nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point)).thenReturn(null);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point)).thenReturn(null);

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
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, DateTime.now());
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getLocation().setGeom(point);

        when(nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point)).thenReturn(environmentalSuitability);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point)).thenReturn(distanceFromDiseaseExtent);

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
    public void addValidationParametersSetsAllValidationParametersRegardlessOfOccurrenceValidity() {
        // Arrange
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, null);
        occurrence.getLocation().setHasPassedQc(false);
        occurrence.getLocation().setGeom(point);

        when(nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point)).thenReturn(environmentalSuitability);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point)).thenReturn(distanceFromDiseaseExtent);

        // Act
        service.addValidationParameters(occurrence);

        // Assert
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.isValidated()).isTrue();
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, DateTime automaticModelRunsStartDate) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(automaticModelRunsStartDate);
        diseaseGroup.setGlobal(false);
        return new DiseaseOccurrence(1, diseaseGroup, new Location(), new Alert(), null, null,
                null);
    }
}
