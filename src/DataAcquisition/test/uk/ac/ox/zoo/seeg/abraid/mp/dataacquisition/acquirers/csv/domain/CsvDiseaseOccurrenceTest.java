package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CsvDiseaseOccurrence class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceTest {
    @Test
    public void readCsvFileReturnsAnEmptyListIfInputIsNull() throws Exception {
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv(null);
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void readCsvFileReturnsAnEmptyListIfInputIsEmpty() throws Exception {
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv("");
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void readCsvFileReturnsAnEmptyListIfInputOnlyHasTheHeaderRow() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n";
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv(csv);
        assertThat(occurrences).isEmpty();
    }

    @Test
    public void readCsvFileReadsOneUnterminatedValidLineSuccessfully() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n" +
                     "\"Scott County, Kentucky, United States\"";
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv(csv);
        assertThat(occurrences).hasSize(1);
        assertThat(occurrences.get(0).getSite()).isEqualTo("Scott County, Kentucky, United States");
    }

    @Test
    public void readCsvFileReadsOneLineAndOneUnterminatedLineSuccessfully() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n" +
                "\"Scott County, Kentucky, United States\"\n" +
                "Chelyabinsk Oblast";
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv(csv);
        assertThat(occurrences).hasSize(2);
        assertThat(occurrences.get(0).getSite()).isEqualTo("Scott County, Kentucky, United States");
        assertThat(occurrences.get(1).getSite()).isEqualTo("Chelyabinsk Oblast");
    }

    @Test
    public void readCsvFileReadsValidLinesSuccessfully() throws Exception {
        // The CSV reader allows:
        // * double-quoted fields (there must be no whitespace around the double quotes or the parser can get confused)
        // * whitespace around fields
        // * missing columns (but not extra columns)
        // * any valid value in the Double columns
        // * Windows, Unix or Mac line separators
        // There must be a header line, the contents of which are ignored (the column order is fixed).

        // Arrange
        String csv =
                "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Feed,Alert Title,Summary,URL,Redundant Extra Header Column\n" +
                "\" Ingombota, Luanda, Angola \", 13.22884, -8.8179,PRECISE,   Angola    , Cholera ,\"30/06/2006\",SEEG data 2014,\"PRO/EDR> Cholera, diarrhea & dysentery update 2006 (27)\",,http://promedmail.org/direct.php?id=20060630.1807\r\n" +
                "Bangladesh,,23,COUNTRY,Bangladesh,Smallpox,2007,SEEG data 2014,\"Small pox: State alert to stay till WHO confirmation\",\"AGARTALA: The central government has sounded a health alert in India's northeast asking authorities to take immediate precautionary measures following reports of a smallpox outbreak in Bangladesh and Myanmar, officials said Monday. 'Following the\",\"http://c.moreover.com/click/here.pl?r962376995\"\r" +
                "831274,00021.58105000,\"-9.5989\",precise,Indonesia,P. falciparum,2006,Malaria Atlas Project human infection data 2014\n";

        // Act
        List<CsvDiseaseOccurrence> occurrences = CsvDiseaseOccurrence.readFromCsv(csv);

        // Assert
        assertThat(occurrences).hasSize(3);
        assertThat(occurrences.get(0)).isEqualTo(new CsvDiseaseOccurrence("Ingombota, Luanda, Angola", 13.22884,
                -8.8179, "PRECISE", "Angola", "Cholera", "30/06/2006", "SEEG data 2014",
                "PRO/EDR> Cholera, diarrhea & dysentery update 2006 (27)", null,
                "http://promedmail.org/direct.php?id=20060630.1807"));
        assertThat(occurrences.get(1)).isEqualTo(new CsvDiseaseOccurrence("Bangladesh", null, 23.0, "COUNTRY",
                "Bangladesh", "Smallpox", "2007", "SEEG data 2014", "Small pox: State alert to stay till WHO confirmation",
                "AGARTALA: The central government has sounded a health alert in India's northeast asking authorities to take immediate precautionary measures following reports of a smallpox outbreak in Bangladesh and Myanmar, officials said Monday. 'Following the",
                "http://c.moreover.com/click/here.pl?r962376995"));
        assertThat(occurrences.get(2)).isEqualTo(new CsvDiseaseOccurrence("831274", 21.58105, -9.5989, "precise",
                "Indonesia", "P. falciparum", "2006", "Malaria Atlas Project human infection data 2014", null, null, null));
    }

    @Test(expected = IOException.class)
    public void readCsvFileThrowsExceptionIfTooManyColumns() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Feed,Alert Title,Summary,URL\n" +
                "My site,10,20,PRECISE,England,Dengue,2012,My feed,My title,My summary,My URL,Extra column\n";
        CsvDiseaseOccurrence.readFromCsv(csv);
    }

    @Test(expected = IOException.class)
    public void readCsvFileThrowsExceptionOnInvalidDouble() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n" +
                "My site,Invalid Double,20,PRECISE,England,Dengue,2012,My title,My summary,My URL\n";
        CsvDiseaseOccurrence.readFromCsv(csv);
    }

    @Test(expected = IOException.class)
    public void readCsvFileThrowsExceptionOnInvalidDouble2() throws Exception {
        String csv = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n" +
                "My site,\"1,4\",20,PRECISE,England,Dengue,2012,My title,My summary,My URL\n";
        CsvDiseaseOccurrence.readFromCsv(csv);
    }
}
