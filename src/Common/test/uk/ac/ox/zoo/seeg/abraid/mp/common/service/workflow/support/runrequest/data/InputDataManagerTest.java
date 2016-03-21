package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        target.writeOccurrenceData(new ArrayList<DiseaseOccurrence>(), dataDir, false);

        // Assert
        verify(mockOccurrenceWriter).write(anyListOf(DiseaseOccurrence.class), eq(Paths.get(dataDir.toString(), "occurrences.csv").toFile()), eq(true));
    }

    @Test
    public void writeOccurrenceDataCallsOccurrenceDataWriterWithCorrectData() throws Exception {
        // Arrange
        OccurrenceDataWriter mockOccurrenceWriter = mock(OccurrenceDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mock(ExtentDataWriter.class), mockOccurrenceWriter);
        List<DiseaseOccurrence> mockData = new ArrayList<>();

        // Act
        target.writeOccurrenceData(mockData, testFolder.newFolder(), false);

        // Assert
        verify(mockOccurrenceWriter).write(same(mockData), any(File.class), eq(true));
    }

    @Test
    public void writeOccurrenceDataCallsOccurrenceDataWriterWithCorrectBiasTargetFile() throws Exception {
        // Arrange
        OccurrenceDataWriter mockOccurrenceWriter = mock(OccurrenceDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mock(ExtentDataWriter.class), mockOccurrenceWriter);
        File dataDir = testFolder.newFolder();

        // Act
        target.writeOccurrenceData(new ArrayList<DiseaseOccurrence>(), dataDir, true);

        // Assert
        verify(mockOccurrenceWriter).write(anyListOf(DiseaseOccurrence.class), eq(Paths.get(dataDir.toString(), "sample_bias.csv").toFile()), eq(false));
    }

    @Test
    public void writeOccurrenceDataCallsOccurrenceDataWriterWithCorrectBiasData() throws Exception {
        // Arrange
        OccurrenceDataWriter mockOccurrenceWriter = mock(OccurrenceDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mock(ExtentDataWriter.class), mockOccurrenceWriter);
        List<DiseaseOccurrence> mockData = new ArrayList<>();

        // Act
        target.writeOccurrenceData(mockData, testFolder.newFolder(), true);

        // Assert
        verify(mockOccurrenceWriter).write(same(mockData), any(File.class), eq(false));
    }


    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectTargetFile() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        File dataDir = testFolder.newFolder();

        // Act
        target.writeExtentData(new ArrayList<AdminUnitDiseaseExtentClass>(), testFolder.newFile(), dataDir);

        // Assert
        verify(mockExtentWriter).write(
                anyListOf(AdminUnitDiseaseExtentClass.class),
                any(File.class),
                eq(Paths.get(dataDir.toString(), "extent.tif").toFile()));

    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectBaseExtentRaster() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        File baseExtentRaster = new File("global.asc");

        // Act
        target.writeExtentData(new ArrayList<AdminUnitDiseaseExtentClass>(),  baseExtentRaster, testFolder.newFolder());

        // Assert
        verify(mockExtentWriter).write(
                anyListOf(AdminUnitDiseaseExtentClass.class),
                eq(baseExtentRaster),
                any(File.class));
    }

    @Test
    public void writeExtentDataCallsExtentDataWriterWithCorrectData() throws Exception {
        // Arrange
        ExtentDataWriter mockExtentWriter = mock(ExtentDataWriter.class);
        InputDataManager target = new InputDataManagerImpl(mockExtentWriter, mock(OccurrenceDataWriter.class));
        List<AdminUnitDiseaseExtentClass> extentData = new ArrayList<AdminUnitDiseaseExtentClass>();

        // Act
        target.writeExtentData(extentData, testFolder.newFile(), testFolder.newFolder());

        // Assert
        verify(mockExtentWriter).write(
                eq(extentData),
                any(File.class),
                any(File.class));
    }
}
