package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for GeoJsonDiseaseOccurrenceFeatureProperties.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseOccurrenceFeaturePropertiesTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseOccurrenceFeaturePropertiesBindsParametersCorrectly() throws Exception {
        // Arrange
        String expectedLocationName = "foo1";
        DateTime expectedOccurrenceDate = DateTime.now();

        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getName()).thenReturn(expectedLocationName);
        when(occurrence.getOccurrenceDate()).thenReturn(expectedOccurrenceDate);

        // Act
        GeoJsonDiseaseOccurrenceFeatureProperties result = new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);

        // Assert
        assertThat(result.getOccurrenceDate()).isEqualTo(expectedOccurrenceDate);
        assertThat(result.getLocationName()).isEqualTo(expectedLocationName);
        assertThat(result.getAlert()).isNotNull();
    }

    @Test
    public void extractAdminUnitQCGaulCodeReturnsExpectedGaulCode() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        Integer gaulCode = 1;

        when(occurrence.getDiseaseGroup().isGlobal()).thenReturn(true);
        when(occurrence.getLocation().getAdminUnitQCGaulCode()).thenReturn(gaulCode);

        // Act
        GeoJsonDiseaseOccurrenceFeatureProperties result = new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);

        // Assert
        assertThat(result.getGaulCode()).isEqualTo(gaulCode);
    }
}
