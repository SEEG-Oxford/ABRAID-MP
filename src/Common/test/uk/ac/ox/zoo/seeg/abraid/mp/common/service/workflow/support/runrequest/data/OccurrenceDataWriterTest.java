package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence;

/**
 * Tests for OccurrenceDataWriter.
 * Copyright (c) 2014 University of Oxford
 */
public class OccurrenceDataWriterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeCreatesCorrectCsvForDefaultOccurrencePointWithWeight() throws Exception {
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), false, true);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,1,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdjustedPrecisionWithWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        // Act
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), true, true);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,-999,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForPreciseWithWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.PRECISE);

        String result = arrangeAndActWriteDataTest(occurrence, false, true);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,-999,NA,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel1WithWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        String result = arrangeAndActWriteDataTest(occurrence, false, true);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,1,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel2WithWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN2);

        String result = arrangeAndActWriteDataTest(occurrence, false, true);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,2,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForCountryWithWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.COUNTRY);

        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        String result = arrangeAndActWriteDataTest(occurrence, false, true);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0.5,0,201,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForDefaultOccurrencePointWithoutWeight() throws Exception {
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), false, false);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,1,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdjustedPrecisionWithoutWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        // Act
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), true, false);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,-999,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForPreciseWithoutWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.PRECISE);

        String result = arrangeAndActWriteDataTest(occurrence, false, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,-999,NA,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel1WithoutWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        String result = arrangeAndActWriteDataTest(occurrence, false, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,1,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel2WithoutWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN2);

        String result = arrangeAndActWriteDataTest(occurrence, false, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,2,102,123,1970-01-01" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForCountryWithoutWeight() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.COUNTRY);

        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        String result = arrangeAndActWriteDataTest(occurrence, false, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date" + "\n" + "-1.0,1.0,0,201,123,1970-01-01" + "\n");
    }

    private String arrangeAndActWriteDataTest(DiseaseOccurrence occurrence, boolean adjustPrecision, boolean includeWeight) throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence);
        ModellingLocationPrecisionAdjuster adjuster = null;
        if (adjustPrecision) {
            adjuster = mock(ModellingLocationPrecisionAdjuster.class);
            when(adjuster.adjust(anyInt(), anyString())).thenReturn(-999);
        } else {
            adjuster = createNoopAdjuster();
        }
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(adjuster);
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        target.write(occurrences, targetFile, includeWeight);
        return FileUtils.readFileToString(targetFile);
    }

    private ModellingLocationPrecisionAdjuster createNoopAdjuster() {
        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(anyInt(), anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[0];
            }
        });
        return adjuster;
    }
}
