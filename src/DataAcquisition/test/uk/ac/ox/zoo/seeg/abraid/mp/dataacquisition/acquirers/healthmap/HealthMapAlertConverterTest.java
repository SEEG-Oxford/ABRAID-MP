package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ObjectUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.HealthMapService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapAlertConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverterTest {
    private static final double DEFAULT_FEED_WEIGHTING = 0.5;

    private HealthMapLookupData lookupData;
    private Map<Integer, HealthMapDisease> diseaseMap;
    private Map<String, HealthMapSubDisease> subDiseaseMap;
    private HealthMapAlertConverter alertConverter;
    private AlertService alertService;
    private HealthMapService healthMapService;
    private EmailService emailService;
    private List<String> placeCategoriesToIgnore;

    @Before
    public void setUp() {
        lookupData = mock(HealthMapLookupData.class);
        diseaseMap = new HashMap<>();
        subDiseaseMap = new HashMap<>();
        when(lookupData.getDiseaseMap()).thenReturn(diseaseMap);
        when(lookupData.getSubDiseaseMap()).thenReturn(subDiseaseMap);

        alertService = mock(AlertService.class);
        healthMapService = mock(HealthMapService.class);
        emailService = mock(EmailService.class);
        placeCategoriesToIgnore = Arrays.asList("imported case");

        alertConverter = new HealthMapAlertConverter(alertService, emailService, lookupData, healthMapService,
                placeCategoriesToIgnore);
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, "");

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(null);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);
        mockOutGetHealthMapProvenance();

        // Act
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
        String feed = "ProMed Mail";
        DateTime date = DateTime.now();

        HashMap<Integer, Feed> feedMap = new HashMap<>();
        feedMap.put(feedId, new Feed("Test feed", null, 0, "zh", 1));
        when(lookupData.getFeedMap()).thenReturn(feedMap);

        HealthMapAlert healthMapAlert = mock(HealthMapAlert.class);
        when(healthMapAlert.getFeedId()).thenReturn(feedId);
        when(healthMapAlert.getFeed()).thenReturn(feed);
        when(healthMapAlert.getDate()).thenReturn(date);
        when(healthMapAlert.getOriginalUrl()).thenReturn(url);

        int diseaseId = 1;
        String healthMapDiseaseName = "HealthMap disease name";
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(healthMapAlert.getDiseaseIds()).thenReturn(Arrays.asList(diseaseId));
        when(healthMapAlert.getDiseases()).thenReturn(Arrays.asList(healthMapDiseaseName));
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseNewName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);
        HealthMapDisease healthMapDisease =
                mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isSameAs(diseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        assertThat(healthMapDisease.getName()).isEqualTo(healthMapDiseaseNewName);
        verify(healthMapService).saveHealthMapDisease(eq(healthMapDisease));
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
        DiseaseGroup existingDiseaseGroup = new DiseaseGroup(1);
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);
        DiseaseGroup newDiseaseGroup = new DiseaseGroup(null, null, diseaseGroupName, DiseaseGroupType.CLUSTER);
        HealthMapDisease newHealthMapDisease = new HealthMapDisease(diseaseId, healthMapDiseaseName, newDiseaseGroup);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(existingDiseaseId, existingHealthMapDiseaseName, existingDiseaseGroup);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isEqualTo(newDiseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(healthMapService).saveHealthMapDisease(eq(newHealthMapDisease));
        assertThat(lookupData.getDiseaseMap().get(diseaseId)).isEqualTo(newHealthMapDisease);
    }

    @Test
    public void healthMapSubDiseaseDoesNotExist() {
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
        String existingHealthMapDiseaseName = "Test existing disease";
        String description = "Test description";
        int healthMapAlertId = 2154965;

        Location location = new Location();
        DiseaseGroup existingDiseaseGroup = new DiseaseGroup(1);
        Alert alert = new Alert();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);
        String subDiseaseName = "new";

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId, summary,
                publicationDate, link, description, originalUrl, feedLanguage);
        healthMapAlert.setComment(subDiseaseName);
        HealthMapSubDisease newHealthMapSubDisease = new HealthMapSubDisease(null, subDiseaseName, null);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, existingHealthMapDiseaseName, existingDiseaseGroup);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(1);
        DiseaseOccurrence occurrence = occurrences.get(0);
        assertThat(occurrence.getDiseaseGroup()).isEqualTo(existingDiseaseGroup);
        assertThat(occurrence.getAlert()).isSameAs(alert);
        assertThat(occurrence.getLocation()).isSameAs(location);
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(publicationDate);
        verify(healthMapService).saveHealthMapSubDisease(eq(newHealthMapSubDisease));
        assertThat(lookupData.getSubDiseaseMap().get(subDiseaseName)).isEqualTo(newHealthMapSubDisease);
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
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void feedExistsAndAlertDoesNotExistAndMultipleDiseasesAndMultipleSubdiseases() {
        // Arrange
        String feedName = "Test feed";
        String summary = "Test summary";
        String feedLanguage = null;
        String originalUrl = "http://promedmail.org/direct.php?id=20140106.2154965";
        int feedId = 1;
        DateTime publicationDate = DateTime.now();
        String link = "http://healthmap.org/ln.php?2154965";
        String description = "Test description";
        int healthMapAlertId = 2154965;
        Location location = new Location();
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        // Here we have:
        // Malaria (of interest) linked to pf and pv (of interest) => existing disease groups for pf and pv
        // Schistosomiasis (of interest) linked to sh (of interest) => existing disease group for sh
        // Malaria (of interest) linked to pk (not of interest) => no disease group
        // Malaria (of interest) linked to pz (non-existent) => no disease group
        // Leishmaniasis (of interest) not linked to a sub-disease => existing disease group for Leishmaniases
        // Not Of Interest 1 (not of interest) linked to ab (of interest) => existing disease group for ab
        // Not Of Interest 2 (not of interest) not linked to a sub-disease => no disease group
        // New (new) not linked to a sub-disease => new disease group
        List<String> diseaseIds = Arrays.asList("1", "2", "3", "4", "5", "6");
        List<String> healthMapDiseaseNames = Arrays.asList("Malaria", "Schistosomiasis", "Leishmaniasis",
                "Not Of Interest 1", "Not Of Interest 2", "New");
        String newDiseaseGroupName = "NEW FROM HEALTHMAP: New";
        String comment = " p f, p k   , s h, Pv, ab, P z ";

        DiseaseGroup diseaseGroupMalaria = new DiseaseGroup(1);
        DiseaseGroup diseaseGroupSchistosomiasis = new DiseaseGroup(2);
        DiseaseGroup diseaseGroupLeishmaniases = new DiseaseGroup(3);
        DiseaseGroup diseaseGroupPf = new DiseaseGroup(4);
        DiseaseGroup diseaseGroupPv = new DiseaseGroup(5);
        DiseaseGroup diseaseGroupSh = new DiseaseGroup(6);
        DiseaseGroup diseaseGroupAb = new DiseaseGroup(7);
        DiseaseGroup diseaseGroupNew = new DiseaseGroup(null, null, newDiseaseGroupName, DiseaseGroupType.CLUSTER);

        HealthMapDisease hmDiseaseMalaria = mockOutGetExistingHealthMapDisease(1, "Malaria", diseaseGroupMalaria);
        HealthMapDisease hmDiseaseSchistosomiasis = mockOutGetExistingHealthMapDisease(2, "Schistosomiasis", diseaseGroupSchistosomiasis);
        mockOutGetExistingHealthMapDisease(3, "Leishmaniasis", diseaseGroupLeishmaniases);
        mockOutGetExistingHealthMapDisease(4, "Not Of Interest 1", null);
        HealthMapDisease hmDiseaseNotOfInterest2 = mockOutGetExistingHealthMapDisease(5, "Not Of Interest 2", null);
        mockOutGetExistingHealthMapSubDisease(hmDiseaseMalaria, "pf", diseaseGroupPf);
        mockOutGetExistingHealthMapSubDisease(hmDiseaseMalaria, "pk", null);
        mockOutGetExistingHealthMapSubDisease(hmDiseaseMalaria, "pv", diseaseGroupPv);
        mockOutGetExistingHealthMapSubDisease(hmDiseaseSchistosomiasis, "sh", diseaseGroupSh);
        mockOutGetExistingHealthMapSubDisease(hmDiseaseNotOfInterest2, "ab", diseaseGroupAb);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, null, null, summary,
                publicationDate, link, description, originalUrl, "");
        healthMapAlert.setDiseaseIds(diseaseIds);
        healthMapAlert.setDiseases(healthMapDiseaseNames);
        healthMapAlert.setComment(comment);

        HealthMapDisease healthMapDiseaseNew = new HealthMapDisease();
        healthMapDiseaseNew.setId(6);
        healthMapDiseaseNew.setName("New");
        healthMapDiseaseNew.setDiseaseGroup(diseaseGroupNew);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(feed);

        // Act
        List<DiseaseOccurrence> occurrences = alertConverter.convert(healthMapAlert, location);

        // Assert
        assertThat(occurrences).hasSize(6);
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupPf)).isTrue();
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupSh)).isTrue();
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupPv)).isTrue();
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupAb)).isTrue();
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupLeishmaniases)).isTrue();
        assertThat(doOccurrencesContainDiseaseGroup(occurrences, diseaseGroupNew)).isTrue();
        verify(healthMapService).saveHealthMapDisease(eq(healthMapDiseaseNew));

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
        mockOutGetAlertByID(healthMapAlertId, alert);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, null);

        // Act
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedNewName, feedId, healthMapDiseaseName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
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
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Feed feed = new Feed(feedName, null, 0, feedLanguage, feedId);

        HealthMapAlert healthMapAlert = new HealthMapAlert(feedName, feedId, healthMapDiseaseName, diseaseId,
                summary, publicationDate, link, description, originalUrl, feedNewLanguage);

        // Prepare mock objects
        mockOutGetAlertByID(healthMapAlertId, null);
        mockOutGetFeed(feed);
        mockOutGetExistingHealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);

        // Act
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

    private void mockOutGetAlertByID(int healthMapAlertId, Alert alert) {
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
                                                                DiseaseGroup diseaseGroup) {
        HealthMapDisease healthMapDisease = new HealthMapDisease(diseaseId, healthMapDiseaseName, diseaseGroup);
        diseaseMap.put(diseaseId, healthMapDisease);
        return healthMapDisease;
    }

    private HealthMapSubDisease mockOutGetExistingHealthMapSubDisease(HealthMapDisease healthMapDisease, String name,
                                                                      DiseaseGroup diseaseGroup) {
        HealthMapSubDisease healthMapSubDisease = new HealthMapSubDisease(healthMapDisease, name, diseaseGroup);
        subDiseaseMap.put(name, healthMapSubDisease);
        return healthMapSubDisease;
    }

    private boolean doOccurrencesContainDiseaseGroup(List<DiseaseOccurrence> occurrences, DiseaseGroup diseaseGroup) {
        for (DiseaseOccurrence occurrence : occurrences) {
            if (ObjectUtils.nullSafeEquals(occurrence.getDiseaseGroup(), diseaseGroup)) {
                return true;
            }
        }
        return false;
    }
}
