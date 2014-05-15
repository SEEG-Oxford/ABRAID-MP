package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence;

/**
 * Tests for InputDataManagerImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeDataCreatesCorrectCsvForDefaultOccurrencePoint() throws Exception {
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence());

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("-1.0,1.0,0.5,-999,NA" + System.lineSeparator());
    }

    @Test
    public void writeDataCreatesCorrectCsvForAdminLevel1() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        String result = arrangeAndActWriteDataTest(occurrence);

        // Assert
        assertThat(result).isEqualTo("-1.0,1.0,0.5,1,102" + System.lineSeparator());
    }

    @Test
    public void writeDataCreatesCorrectCsvForAdminLevel2() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN2);

        String result = arrangeAndActWriteDataTest(occurrence);

        // Assert
        assertThat(result).isEqualTo("-1.0,1.0,0.5,2,102" + System.lineSeparator());
    }

    private String arrangeAndActWriteDataTest(DiseaseOccurrence occurrence) throws Exception {
        // Arrange
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        InputDataManager target = new InputDataManagerImpl();
        File dir = testFolder.newFolder();

        // Act
        target.writeData(data, dir);
        return FileUtils.readFileToString(Paths.get(dir.toString(), "outbreak.csv").toFile());
    }

    @Test
    public void writeDataRequiresEPSG4326() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        InputDataManager target = new InputDataManagerImpl();
        File dir = testFolder.newFolder();

        // Act
        data.setCrs(new GeoJsonNamedCrs());
        catchException(target).writeData(data, dir);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Only EPSG:4326 is supported.");
    }

    @Test
    public void writeDataRejectsFeatureLevelCrs() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        InputDataManager target = new InputDataManagerImpl();
        File dir = testFolder.newFolder();

        // Act
        data.getFeatures().get(0).setCrs(new GeoJsonNamedCrs());
        catchException(target).writeData(data, dir);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Feature level CRS are not supported.");
    }
}
