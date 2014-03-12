package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapAlertConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverterTest {
    private static final double DEFAULT_FEED_WEIGHTING = 0.5;

    @Test
    public void feedExistsAndAlertDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
        assertThat(occurrence.getAlert().getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
        assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
        assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedExistsAndAlertDoesNotExistBecauseLinkDoesNotContainAlertId() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/doesnotcontainalertid";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
        assertThat(occurrence.getAlert().getHealthMapAlertId()).isNull();
        assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
        assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
        assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedExistsAndAlertExists() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(lookupData, null);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutGetHealthMapProvenance(lookupData);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isNotNull();
        assertThat(occurrence.getAlert().getFeed().getName()).isEqualTo(feedName);
        assertThat(occurrence.getAlert().getFeed().getProvenance()).isNotNull();
        assertThat(occurrence.getAlert().getFeed().getProvenance().getName()).isEqualTo(ProvenanceNames.HEALTHMAP);
        assertThat(occurrence.getAlert().getFeed().getWeighting()).isEqualTo(DEFAULT_FEED_WEIGHTING);
        assertThat(occurrence.getAlert().getFeed().getHealthMapFeedId()).isEqualTo(feedId);
        assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
        assertThat(occurrence.getAlert().getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
        assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
        assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
    }

    @Test
    public void healthMapDiseaseNameChanged() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String healthMapDiseaseNewName = "Test disease new name";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseNewName, diseaseId,
                summary, publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        HealthMapDisease healthMapDisease =
                mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
        assertThat(healthMapDisease.getName()).isEqualTo(healthMapDiseaseNewName);
        verify(diseaseService, times(1)).saveHealthMapDisease(eq(healthMapDisease));
    }

    @Test
    public void healthMapDiseaseDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Long existingDiseaseId = 2L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String existingHealthMapDiseaseName = "Test existing disease";
        String diseaseGroupName = "NEW FROM HEALTHMAP: Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup existingDiseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);
        DiseaseGroup newDiseaseGroup = new DiseaseGroup(null, null, diseaseGroupName, DiseaseGroupType.CLUSTER);
        HealthMapDisease newHealthMapDisease = new HealthMapDisease(diseaseId, healthMapDiseaseName, newDiseaseGroup);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, existingDiseaseId, existingHealthMapDiseaseName,
                existingDiseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isEqualTo(newDiseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
        verify(diseaseService, times(1)).saveHealthMapDisease(eq(newHealthMapDisease));
    }

    @Test
    public void healthMapDiseaseIsMissing() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, null, null, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNull();
    }

    @Test
    public void healthMapDiseaseIsNotOfInterest() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, null);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNull();
    }

    @Test
    public void healthMapFeedNameChanged() {
        // Arrange
        String feedName = "Test feed";
        String feedNewName = "Test feed new name";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedNewName, feedId, healthMapDiseaseName, diseaseId,
                summary, publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNotNull();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getFeed().getName()).isSameAs(feedNewName);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(publicationDate);
    }

    @Test
    public void occurrenceAlreadyExists() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        Long diseaseId = 1L;
        Long feedId = 1L;
        Date publicationDate = Calendar.getInstance().getTime();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        long healthMapAlertId = 2154965L;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        HealthMapLookupData lookupData = mock(HealthMapLookupData.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(lookupData, feed);
        mockOutGetExistingHealthMapDisease(lookupData, diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, true);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, lookupData);
        DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrence).isNull();
    }

    private void mockOutGetAlertByID(AlertService alertService, long healthMapAlertId, Alert alert) {
        when(alertService.getAlertByHealthMapAlertId(healthMapAlertId)).thenReturn(alert);
    }

    private void mockOutGetHealthMapProvenance(HealthMapLookupData lookupData) {
        Provenance provenance = new Provenance();
        provenance.setName(ProvenanceNames.HEALTHMAP);
        provenance.setDefaultFeedWeighting(DEFAULT_FEED_WEIGHTING);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
    }

    private void mockOutGetFeed(HealthMapLookupData lookupData, Feed feed) {
        Map<Long, Feed> feedMap = new HashMap<>();
        if (feed != null) {
            feedMap.put(feed.getHealthMapFeedId(), feed);
        }
        when(lookupData.getFeedMap()).thenReturn(feedMap);
    }

    private HealthMapDisease mockOutGetExistingHealthMapDisease(HealthMapLookupData lookupData, Long diseaseId,
                                                    String healthMapDiseaseName, DiseaseGroup diseaseGroup) {
        HealthMapDisease healthMapDisease = new HealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);
        Map<Long, HealthMapDisease> diseaseMap = new HashMap<>();
        diseaseMap.put(diseaseId, healthMapDisease);
        when(lookupData.getDiseaseMap()).thenReturn(diseaseMap);
        return healthMapDisease;
    }

    private void mockOutDoesDiseaseOccurrenceExist(DiseaseService diseaseService, boolean result) {
        when(diseaseService.doesDiseaseOccurrenceExist(any(DiseaseOccurrence.class))).thenReturn(result);
    }
}
