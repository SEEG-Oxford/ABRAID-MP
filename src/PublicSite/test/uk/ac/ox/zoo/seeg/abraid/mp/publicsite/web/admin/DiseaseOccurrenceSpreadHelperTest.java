package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.DiseaseOccurrenceSpreadTable;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.DiseaseOccurrenceSpreadTableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceSpreadHelper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceSpreadHelperTest {
    private List<Country> countries;
    private DiseaseOccurrenceSpreadHelper helper;
    private DiseaseService diseaseService;
    private GeometryService geometryService;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        geometryService = mock(GeometryService.class);
        helper = new DiseaseOccurrenceSpreadHelper(diseaseService, geometryService);
        setUpCountries();
    }

    @Test
    public void getDiseaseOccurrenceSpreadTableReturnsErrorMessageIfDiseaseGroupDoesNotExist() {
        // Arrange
        int diseaseGroupId = 1000;
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(null);

        // Act
        DiseaseOccurrenceSpreadTable table = helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId);

        // Assert
        assertThat(table.getErrorMessage()).isEqualTo("This disease group does not exist.");
    }

    @Test
    public void getDiseaseOccurrenceSpreadTableReturnsErrorMessageIfThereAreNoRelevantOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        mockGetDiseaseGroupAndOccurrences(diseaseGroupId, new ArrayList<DiseaseOccurrence>());

        // Act
        DiseaseOccurrenceSpreadTable table = helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId);

        // Assert
        assertThat(table.getErrorMessage()).isEqualTo("This disease group has no relevant occurrences.");
    }

    @Test
    public void getDiseaseOccurrenceSpreadTableReturnsCorrectTableFor1EligibleOccurrence() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(1, "2013-07-04T00:00:00Z", 23, LocationPrecision.PRECISE, true);
        mockGetDiseaseGroupAndOccurrences(diseaseGroupId, Arrays.asList(occurrence));

        // Act
        DiseaseOccurrenceSpreadTable table = helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId);

        // Assert
        assertThat(table.getErrorMessage()).isNull();
        assertThat(table.getHeadingYears()).hasSize(1);
        assertThat(table.getHeadingYears()).contains(2013);

        assertThat(table.getTotalRow()).containsExactly(1);

        List<DiseaseOccurrenceSpreadTableRow> rows = table.getRows();
        assertThat(rows).hasSize(5);
        assertRowIsEqual(rows.get(0), "Sudan", true, 0);
        assertRowIsEqual(rows.get(1), "Tunisia", true, 0);
        assertRowIsEqual(rows.get(2), "Bangladesh", false, 1);
        assertRowIsEqual(rows.get(3), "Nicaragua", false, 0);
        assertRowIsEqual(rows.get(4), "Yemen", false, 0);
    }

    @Test
    public void getDiseaseOccurrenceSpreadTableReturnsCorrectTableForManyOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        List<DiseaseOccurrence> occurrences = Arrays.asList(
                createDiseaseOccurrence(1, "2012-01-01T12:00:54Z", 6, LocationPrecision.PRECISE, true),
                createDiseaseOccurrence(2, "2012-10-08T05:23:02Z", 23, LocationPrecision.COUNTRY, false), // Excluded from count
                createDiseaseOccurrence(3, "2014-07-31T15:27:00Z", 269, LocationPrecision.PRECISE, true),
                createDiseaseOccurrence(4, "2011-01-01T00:00:35Z", 248, LocationPrecision.ADMIN1, true),
                createDiseaseOccurrence(5, "2011-02-05T17:01:01Z", 248, LocationPrecision.ADMIN2, true),
                createDiseaseOccurrence(6, "2012-04-02T08:24:00Z", 23, LocationPrecision.PRECISE, true),
                createDiseaseOccurrence(7, "2012-03-31T09:00:00Z", 269, LocationPrecision.PRECISE, true),
                createDiseaseOccurrence(8, "2011-07-04T00:00:00Z", 248, LocationPrecision.COUNTRY, true));  // included in count
        mockGetDiseaseGroupAndOccurrences(diseaseGroupId, occurrences);

        // Act
        DiseaseOccurrenceSpreadTable table = helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId);

        // Assert
        assertThat(table.getErrorMessage()).isNull();
        assertSetIsEqual(table.getHeadingYears(), 2011, 2012, 2014);

        assertThat(table.getTotalRow()).containsExactly(3, 3, 1);

        List<DiseaseOccurrenceSpreadTableRow> rows = table.getRows();
        assertThat(rows).hasSize(5);
        assertRowIsEqual(rows.get(0), "Sudan", true, 0, 1, 0);
        assertRowIsEqual(rows.get(1), "Tunisia", true, 3, 0, 0);
        assertRowIsEqual(rows.get(2), "Bangladesh", false, 0, 1, 0);
        assertRowIsEqual(rows.get(3), "Nicaragua", false, 0, 0, 0);
        assertRowIsEqual(rows.get(4), "Yemen", false, 0, 1, 1);
    }

    private void setUpCountries() {
        countries = new ArrayList<>();
        countries.add(new Country(248, "Tunisia", true));
        countries.add(new Country(23, "Bangladesh", false));
        countries.add(new Country(269, "Yemen", false));
        countries.add(new Country(6, "Sudan", true));
        countries.add(new Country(180, "Nicaragua", false));
        when(geometryService.getAllCountries()).thenReturn(countries);
    }

    private void mockGetDiseaseGroupAndOccurrences(int diseaseGroupId, List<DiseaseOccurrence> occurrences) {
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(new DiseaseGroup(diseaseGroupId));
        when(diseaseService.getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(
                diseaseGroupId,
                DiseaseOccurrenceStatus.READY,
                DiseaseOccurrenceStatus.IN_REVIEW,
                DiseaseOccurrenceStatus.AWAITING_BATCHING)).thenReturn(occurrences);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int id, String occurrenceDate, Integer countryGaulCode,
                                                      LocationPrecision precision, boolean modelEligible) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence(id);
        occurrence.setOccurrenceDate(new DateTime(occurrenceDate));
        Location location = new Location();
        location.setCountryGaulCode(countryGaulCode);
        location.setPrecision(precision);
        location.setIsModelEligible(modelEligible);
        occurrence.setLocation(location);
        return occurrence;
    }

    private void assertRowIsEqual(DiseaseOccurrenceSpreadTableRow row, String name, boolean isForMinimumDiseaseSpread,
                                  int... occurrenceCounts) {
        assertThat(row.getCountryName()).isEqualTo(name);
        assertThat(row.getIsForMinimumDiseaseSpread()).isEqualTo(isForMinimumDiseaseSpread);

        assertThat(row.getOccurrenceCounts()).hasSize(occurrenceCounts.length);
        int i = 0;
        for (Integer occurrenceCount : row.getOccurrenceCounts()) {
            assertThat(occurrenceCount).isEqualTo(occurrenceCounts[i]);
            i++;
        }
    }

    private <T> void assertSetIsEqual(Collection<T> set, T... contents) {
        assertThat(set).hasSize(contents.length);
        int i = 0;
        for (T setItem : set) {
            assertThat(setItem).isEqualTo(contents[i]);
            i++;
        }
    }
}
