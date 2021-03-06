package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.io.FileUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.JTS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import javax.media.jai.PlanarImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String TEST_DATA_PATH = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/util/raster";

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
        RasterUtils.transformRaster(rasterFile, outputFile, new File[]{refFile}, new RasterTransformation() {
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
    public void summarizeRasterAppliesOperationCorrectly() throws Exception {
        // Arrange
        final File rasterFile = testFolder.newFile();
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), rasterFile);

        // Act
        List<Double> values = RasterUtils.summarizeRaster(rasterFile, new RasterSummaryCollator<List<Double>>() {
            private List<Double> values = new ArrayList<>();

            @Override
            public void addValue(double value) throws IOException {
                values.add(value);
            }

            @Override
            public List<Double> getSummary() throws IOException {
                return values;
            }
        });

        // Assert
        assertThat(values).hasSize(1881);
        assertThat(values.get(0)).isEqualTo(0.3600128307007253);
        assertThat(values.get(10)).isEqualTo(0.3475194713100791);
        assertThat(values.get(111)).isEqualTo(0.5636678296141326);
        assertThat(values.get(1111)).isEqualTo(0);
        assertThat(values.get(1880)).isEqualTo(0.7626093772705644);
    }

    @Test
    public void summarizeRasterThrowsIfOperationThrowsDuringAdd() throws Exception {
        // Arrange
        final File rasterFile = testFolder.newFile();
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), rasterFile);

        // Act
        Callable callable = new Callable() {
            @Override
            public Void call() throws Exception {
                RasterUtils.summarizeRaster(rasterFile, new RasterSummaryCollator<List<Double>>() {
                    private List<Double> values = new ArrayList<>();

                    @Override
                    public void addValue(double value) throws IOException {
                        values.add(value);
                        throw new IOException();
                    }

                    @Override
                    public List<Double> getSummary() throws IOException {
                        return values;
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
    public void summarizeRasterThrowsIfOperationThrowsDuringResult() throws Exception {
        // Arrange
        final File rasterFile = testFolder.newFile();
        FileUtils.copyFile(new File(TEST_DATA_PATH, "raster.tif"), rasterFile);

        // Act
        Callable callable = new Callable() {
            @Override
            public Void call() throws Exception {
                RasterUtils.summarizeRaster(rasterFile, new RasterSummaryCollator<List<Double>>() {
                    private List<Double> values = new ArrayList<>();

                    @Override
                    public void addValue(double value) throws IOException {
                        values.add(value);
                    }

                    @Override
                    public List<Double> getSummary() throws IOException {
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
        final File rasterFile = new File(TEST_DATA_PATH, "../file1_iso-8859-1.txt");

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
    @Test
    public void disposeRastersCorrectlyDisposesRasterObjects() throws Exception {
        // Arrange
        GridCoverage2D raster1 = mock(GridCoverage2D.class);
        GridCoverage2D raster2 = mock(GridCoverage2D.class);
        GridCoverage2D[] rasters = new GridCoverage2D[] {raster1, raster2, null};
        PlanarImage image1 = mock(PlanarImage.class);
        when(raster1.getRenderedImage()).thenReturn(image1);
        PlanarImage image2 = mock(PlanarImage.class);
        when(raster2.getRenderedImage()).thenReturn(image2);

        // Act
        RasterUtils.disposeRasters(rasters);

        // Assert
        verify(raster1).dispose(true);
        verify(image1).dispose();
        verify(raster2).dispose(true);
        verify(image2).dispose();
    }

    @Test
    public void disposeRastersCorrectlyHandlesNulls() throws Exception {
        // Arrange
        Callable callable = new Callable() {
            @Override
            public Void call() {
                RasterUtils.disposeRasters(null);
                return null;
            }
        };

        // Act
        catchException(callable).call();

        // Assert
        assertThat(caughtException()).isNull();
    }
}
