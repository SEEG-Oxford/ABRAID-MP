package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the CsvDataAcquirer class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDataAcquirerTest {
    private CsvDiseaseOccurrenceConverter converter;
    private DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer;
    private CsvLookupData csvLookupData;
    private CsvDataAcquirer csvDataAcquirer;

    @Before
    public void setUp() {
        converter = mock(CsvDiseaseOccurrenceConverter.class);
        diseaseOccurrenceDataAcquirer = mock(DiseaseOccurrenceDataAcquirer.class);
        csvLookupData = mock(CsvLookupData.class);
        csvDataAcquirer = new CsvDataAcquirer(converter, diseaseOccurrenceDataAcquirer, csvLookupData);
    }

    @Test
    public void acquireThrowsExceptionIfCannotBeParsed() {
        // Arrange
        String csv = "\nMy site,Invalid double";

        // Act
        catchException(csvDataAcquirer).acquireDataFromCsv(csv);

        // Assert
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException()).hasMessageStartingWith("CSV file has invalid format");
    }

    @Test
    public void acquireThrowsExceptionIfCannotBeConverted() {
        // Arrange
        DataAcquisitionException exception = new DataAcquisitionException("Test message");
        String csv = "\nMy site\n";

        when(converter.convert(any(CsvDiseaseOccurrence.class))).thenThrow(exception);

        // Act
        catchException(csvDataAcquirer).acquireDataFromCsv(csv);

        // Assert
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException()).hasMessage("Error in CSV file on line 1: Test message");
    }

    @Test
    public void successMessageHasCorrectCountsIfAtLeastOneDiseaseOccurrenceWasAcquired() {
        // Disease occurrence 1 has location 1 (passed QC)
        // Disease occurrence 2 has location 2 (failed QC)
        // Disease occurrence 3 has location 3 (passed QC)
        // Disease occurrence 4 has location 4 (passed QC) but was not saved
        // Disease occurrence 5 has location 1 (passed QC) but was not saved
        // Disease occurrence 6 has location 2 (failed QC)
        // Disease occurrence 7 has location 3 (passed QC)
        // Disease occurrence 8 has location 4 (passed QC) but was not saved

        // Arrange
        Location location1 = createLocation(1, true);
        Location location2 = createLocation(2, false);
        Location location3 = createLocation(3, true);
        Location location4 = createLocation(4, true);

        createAndSetUpDiseaseOccurrence(1, location1, true);
        createAndSetUpDiseaseOccurrence(2, location2, true);
        createAndSetUpDiseaseOccurrence(3, location3, true);
        createAndSetUpDiseaseOccurrence(4, location4, false);
        createAndSetUpDiseaseOccurrence(5, location1, false);
        createAndSetUpDiseaseOccurrence(6, location2, true);
        createAndSetUpDiseaseOccurrence(7, location3, true);
        createAndSetUpDiseaseOccurrence(8, location4, false);

        String csv = "\n1\n2\n3\n4\n5\n6\n7\n8\n";

        // Act
        String message = csvDataAcquirer.acquireDataFromCsv(csv);

        // Assert
        assertThat(message).isEqualTo("Saved 5 disease occurrence(s) in 3 location(s) (of which 2 location(s) passed QC)");
    }

    @Test
    public void successMessageHasCorrectCountsIfNoDiseaseOccurrencesWereAcquired() {
        // Arrange
        String csv = "\n";

        // Act
        String message = csvDataAcquirer.acquireDataFromCsv(csv);

        // Assert
        assertThat(message).isEqualTo("Saved 0 disease occurrence(s) in 0 location(s) (of which 0 location(s) passed QC)");
    }

    private Location createLocation(int id, boolean hasPassedQc) {
        Location location = new Location(id);
        location.setHasPassedQc(hasPassedQc);
        return location;
    }

    private void createAndSetUpDiseaseOccurrence(int id, Location location, boolean wasDiseaseOccurrenceSaved) {
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence(id);
        diseaseOccurrence.setLocation(location);
        CsvDiseaseOccurrence csvDiseaseOccurrence = new CsvDiseaseOccurrence();
        csvDiseaseOccurrence.setSite(Integer.toString(id));
        when(converter.convert(csvDiseaseOccurrence)).thenReturn(diseaseOccurrence);
        when(diseaseOccurrenceDataAcquirer.acquire(diseaseOccurrence)).thenReturn(wasDiseaseOccurrenceSaved);
    }
}
