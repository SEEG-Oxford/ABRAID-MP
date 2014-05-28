package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
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
    // A small reclassified crop of our real global gaul raster
    private static final String SMALL_RASTER =
        "ncols        20\n" +
        "nrows        17\n" +
        "xllcorner    34.541666666667\n" +
        "yllcorner    29.083333333333\n" +
        "cellsize     0.041666666667\n" +
        "NODATA_value  -9999\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12321 12321 12321 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12321 12321 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 12321 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 12321 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 12321 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 12321 12321 5412 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 12321 -9999 -9999 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 12345 -9999 -9999 -9999 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 12345 -9999 -9999 -9999 5412 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 5412 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 5412 5412 5412 5412 5412 5412 5412 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 5412 5412 5412\n" +
        " 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321\n" +
        " 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321\n" +
        " 12345 12345 12345 12345 12345 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321\n" +
        " 12345 12345 12345 12345 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321\n" +
        " 12345 12345 12345 -9999 -9999 -9999 -9999 -9999 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321 4321\n";


    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Before
    public void setup() {
        java.util.logging.Logger.getLogger("class it.geosolutions.imageio.plugins.arcgrid").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.geotools.gce.arcgrid").setLevel(java.util.logging.Level.OFF);
    }

    @After
    public void tearDown() {
        java.util.logging.Logger.getLogger("class it.geosolutions.imageio.plugins.arcgrid").setLevel(java.util.logging.Level.ALL);
        java.util.logging.Logger.getLogger("org.geotools.gce.arcgrid").setLevel(java.util.logging.Level.ALL);
    }


    @Test
    public void writeShouldProduceCorrectOutput() throws Exception {
        // Arrange
        File sourceRaster = testFolder.newFile();
        FileUtils.writeStringToFile(sourceRaster, SMALL_RASTER, "UTF-8");
        File result = Paths.get(testFolder.newFolder().toString(), "foo.asc").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Map<Integer, Integer> transform = new HashMap<>();
        transform.put(12345, 98765);
        transform.put(5412, 789);
        transform.put(12321, 6789);
        transform.put(4321, 6987);
        String expectation = createExpectation(transform, transform.keySet(), SMALL_RASTER);

        // Act
        target.write(transform, sourceRaster, result);

        // Assert
        String resultString = FileUtils.readFileToString(result);
        assertThat(resultString).isEqualTo(expectation);
    }

    @Test
    public void writeShouldSetAnyUnknownValuesToNoData() throws Exception {
        // Arrange
        File sourceRaster = testFolder.newFile();
        FileUtils.writeStringToFile(sourceRaster, SMALL_RASTER, "UTF-8");
        File result = Paths.get(testFolder.newFolder().toString(), "foo.asc").toFile();
        ExtentDataWriter target = new ExtentDataWriterImpl();
        Map<Integer, Integer> transform = new HashMap<>();
        transform.put(12345, 98765);
        transform.put(5412, 789);
        transform.put(12321, 6789);
        String expectation = createExpectation(transform, Arrays.asList(12345, 5412, 12321, 4321), SMALL_RASTER);

        // Act
        target.write(transform, sourceRaster, result);

        // Assert
        String resultString = FileUtils.readFileToString(result);
        assertThat(resultString).isEqualTo(expectation);
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
            String targetValue = targetInt != null ? Double.toString(targetInt) : "-9999";

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
