package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

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
        String expectedCountryName = "foo2";
        DateTime expectedStartDate = DateTime.now();

        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getName()).thenReturn(expectedLocationName);
        when(occurrence.getLocation().getCountry().getName()).thenReturn(expectedCountryName);
        when(occurrence.getOccurrenceStartDate()).thenReturn(expectedStartDate.toDate());

        // Act
        GeoJsonDiseaseOccurrenceFeatureProperties result = new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);

        // Assert
        assertThat(result.getDiseaseOccurrenceStartDate()).isEqualTo(expectedStartDate);
        assertThat(result.getLocationName()).isEqualTo(expectedLocationName);
        assertThat(result.getCountryName()).isEqualTo(expectedCountryName);
        assertThat(result.getAlert()).isNotNull();
    }
}
