package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import java.util.List;

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
        csvDataAcquirer = new CsvDataAcquirer(converter, diseaseOccurrenceDataAcquirer, csvLookupData, mock(ManualValidationEnforcer.class));
    }

    @Test
    public void acquireReturnsErrorMessageIfCannotBeParsed() {
        // Arrange
        String csv = "\nMy site,Invalid double";

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), false);

        // Assert
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).startsWith("CSV file has invalid format");
    }

    @Test
    public void acquireReturnsErrorMessageIfCannotBeConverted() {
        // Arrange
        DataAcquisitionException exception = new DataAcquisitionException("Test message");
        String csv = "\nMy site\n";

        when(converter.convert(any(CsvDiseaseOccurrence.class), anyBoolean())).thenThrow(exception);

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), false);

        // Assert
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0)).isEqualTo("Found 1 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Did not save any disease occurrences.");
        assertThat(messages.get(2)).isEqualTo("Error in CSV file on line 2: Test message.");
    }

    @Test
    public void acquireReturnsErrorMessagesIfMultipleLinesCannotBeConverted() {
        // Arrange
        DataAcquisitionException exception = new DataAcquisitionException("Test message");
        String csv = "\nMy site\nMy second site\n";

        when(converter.convert(any(CsvDiseaseOccurrence.class), anyBoolean())).thenThrow(exception);

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), false);

        // Assert
        assertThat(messages).hasSize(4);
        assertThat(messages.get(0)).isEqualTo("Found 2 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Did not save any disease occurrences.");
        assertThat(messages.get(2)).isEqualTo("Error in CSV file on line 2: Test message.");
        assertThat(messages.get(3)).isEqualTo("Error in CSV file on line 3: Test message.");
    }

    @Test
    public void initialMessageHasCorrectCountsIfAtLeastOneDiseaseOccurrenceWasAcquired() {
        // Disease occurrence 1 has location 1 (passed QC)
        // Disease occurrence 2 has location 2 (failed QC)
        // Disease occurrence 3 has location 3 (passed QC)
        // Disease occurrence 4 has location 4 (passed QC) but was not saved
        // Disease occurrence 5 has location 1 (passed QC) but was not saved
        // Disease occurrence 6 has location 2 (failed QC)
        // Disease occurrence 7 has location 3 (passed QC)
        // Disease occurrence 8 has location 4 (passed QC) but was not saved

        // Arrange
        boolean isGoldStandard = false;
        Location location1 = createLocation(1, true);
        Location location2 = createLocation(2, false);
        Location location3 = createLocation(3, true);
        Location location4 = createLocation(4, true);

        createAndSetUpDiseaseOccurrence(1, location1, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(2, location2, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(3, location3, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(4, location4, isGoldStandard, false);
        createAndSetUpDiseaseOccurrence(5, location1, isGoldStandard, false);
        createAndSetUpDiseaseOccurrence(6, location2, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(7, location3, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(8, location4, isGoldStandard, false);

        String csv = "\n1\n2\n3\n4\n5\n6\n7\n8\n";

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), isGoldStandard);

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Found 8 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Saved 5 disease occurrence(s) in 3 location(s) (of which 2 location(s) passed QC).");
    }

    @Test
    public void initialMessageHasCorrectCountsIfNoDiseaseOccurrencesWereAcquired() {
        // Arrange
        String csv = "\n";

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), false);

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Found 0 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Did not save any disease occurrences.");
    }

    @Test
    public void initialMessageHasCorrectCountsForGoldStandardDataSet() {
        // Arrange
        boolean isGoldStandard = true;
        Location location1 = createLocation(1, true);
        Location location2 = createLocation(2, false);

        createAndSetUpDiseaseOccurrence(1, location1, isGoldStandard, true);
        createAndSetUpDiseaseOccurrence(2, location2, isGoldStandard, true);

        String csv = "\n1\n2\n";

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv.getBytes(), isGoldStandard);

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Found 2 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Saved 2 disease occurrence(s) in 2 location(s) (of which 1 location(s) passed QC).");
    }

    private Location createLocation(int id, boolean hasPassedQc) {
        Location location = new Location(id);
        location.setHasPassedQc(hasPassedQc);
        return location;
    }

    private void createAndSetUpDiseaseOccurrence(int id, Location location, boolean isGoldStandard,
                                                 boolean wasDiseaseOccurrenceSaved) {
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence(id);
        diseaseOccurrence.setLocation(location);
        CsvDiseaseOccurrence csvDiseaseOccurrence = new CsvDiseaseOccurrence();
        csvDiseaseOccurrence.setSite(Integer.toString(id));
        when(converter.convert(csvDiseaseOccurrence, isGoldStandard)).thenReturn(diseaseOccurrence);
        when(diseaseOccurrenceDataAcquirer.acquire(diseaseOccurrence)).thenReturn(wasDiseaseOccurrenceSaved);
    }
}
