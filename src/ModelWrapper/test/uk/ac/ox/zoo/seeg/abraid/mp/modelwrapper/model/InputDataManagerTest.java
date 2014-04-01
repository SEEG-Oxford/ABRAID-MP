package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence;

/**
 * Tests for InputDataManagerImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeDataCreatesCorrectCsv() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        InputDataManager target = new InputDataManagerImpl();
        File dir = testFolder.newFolder();

        // Act
        target.writeData(data, dir);
        String result = FileUtils.readFileToString(Paths.get(dir.toString(), "outbreak.csv").toFile());

        // Assert
        assertThat(result).isEqualTo("-1.0,1.0,PRECISE,0.5" + System.lineSeparator());
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
