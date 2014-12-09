package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for GeoJsonAlert.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonAlertTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    private String summary = "foo2";
    private String feedName = "foo3";
    private String feedLanguage = "foo4";
    private String url = "foo5";
    private Alert alert;

    @Before
    public void setUp() {
        alert = newAlert(summary, feedName, feedLanguage, url);
    }

    private Alert newAlert(String summary, String feedName, String feedLanguage, String url) {
        Alert alert = new Alert();
        alert.setSummary(summary);
        alert.setFeed(new Feed(feedName, feedLanguage));
        alert.setUrl(url);
        return alert;
    }

    @Test
    public void constructorForGeoJsonAlertBindsParametersCorrectly() throws Exception {
        // Arrange
        String title = "foo1";
        alert.setTitle(title);

        // Act
        GeoJsonAlert result = new GeoJsonAlert(alert);

        // Assert
        assertThat(result.getTitle()).isEqualTo(title);
        assertProperties(result);

    }

    @Test
    public void constructorForGeoJsonAlertBindsParametersCorrectlyWhenAlertTitleNotDefined() throws Exception {
        // Arrange
        alert.setTitle(null);

        // Act
        GeoJsonAlert result = new GeoJsonAlert(alert);

        // Assert
        assertThat(result.getTitle()).isEqualTo(feedName);
        assertProperties(result);
    }

    private void assertProperties(GeoJsonAlert result) {
        assertThat(result.getSummary()).isEqualTo(summary);
        assertThat(result.getFeedName()).isEqualTo(feedName);
        assertThat(result.getFeedLanguage()).isEqualTo(feedLanguage);
        assertThat(result.getUrl()).isEqualTo(url);
    }
}
