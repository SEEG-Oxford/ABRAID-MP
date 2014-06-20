package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
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
    public void writeCreatesCorrectCsvForDefaultOccurrencePoint() throws Exception {
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence());

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + System.lineSeparator() + "-1.0,1.0,0.5,-999,NA" + System.lineSeparator());
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel1() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        String result = arrangeAndActWriteDataTest(occurrence);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + System.lineSeparator() + "-1.0,1.0,0.5,1,102" + System.lineSeparator());
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel2() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN2);

        String result = arrangeAndActWriteDataTest(occurrence);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + System.lineSeparator() + "-1.0,1.0,0.5,2,102" + System.lineSeparator());
    }

    private String arrangeAndActWriteDataTest(DiseaseOccurrence occurrence) throws Exception {
        // Arrange
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl();
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        target.write(data, targetFile);
        return FileUtils.readFileToString(targetFile);
    }

    @Test
    public void writeRequiresEPSG4326() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl();
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        data.setCrs(new GeoJsonNamedCrs());
        catchException(target).write(data, targetFile);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Only EPSG:4326 is supported.");
    }

    @Test
    public void writeRejectsFeatureLevelCrs() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl();
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        data.getFeatures().get(0).setCrs(new GeoJsonNamedCrs());
        catchException(target).write(data, targetFile);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Feature level CRS are not supported.");
    }
}
