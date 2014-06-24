package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static org.fest.assertions.api.Assertions.assertThat;
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
    public void addValidationParametersReturnsFalseIfOccurrenceIsNull() {
        boolean result = service.addValidationParameters(null);
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersReturnsFalseIfOccurrenceLocationIsNull() {
        boolean result = service.addValidationParameters(new DiseaseOccurrence());
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersReturnsFalseIfOccurrenceLocationHasNotPassedQC() {
        // Arrange
        DiseaseOccurrence occurrence = getDefaultDiseaseOccurrence(1);

        // Act
        boolean result = service.addValidationParameters(occurrence);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void addValidationParametersSetsValidationParametersAndReturnsTrue() {
        // Arrange
        Point point = GeometryUtils.createPoint(10, 20);
        int diseaseGroupId = 30;
        double environmentalSuitability = 0.42;
        double distanceFromDiseaseExtent = 500;
        double machineWeighting = 0.7;

        DiseaseOccurrence occurrence = getDefaultDiseaseOccurrence(diseaseGroupId);
        occurrence.getLocation().setHasPassedQc(true);
        occurrence.getLocation().setGeom(point);

        when(nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point)).thenReturn(environmentalSuitability);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point)).thenReturn(distanceFromDiseaseExtent);

        // Act
        boolean result = service.addValidationParameters(occurrence);

        // Assert
        assertThat(result).isTrue();
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getMachineWeighting()).isEqualTo(machineWeighting);
        assertThat(occurrence.isValidated()).isTrue();
    }

    private DiseaseOccurrence getDefaultDiseaseOccurrence(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(false);
        return new DiseaseOccurrence(1, diseaseGroup, new Location(), new Alert(), null, null,
                null);
    }
}