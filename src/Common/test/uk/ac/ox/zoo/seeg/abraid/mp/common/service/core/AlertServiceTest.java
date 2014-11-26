package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AlertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.FeedDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ProvenanceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the AlertService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AlertServiceTest {
    private AlertService alertService;
    private AlertDao alertDao;
    private FeedDao feedDao;
    private ProvenanceDao provenanceDao;

    @Before
    public void setUp() {
        alertDao = mock(AlertDao.class);
        feedDao = mock(FeedDao.class);
        provenanceDao = mock(ProvenanceDao.class);
        alertService = new AlertServiceImpl(alertDao, feedDao, provenanceDao);
    }

    @Test
    public void getAlertByHealthMapAlertId() {
        // Arrange
        Alert alert = new Alert();
        int healthMapAlertId = 123;
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
    public void saveFeed() {
        // Arrange
        Feed feed = new Feed();

        // Act
        alertService.saveFeed(feed);

        // Assert
        verify(feedDao).save(eq(feed));
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
        // Arrange
        Provenance provenance = new Provenance();

        // Act
        alertService.saveProvenance(provenance);

        // Assert
        verify(provenanceDao).save(eq(provenance));
    }
}
