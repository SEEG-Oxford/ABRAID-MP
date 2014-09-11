package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.*;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void getFeedForUploadedData() {
        // Arrange
        Feed expectedFeed = new Feed();
        when(alertService.getFeedsByProvenanceName("Uploaded")).thenReturn(Arrays.asList(expectedFeed));

        // Act
        Feed actualFeed = lookupData.getFeedForUploadedData();

        // Assert
        assertThat(actualFeed).isSameAs(expectedFeed);
    }

    @Test
    public void getFeedForUploadedDataDoesNotReturnOneFeed() {
        // Arrange
        when(alertService.getFeedsByProvenanceName("Uploaded")).thenReturn(new ArrayList<Feed>());

        // Act
        catchException(lookupData).getFeedForUploadedData();

        // Assert
        assertThat(caughtException()).isInstanceOf(RuntimeException.class);
    }
}
