package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the AlertService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AlertServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private AlertService alertService;

    @Test
    public void getAlertByHealthMapAlertId() {
        // Arrange
        Alert alert = new Alert();
        long healthMapAlertId = 123L;
        when(alertDao.getByHealthMapAlertId(healthMapAlertId)).thenReturn(alert);

        // Act
        Alert testAlert = alertService.getAlertByHealthMapAlertId(healthMapAlertId);

        // Assert
        assertThat(testAlert).isSameAs(alert);
    }

    @Test
    public void getFeedsByProvenanceName() {
        // Arrange
        List<Feed> feeds = Arrays.asList(new Feed());
        String provenanceName = ProvenanceNames.HEALTHMAP;
        when(feedDao.getByProvenanceName(provenanceName)).thenReturn(feeds);

        // Act
        List<Feed> testFeeds = alertService.getFeedsByProvenanceName(provenanceName);

        // Assert
        assertThat(testFeeds).isSameAs(feeds);
    }

    @Test
    public void getProvenanceByName() {
        // Arrange
        Provenance provenance = new Provenance();
        String name = "Provenance name";
        when(provenanceDao.getByName(name)).thenReturn(provenance);

        // Act
        Provenance testProvenance = alertService.getProvenanceByName(name);

        // Assert
        assertThat(testProvenance).isSameAs(provenance);
    }

    @Test
    public void saveProvenance() {
        // TODO
    }
}
