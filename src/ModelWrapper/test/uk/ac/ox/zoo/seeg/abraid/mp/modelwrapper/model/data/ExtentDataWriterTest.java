package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.commons.io.FileUtils;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for ExtentDataWriter.
 * Copyright (c) 2014 University of Oxford
 */
public class ExtentDataWriterTest {
    public static final String SMALL_RASTER = "ModelWrapper/test/uk/ac/ox/zoo/seeg/abraid/mp/modelwrapper/model/testdata/SmallRaster.tif";
    public static final String SMALL_RASTER_TRANSFORMED = "ModelWrapper/test/uk/ac/ox/zoo/seeg/abraid/mp/modelwrapper/model/testdata/SmallRaster_transformed.tif";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void writeShouldProduceCorrectOutputAndSetAnyUnknownValuesToNoData() throws Exception {
        // Arrange
        File sourceRaster = new File(SMALL_RASTER);
        File result = Paths.get(testFolder.newFolder().toString(), "foo.tif").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Map<Integer, Integer> transform = new HashMap<>();
        transform.put(1, -100);
        transform.put(2, -50);
        transform.put(3, 0);
        transform.put(4, 50);
        transform.put(5, 100);

        // Act
        target.write(transform, sourceRaster, result);

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

    @Test
    public void writeThrowIfSourceRasterCannotBeRead() throws Exception {
        // Arrange
        File sourceRaster = testFolder.newFile();
        FileUtils.writeStringToFile(sourceRaster, "nonsense", "UTF-8");
        File result = Paths.get(testFolder.newFolder().toString(), "foo.asc").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Map<Integer, Integer> transform = new HashMap<>();

        // Act
        catchException(target).write(transform, sourceRaster, result);

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
        Map<Integer, Integer> transform = new HashMap<>();

        // Act
        catchException(target).write(transform, sourceRaster, result);

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }

    private String createExpectation(Map<Integer, Integer> transform, Collection<Integer> allValuesInRaster, String initial) {
        String expectation = initial;
        for (Integer sourceInt : allValuesInRaster) {
            String sourceValue = sourceInt.toString();
            Integer targetInt = transform.containsKey(sourceInt) ? transform.get(sourceInt) : null;

            // ArcGridWriter writes cell outputs with Double.toString (trailing '.0')
            String targetValue = (targetInt != null) ? Double.toString(targetInt) : "-9999";

            expectation = expectation.replace(sourceValue, targetValue);
        }

        // ArcGridWriter does print the leading space on data rows
        expectation = expectation.replace("\n ", "\n");

        // ArcGridWriter will use the system native line separator
        expectation = expectation.replace("\n", System.lineSeparator());

        // ArcGridWriter capitalises it's metadata fields
        expectation = expectation.toUpperCase();

        // ArcGridWriter does not indent it's fields
        expectation = expectation.replaceAll(" +", " ");

        // ArcGridWriter uses undue precision when printing cell size
        expectation = expectation.replace("CELLSIZE 0.041666666667", "CELLSIZE 0.04166666666700003");

        return expectation;
    }
}
