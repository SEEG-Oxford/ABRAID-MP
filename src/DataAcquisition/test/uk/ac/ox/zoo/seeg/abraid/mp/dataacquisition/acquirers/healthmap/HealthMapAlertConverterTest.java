package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapDiseaseKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapAlertConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverterTest {
    private static final double DEFAULT_FEED_WEIGHTING = 0.5;

    private HealthMapLookupData lookupData;
    private Map<HealthMapDiseaseKey, HealthMapDisease> diseaseMap;

    @Before
    public void setUp() {
        lookupData = mock(HealthMapLookupData.class);
        diseaseMap = new HashMap<>();
        when(lookupData.getDiseaseMap()).thenReturn(diseaseMap);
    }

    @Test
    public void feedExistsAndAlertDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = null;
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, "");

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
        assertThat(occurrence.getAlert().getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
        assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
        assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedExistsAndAlertDoesNotExistBecauseLinkDoesNotContainAlertId() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "vi";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/doesnotcontainalertid";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
        assertThat(occurrence.getAlert().getHealthMapAlertId()).isNull();
        assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
        assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
        assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedExistsAndAlertExists() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "zh";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
    }

    @Test
    public void feedDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "zh";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(null);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutGetHealthMapProvenance();
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        Alert newAlert = occurrence.getAlert();
        Feed newFeed = newAlert.getFeed();
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(newAlert).isNotNull();
        assertThat(newFeed).isNotNull();
        assertThat(newFeed.getName()).isEqualTo(feedName);
        assertThat(newFeed.getProvenance()).isNotNull();
        assertThat(newFeed.getProvenance().getName()).isEqualTo(ProvenanceNames.HEALTHMAP);
        assertThat(newFeed.getLanguage()).isEqualTo(feedLanguage);
        assertThat(newFeed.getWeighting()).isEqualTo(DEFAULT_FEED_WEIGHTING);
        assertThat(newFeed.getHealthMapFeedId()).isEqualTo(feedId);
        assertThat(newAlert.getSummary()).isEqualTo(description);
        assertThat(newAlert.getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        assertThat(newAlert.getPublicationDate()).isEqualTo(publicationDate);
        assertThat(newAlert.getTitle()).isEqualTo(summary);
        assertThat(newAlert.getUrl()).isEqualTo(originalUrl);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(alertService).saveFeed(same(newFeed));
        assertThat(lookupData.getFeedMap().get(feedId)).isSameAs(newFeed);
    }

    @Test
    public void invalidUrlIsNotSavedOnConvertedAlert() {
        testInvalidUrl("/invalid/url.com");
        testInvalidUrl("article.aspx?articleNO=21162&?????? 10 ?????? ????? ??\"??????\" ?29 ???? ?????? ??? ????? ??? ????? ?????-21162");
        testInvalidUrl("gx.php?id=XX_ALERT_ID_XX&t=Single+Patient+Report+from+the+GeoSentinel+Surveillance+Network");
        testInvalidUrl("onm.php?id=XX_ALERT_ID_XXhttps://hk.news.yahoo.com/???????????-????????-102800036.html");
        testInvalidUrl("www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
        testInvalidUrl("file://www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
        testInvalidUrl("scp://www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
        testInvalidUrl("ftp://www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
        testInvalidUrl("ssh://www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
        testInvalidUrl("smb://www.phac-aspc.gc.ca/phn-asp/2013/measles-0325-eng.php");
    }

    @Test
    public void onlyHttpAndHttpsUrlsAreAccepted() {
        testValidUrl("http://promedmail.org/direct.php?id=20140106.2154965");
        testValidUrl("https://promedmail.org/direct.php?id=20140106.2154965");
    }

    private void testInvalidUrl(String url) {
        Alert newAlert = testUrl(url);
        assertThat(newAlert.getUrl()).isNull();
    }

    private void testValidUrl(String url) {
        Alert newAlert = testUrl(url);
        assertThat(newAlert.getUrl()).isEqualTo(url);
    }

    private Alert testUrl(String url) {
        // Arrange
        int feedId = 1;

        HashMap<Integer, Feed> feedMap = new HashMap<>();
        feedMap.put(feedId, new Feed("Test feed", null, 0, "zh", 1));
        when(lookupData.getFeedMap()).thenReturn(feedMap);

        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(
                mock(AlertService.class), mock(DiseaseService.class), mock(EmailService.class), lookupData);

        HealthMapAlert healthMapAlert = mock(HealthMapAlert.class);
        when(healthMapAlert.getFeedId()).thenReturn(feedId);
        when(healthMapAlert.getOriginalUrl()).thenReturn(url);

        int diseaseId = 1;
        String healthMapDiseaseName = "HealthMap disease name";
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(healthMapAlert.getDiseaseIds()).thenReturn(Arrays.asList(diseaseId));
        when(healthMapAlert.getDiseases()).thenReturn(Arrays.asList(healthMapDiseaseName));
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, mock(Location.class));

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        Alert newAlert = occurrence.getAlert();
        assertThat(newAlert).isNotNull();
        return newAlert;
    }

    @Test
    public void healthMapDiseaseNameChanged() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "zh";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String healthMapDiseaseNewName = "Test disease new name";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseNewName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(feed);
        HealthMapDisease healthMapDisease =
                mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        assertThat(healthMapDisease.getName()).isEqualTo(healthMapDiseaseNewName);
        verify(diseaseService).saveHealthMapDisease(eq(healthMapDisease));
    }

    @Test
    public void healthMapDiseaseDoesNotExist() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = null;
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        int existingDiseaseId = 2;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String existingHealthMapDiseaseName = "Test existing disease";
        String diseaseGroupName = "NEW FROM HEALTHMAP: Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup existingDiseaseGroup = new DiseaseGroup();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);
        DiseaseGroup newDiseaseGroup = new DiseaseGroup(null, null, diseaseGroupName, DiseaseGroupType.CLUSTER);
        HealthMapDisease newHealthMapDisease =
                new HealthMapDisease(diseaseId, healthMapDiseaseName, null, newDiseaseGroup);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(existingDiseaseId, existingHealthMapDiseaseName, null,
                existingDiseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isEqualTo(newDiseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(diseaseService).saveHealthMapDisease(eq(newHealthMapDisease));
        HealthMapDiseaseKey key = new HealthMapDiseaseKey(diseaseId, null);
        assertThat(lookupData.getDiseaseMap().get(key)).isEqualTo(newHealthMapDisease);
    }

    @Test
    public void invalidAlert() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "fr";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String description = "Test description";
        int healthMapAlertId = 2154965;
        String healthMapDiseaseName = "Test disease";

        Location location = new Location();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, null, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void feedExistsAndAlertDoesNotExistAndMultipleDiseasesWithOneNotOfInterestAndOneNew() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = null;
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        List<String> diseaseIds = Arrays.asList("1", "2", "3", "4");
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        List<String> healthMapDiseaseNames = Arrays.asList("Of interest 1", "Not of interest", "Of interest 2", "New");
        String newDiseaseGroupName = "NEW FROM HEALTHMAP: New";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroupOfInterest1 = new DiseaseGroup();
        DiseaseGroup diseaseGroupOfInterest2 = new DiseaseGroup();
        DiseaseGroup diseaseGroupNew = new DiseaseGroup(null, null, newDiseaseGroupName, DiseaseGroupType.CLUSTER);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, null, null, summary,
                publicationDate, link, description, originalUrl, "");
        healthMapAlert.setDiseaseIds(diseaseIds);
        healthMapAlert.setDiseases(healthMapDiseaseNames);

        HealthMapDisease healthMapDiseaseNew = new HealthMapDisease();
        healthMapDiseaseNew.setHealthMapDiseaseId(4);
        healthMapDiseaseNew.setName("New");
        healthMapDiseaseNew.setDiseaseGroup(diseaseGroupNew);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(1, "Of interest 1", null, diseaseGroupOfInterest1);
        mockOutGetExistingHealthMapDisease(2, "Not of interest", null, null);
        mockOutGetExistingHealthMapDisease(3, "Of interest 2", null, diseaseGroupOfInterest2);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(3);
        assertThat(occurrences.get(0).getDiseaseGroup()).isSameAs(diseaseGroupOfInterest1);
        assertThat(occurrences.get(1).getDiseaseGroup()).isSameAs(diseaseGroupOfInterest2);
        assertThat(occurrences.get(2).getDiseaseGroup()).isEqualTo(diseaseGroupNew);
        verify(diseaseService).saveHealthMapDisease(eq(healthMapDiseaseNew));

        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getAlert()).isNotNull();
            assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
            assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
            assertThat(occurrence.getAlert().getHealthMapAlertId()).isEqualTo(healthMapAlertId);
            assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
            assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
            assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
            assertThat(occurrence.getLocation()).isSameAs(location);
            assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        }
    }

    @Test
    public void feedExistsAndAlertDoesNotExistAndMultipleSubdiseasesWithOneNotOfInterestAndOneNew() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = null;
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Malaria";
        String comment = " p f, p k   , Pv, P z ";
        String newDiseaseGroupName = "NEW FROM HEALTHMAP: Malaria [sub-name: pz]";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroupOfInterest1 = new DiseaseGroup();
        DiseaseGroup diseaseGroupOfInterest2 = new DiseaseGroup();
        DiseaseGroup diseaseGroupNew = new DiseaseGroup(null, null, newDiseaseGroupName, DiseaseGroupType.CLUSTER);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, "");
        healthMapAlert.setComment(comment);

        HealthMapDisease healthMapDiseaseNew = new HealthMapDisease();
        healthMapDiseaseNew.setHealthMapDiseaseId(diseaseId);
        healthMapDiseaseNew.setName(healthMapDiseaseName);
        healthMapDiseaseNew.setSubName("pz");
        healthMapDiseaseNew.setDiseaseGroup(diseaseGroupNew);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, "pf", diseaseGroupOfInterest1);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, "pk", null);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, "pv", diseaseGroupOfInterest2);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(3);
        assertThat(occurrences.get(0).getDiseaseGroup()).isSameAs(diseaseGroupOfInterest1);
        assertThat(occurrences.get(1).getDiseaseGroup()).isSameAs(diseaseGroupOfInterest2);
        assertThat(occurrences.get(2).getDiseaseGroup()).isEqualTo(diseaseGroupNew);
        verify(diseaseService).saveHealthMapDisease(eq(healthMapDiseaseNew));

        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getAlert()).isNotNull();
            assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
            assertThat(occurrence.getAlert().getSummary()).isEqualTo(description);
            assertThat(occurrence.getAlert().getHealthMapAlertId()).isEqualTo(healthMapAlertId);
            assertThat(occurrence.getAlert().getPublicationDate()).isEqualTo(publicationDate);
            assertThat(occurrence.getAlert().getTitle()).isEqualTo(summary);
            assertThat(occurrence.getAlert().getUrl()).isEqualTo(originalUrl);
            assertThat(occurrence.getLocation()).isSameAs(location);
            assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        }
    }

    @Test
    public void healthMapDiseaseIsNotOfInterest() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = "de";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, null);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void healthMapFeedNameChanged() {
        // Arrange
        String feedName = "Test feed";
        String feedNewName = "Test feed new name";
        String feedLanguage = "de";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedNewName, feedId, healthMapDiseaseName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getFeed().getName()).isSameAs(feedNewName);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(alertService).saveFeed(same(feed));
    }

    @Test
    public void healthMapFeedLanguageChanged() {
        // Arrange
        String feedName = "Test feed";
        String feedLanguage = null;
        String feedNewLanguage = "zh";
        String summary = "Test summary";
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int diseaseId = 1;
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String healthMapDiseaseName = "Test disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedNewLanguage);

        // Prepare mock objects
        AlertService alertService = mock(AlertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        mockOutGetAlertByID(alertService, healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null, diseaseGroup);
        mockOutDoesDiseaseOccurrenceExist(diseaseService, false);

        // Act
        HealthMapAlertConverter alertConverter = new HealthMapAlertConverter(alertService, diseaseService, emailService, lookupData);
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getFeed()).isSameAs(feed);
        assertThat(occurrence.getAlert().getFeed().getLanguage()).isSameAs(feedNewLanguage);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(alertService).saveFeed(same(feed));
    }

    private void mockOutGetAlertByID(AlertService alertService, int healthMapAlertId, Alert alert) {
        when(alertService.getAlertByHealthMapAlertId(healthMapAlertId)).thenReturn(alert);
    }

    private void mockOutGetHealthMapProvenance() {
        Provenance provenance = new Provenance();
        provenance.setName(ProvenanceNames.HEALTHMAP);
        provenance.setDefaultFeedWeighting(DEFAULT_FEED_WEIGHTING);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
    }

    private void mockOutGetFeed(Feed feed) {
        Map<Integer, Feed> feedMap = new HashMap<>();
        if (feed != null) {
            feedMap.put(feed.getHealthMapFeedId(), feed);
        }
        when(lookupData.getFeedMap()).thenReturn(feedMap);
    }

    private HealthMapDisease mockOutGetExistingHealthMapDisease(Integer diseaseId, String healthMapDiseaseName,
                                                                String subName, DiseaseGroup diseaseGroup) {
        HealthMapDisease healthMapDisease = new HealthMapDisease(diseaseId, healthMapDiseaseName, subName,
                diseaseGroup);
        diseaseMap.put(new HealthMapDiseaseKey(diseaseId, subName), healthMapDisease);
        return healthMapDisease;
    }

    private void mockOutDoesDiseaseOccurrenceExist(DiseaseService diseaseService, boolean result) {
        when(diseaseService.doesDiseaseOccurrenceExist(any(DiseaseOccurrence.class))).thenReturn(result);
    }
}
