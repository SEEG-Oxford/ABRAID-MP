package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.io.FileUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.JTS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.media.jai.PlanarImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the RasterUtils class.
 * Copyright (c) 2015 University of Oxford
 */
public class RasterUtilsTest {
    private static final String TEST_DATA_PATH = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/util";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void transformRasterAppliesCorrectTransform() throws Exception {
        // Arrange
        File expectation = new File(TEST_DATA_PATH, "expected.tif");
        File rasterFile = testFolder.newFile();
        File refFile = testFolder.newFile();
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), rasterFile);
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), refFile);
        File outputFile = testFolder.newFile();

        // Act
        RasterUtils.transformRaster(rasterFile, outputFile, new File[] {refFile}, new RasterTransformation() {
            @Override
            public void transform(WritableRaster raster, Raster[] referenceRasters) {
                for (int i = 0; i < raster.getWidth(); i++) {
                    for (int j = 0; j < raster.getHeight(); j++) {
                        double rasterValue = raster.getSampleDouble(i, j, 0);
                        double referenceValue = referenceRasters[0].getSampleDouble(i, j, 0);
                        raster.setSample(i, j, 0, rasterValue * referenceValue);
                    }
                }
            }
        });

        // Assert
        assertThat(outputFile).hasContentEqualTo(expectation);
        assertThat(outputFile.delete()).isTrue(); // Verify file locks released
        assertThat(rasterFile.delete()).isTrue(); // Verify file locks released
        assertThat(refFile.delete()).isTrue(); // Verify file locks released
    }

    @Test
    public void transformRasterThrowsIfOperationThrows() throws Exception {
        // Arrange
        final File rasterFile = testFolder.newFile();
        final File refFile = testFolder.newFile();
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), rasterFile);
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), refFile);
        final File outputFile = testFolder.newFile();


        // Act
        Callable callable = new Callable() {
            @Override
            public Void call() throws Exception {
                RasterUtils.transformRaster(rasterFile, outputFile, new File[] {refFile}, new RasterTransformation() {
                    @Override
                    public void transform(WritableRaster raster, Raster[] referenceRasters) throws IOException {
                        throw new IOException();
                    }
                });
                return null;
            }
        };
        catchException(callable).call();

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }

    @Test
    public void loadRasterOpensCorrectFile() throws Exception {
        // Arrange
        File rasterFile = new File(TEST_DATA_PATH, "raster.tif");

        // Act
        GridCoverage2D raster = null;
        try {
            raster = RasterUtils.loadRaster(rasterFile);

            // Assert
            double[] value1 = raster.evaluate(JTS.toDirectPosition(new Coordinate(0, 0), GeometryUtils.WGS_84_CRS), (double[]) null);
            assertThat(value1[0]).isEqualTo(0.0);
            double[] value2 = raster.evaluate(JTS.toDirectPosition(new Coordinate(-100, 50), GeometryUtils.WGS_84_CRS), (double[]) null);
            assertThat(value2[0]).isEqualTo(0.2772243076469749);
            double[] value3 = raster.evaluate(JTS.toDirectPosition(new Coordinate(75, -5), GeometryUtils.WGS_84_CRS), (double[]) null);
            assertThat(value3[0]).isEqualTo(-9999.0);
        } finally {
            // Cleanup
            RasterUtils.disposeRaster(raster);
        }
    }

    @Test
    public void loadRasterThrowsForMissingFile() throws Exception {
        // Arrange
        final File rasterFile = new File(TEST_DATA_PATH, "not_raster.tif");

        // Act
        Callable callable = new Callable() {
            @Override
            public Void call() throws IOException {
                GridCoverage2D raster = null;
                try {
                    raster = RasterUtils.loadRaster(rasterFile);
                } finally {
                    RasterUtils.disposeRaster(raster);
                }
                return null;
            }
        };
        catchException(callable).call();

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loadRasterThrowsForWrongFile() throws Exception {
        // Arrange
        final File rasterFile = new File(TEST_DATA_PATH, "file1_iso-8859-1.txt");

        // Act
        Callable callable = new Callable() {
            @Override
            public Void call() throws IOException {
                GridCoverage2D raster = null;
                try {
                    raster = RasterUtils.loadRaster(rasterFile);
                } finally {
                    RasterUtils.disposeRaster(raster);
                }
                return null;
            }
        };
        catchException(callable).call();

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }

    @Test
    public void saveRasterSavesCorrectData() throws Exception {
        // Arrange
        File rasterFile = new File(TEST_DATA_PATH, "raster.tif");
        File outputFile = testFolder.newFile();
        GridCoverage2D raster = null;
        try {
            raster = RasterUtils.loadRaster(rasterFile);

            // Act
            RasterUtils.saveRaster(outputFile, (WritableRaster) raster.getRenderedImage().getData(), raster.getGridGeometry().getEnvelope2D(), raster.getSampleDimensions());

            // Assert
            assertThat(outputFile).hasContentEqualTo(rasterFile);
            assertThat(outputFile.delete()).isTrue(); // Verify file locks released
        } finally {
            // Cleanup
            RasterUtils.disposeRaster(raster);
        }
    }

    @Test
    public void saveRasterThrowsForInvalidFile() throws Exception {
        // Arrange
        final File rasterFile = new File(TEST_DATA_PATH, "raster.tif");
        final File outputFile = testFolder.getRoot();
        GridCoverage2D raster = null;
        try {
            raster = RasterUtils.loadRaster(rasterFile);

            final GridCoverage2D finalRaster = raster;

            // Act
            Callable callable = new Callable() {
                @Override
                public Void call() throws IOException {
                    RasterUtils.saveRaster(outputFile, (WritableRaster) finalRaster.getRenderedImage().getData(), finalRaster.getGridGeometry().getEnvelope2D(), finalRaster.getSampleDimensions());
                    return null;
                }
            };
            catchException(callable).call();

            // Assert
            assertThat(caughtException()).isInstanceOf(IOException.class);
        } finally {
            // Cleanup
            RasterUtils.disposeRaster(raster);
        }
    }

    @Test
    public void disposeRasterCorrectlyDisposesRasterObject() throws Exception {
        // Arrange
        GridCoverage2D raster = mock(GridCoverage2D.class);
        PlanarImage image = mock(PlanarImage.class);
        when(raster.getRenderedImage()).thenReturn(image);

        // Act
        RasterUtils.disposeRaster(raster);

        // Assert
        verify(raster).dispose(true);
        verify(image).dispose();
    }

    @Test
    public void disposeRasterCorrectlyHandlesNulls() throws Exception {
        // Arrange
        Callable callable = new Callable() {
            @Override
            public Void call() {
                RasterUtils.disposeRaster(null);
                return null;
            }
        };

        // Act
        catchException(callable).call();

        // Assert
        assertThat(caughtException()).isNull();
    }
}
