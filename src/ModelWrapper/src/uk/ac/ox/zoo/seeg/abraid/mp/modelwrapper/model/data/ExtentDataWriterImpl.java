package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.log4j.Logger;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.geometry.Envelope2D;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Provides a mechanism for writing model input extent data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class ExtentDataWriterImpl implements ExtentDataWriter {
    private static final Logger LOGGER = Logger.getLogger(ExtentDataWriterImpl.class);
    private static final String LOG_FAILED_TO_READ_SOURCE_RASTER =
            "Failed to read gaul code source raster: %s.";
    private static final String LOG_FAILED_TO_SAVE_TRANSFORMED_RASTER =
            "Failed to save transformed weighting raster: %s.";
    private static final String LOG_SAVING_TRANSFORMED_RASTER =
            "Saving transformed weighting raster: %s";
    private static final String LOG_LOADING_SOURCE_RASTER =
            "Loading gaul code source raster: %s.";
    private static final Object LOG_TRANSFORMING_RASTER_DATA =
            "Transforming gaul code raster to weightings raster.";
    private static final int RASTER_NO_DATA_VALUE = -9999;
    private static final AbstractGridFormat GEOTIFF_FORMAT = new GeoTiffFormat();
    private static final float GEOTIFF_COMPRESSION_LEVEL = 0.9F;
    private static final String GEOTIFF_COMPRESSION_TYPE = "Deflate";

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param sourceRasterFile The path of the gaul code raster to reclassify.
     * @param targetFile The file to be created.
     * @throws java.io.IOException If the data could not be written.
     */
    @Override
    public void write(Map<Integer, Integer> extentData, File sourceRasterFile, File targetFile) throws IOException {
        GridCoverage2D sourceRaster = null;
        try {
            sourceRaster = loadRaster(sourceRasterFile);

            Envelope2D rasterExtent = sourceRaster.getGridGeometry().getEnvelope2D();
            GridSampleDimension[] rasterProperties = sourceRaster.getSampleDimensions();
            WritableRaster rasterData = (WritableRaster) sourceRaster.getRenderedImage().getData();

            transformRaster(extentData, rasterData);

            saveRaster(targetFile, rasterData, rasterExtent, rasterProperties);
        } finally {
            dispose(sourceRaster);
        }
    }

    private GridCoverage2D loadRaster(File location) throws IOException {
        LOGGER.info(String.format(LOG_LOADING_SOURCE_RASTER, location.toString()));
        GridCoverage2DReader reader = null;
        try {
            reader = new GeoTiffReader(location);
            return reader.read(null);
        } catch (Exception e) {
            final String message = String.format(LOG_FAILED_TO_READ_SOURCE_RASTER, location.toString());
            LOGGER.error(message, e);
            throw new IOException(message, e);
        } finally {
            dispose(reader);
        }
    }

    private void transformRaster(Map<Integer, Integer> transform, WritableRaster data) {
        LOGGER.info(LOG_TRANSFORMING_RASTER_DATA);

        for (int i = 0; i < data.getWidth(); i++) {
            for (int j = 0; j < data.getHeight(); j++) {
                int gaul = data.getSample(i, j, 0);
                if (transform.containsKey(gaul)) {
                    data.setSample(i, j, 0, transform.get(gaul));
                } else {
                    if (gaul != RASTER_NO_DATA_VALUE) {
                        data.setSample(i, j, 0, RASTER_NO_DATA_VALUE);
                    }
                }
            }
        }
    }

    private void saveRaster(File location, WritableRaster raster, Envelope2D extents, GridSampleDimension[] properties)
            throws IOException {
        LOGGER.info(String.format(LOG_SAVING_TRANSFORMED_RASTER, location.toString()));

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
            dispose(targetRaster);
            dispose(writer);
        }
    }

    private GeneralParameterValue[] getGeoTiffWriteParameters() {
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

    private void dispose(GridCoverage2D raster) {
        if (raster != null) {
            raster.dispose(true);
        }
    }

    private void dispose(GridCoverage2DReader reader) throws IOException {
        if (reader != null) {
            reader.dispose();
        }
    }

    private void dispose(GridCoverageWriter writer) throws IOException {
        if (writer != null) {
            writer.dispose();
        }
    }
}
