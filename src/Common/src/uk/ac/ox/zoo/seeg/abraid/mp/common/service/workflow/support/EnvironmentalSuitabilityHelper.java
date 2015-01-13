package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.RasterUtils;
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
     * Note: The raster returned by the method must be disposed using RasterUtils.disposeRaster when no longer in use.
     * @param diseaseGroup The disease group.
     * @return The mean prediction raster returned by the most recent completed model run for this disease group,
     * or null if no such raster exists.
     */
    public GridCoverage2D getLatestMeanPredictionRaster(DiseaseGroup diseaseGroup) {
        ModelRun modelRun = modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroup.getId());
        if (modelRun != null) {
            File rasterFile = rasterFilePathFactory.getFullMeanPredictionRasterFile(modelRun);

            LOGGER.debug(String.format(READING_RASTER_FILE_MESSAGE, rasterFile.getAbsolutePath()));
            GridCoverage2D raster = null;
            try {
                raster = RasterUtils.loadRaster(rasterFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

           return raster;
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
}
