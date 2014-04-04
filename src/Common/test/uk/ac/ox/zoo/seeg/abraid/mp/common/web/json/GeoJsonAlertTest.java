package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

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
        String expectedFeedLanguage = "foo4";
        String expectedUrl = "foo5";

        Alert alert = defaultAlert();
        when(alert.getTitle()).thenReturn(expectedTitle);
        when(alert.getSummary()).thenReturn(expectedSummary);
        when(alert.getFeed().getName()).thenReturn(expectedFeedName);
        when(alert.getFeed().getLanguage()).thenReturn(expectedFeedLanguage);
        when(alert.getUrl()).thenReturn(expectedUrl);

        // Act
        GeoJsonAlert result = new GeoJsonAlert(alert);

        // Assert
        assertThat(result.getFeedName()).isEqualTo(expectedFeedName);
        assertThat(result.getFeedLanguage()).isEqualTo(expectedFeedLanguage);
        assertThat(result.getUrl()).isEqualTo(expectedUrl);
        assertThat(result.getSummary()).isEqualTo(expectedSummary);
        assertThat(result.getTitle()).isEqualTo(expectedTitle);
    }
}
