package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

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

import javax.media.jai.PlanarImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
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
            "Applying raster transforming raster.";
    private static final String LOG_READING_RASTER =
            "Reading raster: %s.";
    private static final String CANNOT_FIND_FILE_MESSAGE = "Cannot find raster file %s";

    public static final int NO_DATA_VALUE = -9999;
    public static final int UNKNOWN_VALUE = +9999;
    private static final AbstractGridFormat GEOTIFF_FORMAT = new GeoTiffFormat();
    private static final float GEOTIFF_COMPRESSION_LEVEL = 0.9F;
    private static final String GEOTIFF_COMPRESSION_TYPE = "Deflate";
    private static final Hints RASTER_READ_HINTS =
            new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, GeometryUtils.WGS_84_CRS);

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
            for (int i=0; i < numberOfReferenceRasters; i++) {
                referenceRasters[i] = loadRaster(referenceRasterFiles[i]);
            }
            // Extract raw data from the reference rasters
            Raster[] referenceRastersData = new Raster[numberOfReferenceRasters];
            for (int i=0; i < numberOfReferenceRasters; i++) {
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
            for (int i=0; i < numberOfReferenceRasters; i++) {
                disposeRaster(referenceRasters[i]);
            }
        }
    }

    public static GridCoverage2D loadRaster(File location) throws IOException {
        LOGGER.info(String.format(LOG_READING_RASTER, location.getAbsolutePath()));
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

    public static void saveRaster(File location, WritableRaster raster, Envelope2D extents, GridSampleDimension[] properties)
            throws IOException {
        GridCoverage2D targetRaster = null;
        GridCoverageWriter writer = null;

        try {
            GridCoverageFactory factory = new GridCoverageFactory();
            targetRaster = factory.create(location.getName(), raster, extents, properties);

            writer = GEOTIFF_FORMAT.getWriter(location);
            writer.write(targetRaster, getGeoTiffWriteParameters());
        } catch (Exception e) {
            final String message = String.format(LOG_FAILED_TO_SAVE_TRANSFORMED_RASTER, location.toString());
            LOGGER.error(message, e);
            throw new IOException(message, e);
        } finally {
            disposeRaster(targetRaster);
            disposeResource(writer);
        }
    }

    private static GeneralParameterValue[] getGeoTiffWriteParameters() {
        GeoTiffWriteParams writeParams = new GeoTiffWriteParams();
        writeParams.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
        writeParams.setCompressionType(GEOTIFF_COMPRESSION_TYPE);
        writeParams.setCompressionQuality(GEOTIFF_COMPRESSION_LEVEL);
        ParameterValue parameterValue = AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.createValue();
        parameterValue.setValue(writeParams);
        return new GeneralParameterValue[] {
                parameterValue
        };
    }

    public static void disposeRaster(GridCoverage2D raster) {
        if (raster != null) {
            ImageUtilities.disposePlanarImageChain((PlanarImage) raster.getRenderedImage());
            raster.dispose(true);
        }
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
