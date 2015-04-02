package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the CsvLookupData class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvLookupDataTest {
    private AlertService alertService;
    private GeometryService geometryService;
    private DiseaseService diseaseService;
    private CsvLookupData lookupData;

    @Before
    public void setUp() {
        alertService = mock(AlertService.class);
        geometryService = mock(GeometryService.class);
        diseaseService = mock(DiseaseService.class);
        lookupData = new CsvLookupData(alertService, geometryService, diseaseService);
    }

    @Test
    public void getCountryMap() {
        // Arrange
        Country country1 = new Country(1, "Test country 1");
        Country country2 = new Country(2, "Test country 2");

        List<Country> countries = Arrays.asList(country1, country2);
        when(geometryService.getAllCountries()).thenReturn(countries);

        Map<String, Country> expectedCountryMap = new HashMap<>();
        expectedCountryMap.put("test country 1", country1);
        expectedCountryMap.put("test country 2", country2);

        // Act
        Map<String, Country> actualCountryMap = lookupData.getCountryMap();

        // Assert
        assertThat(actualCountryMap).isEqualTo(expectedCountryMap);
    }

    @Test
    public void getDiseaseMap() {
        // Arrange
        DiseaseGroup diseaseGroup1 = new DiseaseGroup("Test disease 1");
        DiseaseGroup diseaseGroup2 = new DiseaseGroup("Test disease 2");

        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2);
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);

        Map<String, DiseaseGroup> expectedDiseaseGroupMap = new HashMap<>();
        expectedDiseaseGroupMap.put("test disease 1", diseaseGroup1);
        expectedDiseaseGroupMap.put("test disease 2", diseaseGroup2);

        // Act
        Map<String, DiseaseGroup> actualDiseaseGroupMap = lookupData.getDiseaseGroupMap();

        // Assert
        assertThat(actualDiseaseGroupMap).isEqualTo(expectedDiseaseGroupMap);
    }

    @Test
    public void getFeedForManuallyUploadedDataReturnsExistingFeed() {
        // NB. Case insensitive when checking for existing feed by name, and same feed name is allowed for different provenances
        // Arrange
        String feedName = "SEEG Data";
        Feed expectedManualFeed = new Feed(feedName);
        Feed expectedGoldStandardFeed = new Feed(feedName);

        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL)).thenReturn(Arrays.asList(expectedManualFeed));
        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL_GOLD_STANDARD)).thenReturn(Arrays.asList(expectedGoldStandardFeed));

        // Act
        Feed manualFeed = lookupData.getFeedForManuallyUploadedData("seeg data", false);
        Feed goldStandardFeed = lookupData.getFeedForManuallyUploadedData("seeg DATA", true);

        // Assert
        assertThat(manualFeed).isSameAs(expectedManualFeed);
        assertThat(goldStandardFeed).isSameAs(expectedGoldStandardFeed);
        verify(alertService, never()).saveFeed(any(Feed.class));
    }

    @Test
    public void getFeedForManuallyUploadedDataAddsNewFeed() {
        // Arrange
        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL)).thenReturn(new ArrayList<Feed>());
        when(alertService.getProvenanceByName(ProvenanceNames.MANUAL)).thenReturn(new Provenance(ProvenanceNames.MANUAL));
        String newFeedName = "SEEG Data 2014";

        // Act
        Feed newFeed1 = lookupData.getFeedForManuallyUploadedData(newFeedName, false);
        Feed newFeed2 = lookupData.getFeedForManuallyUploadedData(newFeedName.toLowerCase(), false);

        // Assert - Only one feed saved due to case insensitive checking on feed name.
        assertFeed(newFeed1, newFeedName);
        assertFeed(newFeed2, newFeedName);
        verify(alertService, times(1)).saveFeed(any(Feed.class));
    }

    private void assertFeed(Feed newFeed, String newFeedName) {
        assertThat(newFeed.getName()).isEqualTo(newFeedName);
        assertThat(newFeed.getProvenance().getName()).isEqualTo(ProvenanceNames.MANUAL);
        assertThat(newFeed.getWeighting()).isEqualTo(newFeed.getProvenance().getDefaultFeedWeighting());
    }
}
