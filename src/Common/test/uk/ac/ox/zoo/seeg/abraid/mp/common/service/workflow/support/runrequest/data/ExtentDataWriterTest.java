package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import org.apache.commons.io.FileUtils;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests for ExtentDataWriter.
* Copyright (c) 2014 University of Oxford
*/
public class ExtentDataWriterTest {
    public static final String SMALL_RASTER = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/runrequest/data/testdata/SmallRaster.tif";
    public static final String SMALL_RASTER_TRANSFORMED = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/runrequest/data/testdata/SmallRaster_transformed.tif";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeShouldProduceCorrectOutputAndSetAnyUnknownValuesToNoData() throws Exception {
        // Arrange
        File sourceRaster = new File(SMALL_RASTER);
        File result = Paths.get(testFolder.newFolder().toString(), "foo.tif").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Collection<AdminUnitDiseaseExtentClass> extent = Arrays.asList(
            createMockAdminUnitDiseaseExtentClass(1, -100),
            createMockAdminUnitDiseaseExtentClass(2, -50),
            createMockAdminUnitDiseaseExtentClass(3, 0),
            createMockAdminUnitDiseaseExtentClass(4, 50),
            createMockAdminUnitDiseaseExtentClass(5, 100)
        );

        // Act
        target.write(extent, sourceRaster, result);

        // Assert
        GridCoverage2DReader reader = new GeoTiffReader(result, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
        Raster transformed = reader.read(null).getRenderedImage().getData();
        assertThat(transformed.getSample(0, 0, 0)).isEqualTo(-100);
        assertThat(transformed.getSample(1, 0, 0)).isEqualTo(-50);
        assertThat(transformed.getSample(2, 0, 0)).isEqualTo(0);
        assertThat(transformed.getSample(0, 1, 0)).isEqualTo(50);
        assertThat(transformed.getSample(1, 1, 0)).isEqualTo(100);
        assertThat(transformed.getSample(2, 1, 0)).isEqualTo(-9999);
        assertThat(transformed.getSample(0, 2, 0)).isEqualTo(-9999);
        assertThat(transformed.getSample(1, 2, 0)).isEqualTo(-9999);
        assertThat(transformed.getSample(2, 2, 0)).isEqualTo(-9999);

        // Verify the meta data fields
        assertThat(result).hasContentEqualTo(new File(SMALL_RASTER_TRANSFORMED));
    }

    private AdminUnitDiseaseExtentClass createMockAdminUnitDiseaseExtentClass(int gaul, int weighting) {
        AdminUnitDiseaseExtentClass obj = mock(AdminUnitDiseaseExtentClass.class);
        when(obj.getDiseaseExtentClass()).thenReturn(mock(DiseaseExtentClass.class));
        when(obj.getDiseaseExtentClass().getWeighting()).thenReturn(weighting);
        when(obj.getAdminUnitGlobalOrTropical()).thenReturn(mock(AdminUnitGlobalOrTropical.class));
        when(obj.getAdminUnitGlobalOrTropical().getGaulCode()).thenReturn(gaul);
        return obj;
    }

    @Test
    public void writeThrowIfSourceRasterCannotBeRead() throws Exception {
        // Arrange
        File sourceRaster = testFolder.newFile();
        FileUtils.writeStringToFile(sourceRaster, "nonsense", "UTF-8");
        File result = Paths.get(testFolder.newFolder().toString(), "foo.asc").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Collection<AdminUnitDiseaseExtentClass> extent = new ArrayList<>();

        // Act
        catchException(target).write(extent, sourceRaster, result);

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }

    @Test
    public void writeThrowIfTargetRasterCannotBeSaved() throws Exception {
        // Arrange
        File sourceRaster = testFolder.newFile();
        FileUtils.writeStringToFile(sourceRaster, SMALL_RASTER, "UTF-8");
        File result = testFolder.newFolder(); // already exists as a directory
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Collection<AdminUnitDiseaseExtentClass> extent = new ArrayList<>();

        // Act
        catchException(target).write(extent, sourceRaster, result);

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }
}
