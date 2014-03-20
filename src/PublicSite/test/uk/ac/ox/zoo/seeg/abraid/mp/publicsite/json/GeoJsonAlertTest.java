package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for GeoJsonAlert.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonAlertTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void constructorForGeoJsonAlertBindsParametersCorrectly() throws Exception {
        // Arrange
        String expectedTitle = "foo1";
        String expectedSummary = "foo2";
        String expectedFeedName = "foo3";
        String expectedUrl = "foo4";
        DateTime expectedPublicationDate = DateTime.now();

        Alert alert = defaultAlert();
        when(alert.getTitle()).thenReturn(expectedTitle);
        when(alert.getSummary()).thenReturn(expectedSummary);
        when(alert.getFeed().getName()).thenReturn(expectedFeedName);
        when(alert.getUrl()).thenReturn(expectedUrl);
        when(alert.getPublicationDate()).thenReturn(expectedPublicationDate);

        // Act
        GeoJsonAlert result = new GeoJsonAlert(alert);

        // Assert
        assertThat(result.getFeedName()).isEqualTo(expectedFeedName);
        assertThat(result.getUrl()).isEqualTo(expectedUrl);
        assertThat(result.getPublicationDate()).isEqualTo(expectedPublicationDate);
        assertThat(result.getSummary()).isEqualTo(expectedSummary);
        assertThat(result.getTitle()).isEqualTo(expectedTitle);
    }
}
