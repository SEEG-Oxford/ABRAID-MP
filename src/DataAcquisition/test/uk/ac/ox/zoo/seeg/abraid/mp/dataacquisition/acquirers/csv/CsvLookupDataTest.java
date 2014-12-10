package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

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
    private LocationService locationService;
    private DiseaseService diseaseService;
    private CsvLookupData lookupData;

    @Before
    public void setUp() {
        alertService = mock(AlertService.class);
        locationService = mock(LocationService.class);
        diseaseService = mock(DiseaseService.class);
        lookupData = new CsvLookupData(alertService, locationService, diseaseService);
    }

    @Test
    public void getCountryMap() {
        // Arrange
        Country country1 = new Country(1, "Test country 1");
        Country country2 = new Country(2, "Test country 2");

        List<Country> countries = Arrays.asList(country1, country2);
        when(locationService.getAllCountries()).thenReturn(countries);

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
    public void getFeedForManuallyUploadedData() {
        // Arrange
        String feedName = "SEEG Data";
        Feed manualFeed = new Feed(feedName);
        Feed goldStandardFeed = new Feed(feedName);

        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL)).thenReturn(Arrays.asList(manualFeed));
        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL_GOLD_STANDARD)).thenReturn(Arrays.asList(goldStandardFeed));

        // Act
        Feed actualManualFeed = lookupData.getFeedForManuallyUploadedData(feedName, false);
        Feed actualGoldStandardFeed = lookupData.getFeedForManuallyUploadedData(feedName, true);

        // Assert
        assertThat(actualManualFeed).isSameAs(manualFeed);
        assertThat(actualGoldStandardFeed).isSameAs(goldStandardFeed);
    }

    @Test
    public void getFeedForManuallyUploadedDataAddsNewFeed() {
        // Arrange
        when(alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL)).thenReturn(new ArrayList<Feed>());
        when(alertService.getProvenanceByName(ProvenanceNames.MANUAL)).thenReturn(new Provenance(ProvenanceNames.MANUAL));
        String newFeedName = "SEEG Data 2014";

        // Act
        Feed newFeed = lookupData.getFeedForManuallyUploadedData(newFeedName, false);

        // Assert
        assertThat(newFeed.getName()).isEqualTo(newFeedName);
        assertThat(newFeed.getProvenance().getName()).isEqualTo(ProvenanceNames.MANUAL);
        assertThat(newFeed.getWeighting()).isEqualTo(newFeed.getProvenance().getDefaultFeedWeighting());
        verify(alertService).saveFeed(any(Feed.class));
    }
}
