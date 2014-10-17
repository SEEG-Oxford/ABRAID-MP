package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFileBuilder;

import java.io.File;

/**
 * Helper class to find the environmental suitability of a location.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelper {
    private ModelRunService modelRunService;
    private RasterFileBuilder rasterFileBuilder;

    private static final Logger LOGGER = Logger.getLogger(EnvironmentalSuitabilityHelper.class);
    private static final int RASTER_NO_DATA_VALUE = -9999;
    private static final String CANNOT_FIND_FILE_MESSAGE = "Cannot find raster file \"%s\"";

    public EnvironmentalSuitabilityHelper(ModelRunService modelRunService, RasterFileBuilder rasterFileBuilder) {
        this.modelRunService = modelRunService;
        this.rasterFileBuilder = rasterFileBuilder;
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
        ModelRun modelRun = modelRunService.getLastCompletedModelRun(diseaseGroup.getId());
        if (modelRun != null) {
            File rasterFile = rasterFileBuilder.getMeanPredictionRasterFile(modelRun);
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
                }
            } catch (PointOutsideCoverageException e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Ignore the exception - if the point is outside of the raster area, the result is null
            }
        }

        return result;
    }

    private GridCoverage2D readRasterFile(File rasterFile) {
        if (rasterFile.exists()) {
            try {
                GridCoverage2DReader reader = new GeoTiffReader(rasterFile);
                return reader.read(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            String message = String.format(CANNOT_FIND_FILE_MESSAGE, rasterFile.getAbsolutePath());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
