package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import java.io.File;
import java.io.IOException;

/**
 * Helper class to find the environmental suitability of a location.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelper {
    private ModelRunService modelRunService;
    private RasterFilePathFactory rasterFilePathFactory;

    private static final Logger LOGGER = Logger.getLogger(EnvironmentalSuitabilityHelper.class);
    private static final String ES_NOT_FOUND_NO_DATA_MESSAGE =
            "Environmental suitability at position (%f,%f) is not defined (value \"no data\")";
    private static final String ES_NOT_FOUND_OUTSIDE_AREA_MESSAGE =
            "Environmental suitability at position (%f,%f) is outside of the raster area";
    private static final String READING_RASTER_FILE_MESSAGE =
            "Reading raster file %s for environmental suitability calculation";
    private static final String CANNOT_FIND_FILE_MESSAGE = "Cannot find raster file \"%s\"";

    private static final int RASTER_NO_DATA_VALUE = -9999;
    private static final Hints RASTER_READ_HINTS =
            new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, GeometryUtils.WGS_84_CRS);

    public EnvironmentalSuitabilityHelper(ModelRunService modelRunService,
                                          RasterFilePathFactory rasterFilePathFactory) {
        this.modelRunService = modelRunService;
        this.rasterFilePathFactory = rasterFilePathFactory;
    }

    /**
     * Gets the latest mean prediction raster for the disease group. This can then be used to find the environmental
     * suitability of points. Use this routine in conjunction with findEnvironmentalSuitability(occurrence, raster)
     * when you have several occurrences in the same disease group.
     * @param diseaseGroup The disease group.
     * @return The mean prediction raster returned by the most recent completed model run for this disease group,
     * or null if no such raster exists.
     */
    public GridCoverage2D getLatestMeanPredictionRaster(DiseaseGroup diseaseGroup) {
        ModelRun modelRun = modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroup.getId());
        if (modelRun != null) {
            File rasterFile = rasterFilePathFactory.getFullMeanPredictionRasterFile(modelRun);
            return readRasterFile(rasterFile);
        }
        return null;
    }

    /**
     * Finds the environmental suitability of the given occurrence, using the specified raster.
     * NODATA values in the raster are returned as null.
     * @param occurrence The occurrence.
     * @param raster The raster.
     * @return The environmental suitability of the occurrence according to the raster, or null if not found.
     */
    public Double findEnvironmentalSuitability(DiseaseOccurrence occurrence, GridCoverage2D raster) {
        Double result = null;

        if (raster != null) {
            Point point = occurrence.getLocation().getGeom();
            try {
                DirectPosition position = JTS.toDirectPosition(point.getCoordinate(), GeometryUtils.WGS_84_CRS);
                double[] resultArray = raster.evaluate(position, (double[]) null);
                if (resultArray[0] != RASTER_NO_DATA_VALUE) {
                    result = resultArray[0];
                } else {
                    LOGGER.debug(String.format(ES_NOT_FOUND_NO_DATA_MESSAGE, point.getX(), point.getY()));
                }
            } catch (PointOutsideCoverageException e) {
                // Ignore the exception - if the point is outside of the raster area, the result is null
                LOGGER.debug(String.format(ES_NOT_FOUND_OUTSIDE_AREA_MESSAGE, point.getX(), point.getY()));
            }
        }

        return result;
    }

    private GridCoverage2D readRasterFile(File rasterFile) {
        if (rasterFile.exists()) {
            GridCoverage2DReader reader = null;
            try {
                LOGGER.debug(String.format(READING_RASTER_FILE_MESSAGE, rasterFile.getAbsolutePath()));
                reader = new GeoTiffReader(rasterFile, RASTER_READ_HINTS);
                return reader.read(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                disposeResource(reader);
            }
        } else {
            String message = String.format(CANNOT_FIND_FILE_MESSAGE, rasterFile.getAbsolutePath());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    private void disposeResource(GridCoverage2DReader reader) {
        if (reader != null) {
            try {
                reader.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
