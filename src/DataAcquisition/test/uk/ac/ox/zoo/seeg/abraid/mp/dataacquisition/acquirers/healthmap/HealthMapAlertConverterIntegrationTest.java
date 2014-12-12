package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the HealthMapAlertConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverterIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private HealthMapAlertConverter alertConverter;

    @Test
    public void invalidAlertBecausePlaceCategory1ToBeIgnored() {
        testInvalidAlertBecausePlaceCategoryToBeIgnored("imported case");
    }

    @Test
    public void invalidAlertBecausePlaceCategory2ToBeIgnored() {
        testInvalidAlertBecausePlaceCategoryToBeIgnored("vaccine-associated paralytic Poliomyelitis");
    }

    private void testInvalidAlertBecausePlaceCategoryToBeIgnored(String placeCategory) {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "fr";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String description = "Test description";
        String healthMapDiseaseName = "Test disease";

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);
        healthMapAlert.setPlaceCategories(Arrays.asList(placeCategory));

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, new Location());

        // Assert
        assertThat(occurrences).isEmpty();
    }
}
