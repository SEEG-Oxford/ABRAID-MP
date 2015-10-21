package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.apache.log4j.Logger;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.geometry.Envelope2D;
import org.geotools.resources.image.ImageUtilities;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import javax.media.jai.PlanarImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Utilities for working with raster files.
 * Copyright (c) 2015 University of Oxford
 */
public final class RasterUtils {
    private RasterUtils() {
    }
    private static final Logger LOGGER = Logger.getLogger(RasterUtils.class);
    private static final String LOG_FAILED_TO_READ_RASTER =
            "Failed to read raster: %s.";
    private static final String LOG_FAILED_TO_SAVE_TRANSFORMED_RASTER =
            "Failed to save transformed raster: %s.";
    private static final String LOG_SAVING_RASTER =
            "Saving transformed raster: %s";
    private static final String LOG_LOADING_SOURCE_RASTER =
            "Loading source raster.";
    private static final String LOG_LOADING_REFERENCE_RASTERS =
            "Loading reference rasters.";
    private static final Object LOG_TRANSFORMING_RASTER_DATA =
            "Applying raster transformation.";
    private static final String LOG_READING_RASTER =
            "Reading raster: %s.";
    private static final String CANNOT_FIND_FILE_MESSAGE = "Cannot find raster file %s";

    private static final AbstractGridFormat GEOTIFF_FORMAT = new GeoTiffFormat();

    private static final float GEOTIFF_COMPRESSION_QUALITY = 1F;
    // This equates to "level 9" (level = (int)(1 + 8*quality))
    // https://github.com/geosolutions-it/imageio-ext/blob/master/plugin/tiff/src/main/java/it/geosolutions/imageioimpl
    // /plugins/tiff/TIFFDeflater.java#L105
    // The compression is loss less. The level is a speed vs compression trade-off.

    private static final String GEOTIFF_COMPRESSION_TYPE = "Deflate";
    private static final Hints RASTER_READ_HINTS =
            new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, GeometryUtils.WGS_84_CRS);

    /**
     * The standard raster NODATA value (-9999).
     */
    public static final int NO_DATA_VALUE = -9999;

    /**
     * A alternative raster NODATA value, to be used instead of RasterUtils.NO_DATA_VALUE for pixels where the value is
     * intentionally excluded, but which are on land.
     */
    public static final int UNKNOWN_VALUE = +9999;

    /**
     * Applies a transformation operation to a raster file and saves the updated raster at a new location.
     * @param sourceRasterFile The file location of the raster to be transformed.
     * @param targetRasterFile The file location at which to save the updated raster.
     * @param referenceRasterFiles The file locations of any additional raster required to perform the transformation.
     * @param transformation The RasterTransformation to be performed.
     * @throws IOException thrown if unable to complete the transformation.
     */
    public static void transformRaster(File sourceRasterFile, File targetRasterFile, File[] referenceRasterFiles,
                                       RasterTransformation transformation) throws IOException {
        int numberOfReferenceRasters = referenceRasterFiles.length;
        GridCoverage2D sourceRaster = null;
        GridCoverage2D[] referenceRasters = new GridCoverage2D[numberOfReferenceRasters];

        try {
            // Load source raster
            LOGGER.info(LOG_LOADING_SOURCE_RASTER);
            sourceRaster = loadRaster(sourceRasterFile);
            // Extract raw data from the source raster
            WritableRaster rasterData = (WritableRaster) sourceRaster.getRenderedImage().getData();
            // Extract meta data from the source raster
            Envelope2D rasterExtent = sourceRaster.getGridGeometry().getEnvelope2D();
            GridSampleDimension[] rasterProperties = sourceRaster.getSampleDimensions();

            // Load reference rasters
            LOGGER.info(LOG_LOADING_REFERENCE_RASTERS);
            for (int i = 0; i < numberOfReferenceRasters; i++) {
                referenceRasters[i] = loadRaster(referenceRasterFiles[i]);
            }
            // Extract raw data from the reference rasters
            Raster[] referenceRastersData = new Raster[numberOfReferenceRasters];
            for (int i = 0; i < numberOfReferenceRasters; i++) {
                referenceRastersData[i] = referenceRasters[i].getRenderedImage().getData();
            }

            // Apply transformation
            LOGGER.info(LOG_TRANSFORMING_RASTER_DATA);
            transformation.transform(rasterData, referenceRastersData);

            // Save result
            LOGGER.info(String.format(LOG_SAVING_RASTER, targetRasterFile.getAbsolutePath()));
            saveRaster(targetRasterFile, rasterData, rasterExtent, rasterProperties);
        } finally {
            disposeRaster(sourceRaster);
            for (int i = 0; i < numberOfReferenceRasters; i++) {
                disposeRaster(referenceRasters[i]);
            }
        }
    }

     /**
     * Applies a summation operation to a raster file.
     * @param rasterFile The file location of the raster.
     * @param collator The summary operation to to be performed.
     * @param <TResult> The data type of the summary result.
     * @return The summary result.
     * @throws IOException thrown if unable to complete the operation.
     */
    public static <TResult> TResult summarizeRaster(File rasterFile, RasterSummaryCollator<TResult> collator)
            throws IOException {
        GridCoverage2D raster = null;

        try {
            // Load source raster
            LOGGER.info(LOG_LOADING_SOURCE_RASTER);
            raster = loadRaster(rasterFile);
            // Extract raw data from the source raster
            WritableRaster rasterData = (WritableRaster) raster.getRenderedImage().getData();

            for (int i = 0; i < rasterData.getWidth(); i++) {
                for (int j = 0; j < rasterData.getHeight(); j++) {
                    double rasterValue = rasterData.getSampleDouble(i, j, 0);
                    if (rasterValue != RasterUtils.NO_DATA_VALUE) {
                        collator.addValue(rasterValue);
                    }
                }
            }

            return collator.getSummary();
        } finally {
            disposeRaster(raster);
        }
    }

    /**
     * Load a raster file from a given location. This function assumes WGS84 GeoTiff files.
     * NOTE: All loaded rasters must subsequently be disposed using RasterUtils.disposeRaster.
     * @param location The file location from which to load the raster.
     * @return The raster coverage object.
     * @throws IOException thrown if unable to load the raster.
     */
    public static GridCoverage2D loadRaster(File location) throws IOException {
        LOGGER.debug(String.format(LOG_READING_RASTER, location.getAbsolutePath()));
        if (location.exists()) {
            GridCoverage2DReader reader = null;
            try {
                reader = new GeoTiffReader(location, RASTER_READ_HINTS);
                return reader.read(null);
            } catch (Exception e) {
                final String message = String.format(LOG_FAILED_TO_READ_RASTER, location.toString());
                LOGGER.error(message, e);
                throw new IOException(message, e);
            } finally {
                disposeResource(reader);
            }
        } else {
            String message = String.format(CANNOT_FIND_FILE_MESSAGE, location.getAbsolutePath());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Save a set of raster data at a given location.
     * @param location The file location at which to save the raster.
     * @param raster The raw pixel values for the raster.
     * @param extents The extent of the raster.
     * @param properties The meta-data for the raster.
     * @throws IOException thrown if unable to save the raster.
     */
    public static void saveRaster(File location, WritableRaster raster,
                                  Envelope2D extents, GridSampleDimension[] properties) throws IOException {
        GridCoverage2D targetRaster = null;
        try {
            GridCoverageFactory factory = new GridCoverageFactory();
            targetRaster = factory.create(location.getName(), raster, extents, properties);
            saveRaster(location, targetRaster);
        } finally {
            disposeRaster(targetRaster);
        }
    }

    /**
     * Save a set of raster data at a given location.
     * @param location The file location at which to save the raster.
     * @param targetRaster The raw pixel values & metadata for the raster.
     * @throws IOException thrown if unable to save the raster.
     */
    public static void saveRaster(File location, GridCoverage2D targetRaster) throws IOException {
        GridCoverageWriter writer = null;
        try {
            writer = GEOTIFF_FORMAT.getWriter(location);
            writer.write(targetRaster, getGeoTiffWriteParameters());
        } catch (Exception e) {
            final String message = String.format(LOG_FAILED_TO_SAVE_TRANSFORMED_RASTER, location.toString());
            LOGGER.error(message, e);
            throw new IOException(message, e);
        } finally {
            disposeResource(writer);
        }
    }

    /**
     * Correctly dispose of a GridCoverage2D raster object.
     * This includes disposing the PlanarImage object that has a read lock on the raster file.
     * It also invokes a forced garbage collection, as something in the JAI stack doesn't properly release the image
     * until the next GC. Without this there are intermittent unit test failures (rasters being reopened by different
     * tests before the image is released).
     * @param raster The raster to be disposed.
     */
    public static void disposeRaster(GridCoverage2D raster) {
        if (raster != null) {
            RenderedImage image = raster.getRenderedImage();
            if (image != null) {
                ImageUtilities.disposePlanarImageChain((PlanarImage) image);
            }
            raster.dispose(true);
            System.gc();
        }
    }

    /**
     * Correctly dispose of an array of GridCoverage2D raster object.
     * This includes disposing the PlanarImage object that has a read lock on the raster file.
     * @param rasters The rasters to be disposed.
     */
    public static void disposeRasters(GridCoverage2D[] rasters) {
        if (rasters != null) {
            for (int i = 0; i < rasters.length; i++) {
                disposeRaster(rasters[i]);
                rasters[i] = null;
            }
        }
    }

    private static GeneralParameterValue[] getGeoTiffWriteParameters() {
        GeoTiffWriteParams writeParams = new GeoTiffWriteParams();
        writeParams.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
        writeParams.setCompressionType(GEOTIFF_COMPRESSION_TYPE);
        writeParams.setCompressionQuality(GEOTIFF_COMPRESSION_QUALITY);
        ParameterValue parameterValue = AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.createValue();
        parameterValue.setValue(writeParams);
        return new GeneralParameterValue[] {
                parameterValue
        };
    }

    private static void disposeResource(GridCoverage2DReader reader) throws IOException {
        if (reader != null) {
            reader.dispose();
        }
    }

    private static void disposeResource(GridCoverageWriter writer) throws IOException {
        if (writer != null) {
            writer.dispose();
        }
    }
}
