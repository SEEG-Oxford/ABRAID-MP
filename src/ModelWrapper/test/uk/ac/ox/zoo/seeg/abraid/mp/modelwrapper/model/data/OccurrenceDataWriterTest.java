package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
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
    public void writeCreatesCorrectCsvForDefaultOccurrencePoint() throws Exception {
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), false);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,1,102" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdjustedPrecision() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        // Act
        String result = arrangeAndActWriteDataTest(defaultDiseaseOccurrence(), true);

        // Assert - Values must be in the order: longitude, latitude, occurrence weighting, admin level value, gaul code
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,-999,102" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForPrecise() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.PRECISE);
        when(occurrence.getLocation().getAdminUnitQCGaulCode()).thenReturn(null);

        String result = arrangeAndActWriteDataTest(occurrence, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,-999,NA" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel1() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);

        String result = arrangeAndActWriteDataTest(occurrence, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,1,102" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForAdminLevel2() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN2);

        String result = arrangeAndActWriteDataTest(occurrence, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,2,102" + "\n");
    }

    @Test
    public void writeCreatesCorrectCsvForCountry() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.COUNTRY);

        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        String result = arrangeAndActWriteDataTest(occurrence, false);

        // Assert
        assertThat(result).isEqualTo("Longitude,Latitude,Weight,Admin,GAUL" + "\n" + "-1.0,1.0,0.5,0,201" + "\n");
    }

    private String arrangeAndActWriteDataTest(DiseaseOccurrence occurrence, boolean adjustPrecision) throws Exception {
        // Arrange
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
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
        target.write(data, targetFile);
        return FileUtils.readFileToString(targetFile);
    }

    @Test
    public void writeRequiresEPSG4326() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        data.setCrs(new GeoJsonNamedCrs());
        catchException(target).write(data, targetFile);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Only EPSG:4326 is supported.");
    }

    @Test
    public void writeRejectsPrecisePointWithGAUL() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.PRECISE);
        when(occurrence.getLocation().getAdminUnitQCGaulCode()).thenReturn(123);

        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        catchException(target).write(data, targetFile);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Precise location occurrences with GAUL codes are not supported.");
    }

    @Test
    public void writeRejectsFeatureLevelCrs() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        GeoJsonDiseaseOccurrenceFeatureCollection data = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(occurrence));
        OccurrenceDataWriter target = new OccurrenceDataWriterImpl(createNoopAdjuster());
        File targetFile = Paths.get(testFolder.newFolder().toString(), "outbreak.csv").toFile();

        // Act
        data.getFeatures().get(0).setCrs(new GeoJsonNamedCrs());
        catchException(target).write(data, targetFile);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("Feature level CRS are not supported.");
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
