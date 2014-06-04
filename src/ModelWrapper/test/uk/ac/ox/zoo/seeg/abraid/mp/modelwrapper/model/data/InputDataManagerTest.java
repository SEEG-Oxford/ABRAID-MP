package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.AdminUnitRunConfiguration;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for InputDataManager.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeOccurrenceDataCallsOccurrenceDataWriterWithCorrectTargetFile() throws Exception {
        // Arrange
        OccurrenceDataWriter mockOccurrenceWriter = mock(OccurrenceDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mock(ExtentDataWriter.class), mockOccurrenceWriter);
        File dataDir = testFolder.newFolder();

        // Act
        target.writeOccurrenceData(mock(GeoJsonDiseaseOccurrenceFeatureCollection.class), dataDir);

        // Assert
        verify(mockOccurrenceWriter, times(1)).write(any(GeoJsonDiseaseOccurrenceFeatureCollection.class), eq(Paths.get(dataDir.toString(), "occurrences.csv").toFile()));
    }

    @Test
    public void writeOccurrenceDataCallsOccurrenceDataWriterWithCorrectData() throws Exception {
        // Arrange
        OccurrenceDataWriter mockOccurrenceWriter = mock(OccurrenceDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mock(ExtentDataWriter.class), mockOccurrenceWriter);
        GeoJsonDiseaseOccurrenceFeatureCollection mockData = mock(GeoJsonDiseaseOccurrenceFeatureCollection.class);

        // Act
        target.writeOccurrenceData(mockData, testFolder.newFolder());

        // Assert
        verify(mockOccurrenceWriter, times(1)).write(eq(mockData), any(File.class));
    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectTargetFile() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        AdminUnitRunConfiguration mockAdminUnitRunConfiguration = setupAdminUnitRunConfiguration(true);
        File dataDir = testFolder.newFolder();

        // Act
        target.writeExtentData(new HashMap<Integer, Integer>(), mockAdminUnitRunConfiguration, dataDir);

        // Assert
        verify(mockExtentWriter, times(1)).write(
                anyMapOf(Integer.class, Integer.class),
                any(File.class),
                eq(Paths.get(dataDir.toString(), "extent.asc").toFile()));

    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectGlobalSourceRaster() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        AdminUnitRunConfiguration mockAdminUnitRunConfiguration = setupAdminUnitRunConfiguration(true);

        // Act
        target.writeExtentData(new HashMap<Integer, Integer>(), mockAdminUnitRunConfiguration, testFolder.newFolder());

        // Assert
        verify(mockExtentWriter, times(1)).write(
                anyMapOf(Integer.class, Integer.class),
                eq(new File("global.asc")),
                any(File.class));
    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectTropicalSourceRaster() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        AdminUnitRunConfiguration mockAdminUnitRunConfiguration = setupAdminUnitRunConfiguration(false);
        // Act
        target.writeExtentData(new HashMap<Integer, Integer>(), mockAdminUnitRunConfiguration, testFolder.newFolder());

        // Assert
        verify(mockExtentWriter, times(1)).write(
                anyMapOf(Integer.class, Integer.class),
                eq(new File("tropical.asc")),
                any(File.class));
    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectData() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        AdminUnitRunConfiguration mockAdminUnitRunConfiguration = setupAdminUnitRunConfiguration(true);
        HashMap<Integer, Integer> extentData = new HashMap<>();

        // Act
        target.writeExtentData(extentData, mockAdminUnitRunConfiguration, testFolder.newFolder());

        // Assert
        verify(mockExtentWriter, times(1)).write(
                eq(extentData),
                any(File.class),
                any(File.class));
    }

    private AdminUnitRunConfiguration setupAdminUnitRunConfiguration(boolean useGlobal) {
        AdminUnitRunConfiguration mockAdminUnitRunConfiguration = mock(AdminUnitRunConfiguration.class);
        when(mockAdminUnitRunConfiguration.getGlobalRasterFile()).thenReturn("global.asc");
        when(mockAdminUnitRunConfiguration.getTropicalRasterFile()).thenReturn("tropical.asc");
        when(mockAdminUnitRunConfiguration.getUseGlobalRasterFile()).thenReturn(useGlobal);
        return mockAdminUnitRunConfiguration;
    }
}
