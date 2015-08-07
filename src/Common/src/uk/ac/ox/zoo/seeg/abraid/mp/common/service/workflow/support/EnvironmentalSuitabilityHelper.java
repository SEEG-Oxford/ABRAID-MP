package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ValidationParameterCacheService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

/**
 * Helper class to find the environmental suitability of a location.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelper {
    private static final int NUMBER_OF_ADMIN_LEVELS = 3;
    private static final String NO_PIXEL_WARNING = "Admin unit %s (%s) does not appear to cover any raster pixels. " +
            "Falling back to lat/long based environmental suitability";
    private ModelRunService modelRunService;
    private RasterFilePathFactory rasterFilePathFactory;
    private ValidationParameterCacheService cacheService;

    private static final Logger LOGGER = Logger.getLogger(EnvironmentalSuitabilityHelper.class);
    private static final String ES_NOT_FOUND_NO_DATA_MESSAGE =
            "Environmental suitability at position (%f,%f) is not defined (value \"no data\")";
    private static final String ES_NOT_FOUND_OUTSIDE_AREA_MESSAGE =
            "Environmental suitability at position (%f,%f) is outside of the raster area";
    private static final String READING_RASTER_FILE_MESSAGE =
            "Reading raster file %s for environmental suitability calculation";

    private static final int RASTER_NO_DATA_VALUE = -9999;

    public EnvironmentalSuitabilityHelper(ModelRunService modelRunService,
                                          RasterFilePathFactory rasterFilePathFactory,
                                          ValidationParameterCacheService cacheService) {
        this.modelRunService = modelRunService;
        this.rasterFilePathFactory = rasterFilePathFactory;
        this.cacheService = cacheService;
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
     * Gets the admin unit level rasters for the three admin unit levels.
     * Note: The rasters returned by the method must be disposed using RasterUtils.disposeRasters when no longer in use.
     * @return The admin unit rasters.
     */
    public GridCoverage2D[] getAdminRasters() {
        GridCoverage2D[] rasters = new GridCoverage2D[NUMBER_OF_ADMIN_LEVELS];
        loadSingleAdminRaster(0, rasters);
        loadSingleAdminRaster(1, rasters);
        loadSingleAdminRaster(2, rasters);
        return rasters;
    }

    /**
     * Gets the admin unit level raster for a single level (at the array index for the level).
     * Note: The rasters returned by the method must be disposed using RasterUtils.disposeRasters when no longer in use.
     * @param precision The precision of the admin unit level to load.
     * @return The admin unit raster.
     */
    public GridCoverage2D[] getSingleAdminRaster(LocationPrecision precision) {
        GridCoverage2D[] rasters = new GridCoverage2D[NUMBER_OF_ADMIN_LEVELS];
        if (precision != LocationPrecision.PRECISE) {
            loadSingleAdminRaster(precision.getModelValue(), rasters);
        }
        return rasters;
    }

    private void loadSingleAdminRaster(int level, GridCoverage2D[] set) {
        File rasterFile = rasterFilePathFactory.getAdminRaster(level);
        try {
            set[level] = RasterUtils.loadRaster(rasterFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds the environmental suitability of the given occurrence, using the specified rasters.
     * @param occurrence The occurrence.
     * @param suitabilityRaster The environmental suitability raster for the occurrences disease group.
     * @param adminRasters A set of admin unit level rasters.
     * @return The environmental suitability of the occurrence according to the raster, or null if not found.
     */
    public Double findEnvironmentalSuitability(
            DiseaseOccurrence occurrence, GridCoverage2D suitabilityRaster, GridCoverage2D[] adminRasters) {
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        Integer locationId = occurrence.getLocation().getId();

        Double suitability = cacheService.getEnvironmentalSuitabilityFromCache(diseaseGroupId, locationId);
        if (suitability != null) {
            return suitability;
        }

        suitability = calculateEnvironmentalSuitability(occurrence, suitabilityRaster, adminRasters);

        if (suitability != null) {
            cacheService.saveEnvironmentalSuitabilityCacheEntry(diseaseGroupId, locationId, suitability);
        }
        return suitability;
    }

    private Double calculateEnvironmentalSuitability(DiseaseOccurrence occurrence,
                                                     GridCoverage2D suitabilityRaster, GridCoverage2D[] adminRasters) {
        Location location = occurrence.getLocation();
        LocationPrecision precision = location.getPrecision();
        if (precision == LocationPrecision.PRECISE) {
            return getPreciseES(location, suitabilityRaster);
        } else {
            int gaul = getLocationGaulCode(location, precision);
            GridCoverage2D adminLayerRaster = adminRasters[precision.getModelValue()];
            Double averageES = getAverageES(gaul, suitabilityRaster, adminLayerRaster);
            if (averageES == null) {
                LOGGER.warn(String.format(NO_PIXEL_WARNING, gaul, precision));
                return getPreciseES(location, suitabilityRaster);
            }
            return averageES;
        }
    }

    private Integer getLocationGaulCode(Location location, LocationPrecision precision) {
        return (precision == LocationPrecision.COUNTRY) ?
                location.getCountryGaulCode() :
                location.getAdminUnitQCGaulCode();
    }

    private Double getAverageES(int gaul, GridCoverage2D suitabilityRaster, GridCoverage2D adminRaster) {
        Raster suitabilityData = suitabilityRaster.getRenderedImage().getData();
        Raster adminData = adminRaster.getRenderedImage().getData();
        double sum = 0;
        int count = 0;
        for (int i = 0; i < adminData.getWidth(); i++) {
            for (int j = 0; j < adminData.getHeight(); j++) {
                int adminValue = adminData.getSample(i, j, 0);
                if (adminValue == gaul) {
                    double rasterValue = suitabilityData.getSampleDouble(i, j, 0);
                    if (rasterValue != RasterUtils.NO_DATA_VALUE) {
                        count++;
                        sum += rasterValue;
                    }
                }
            }
        }
        return (count != 0) ? (sum / count) : null;
    }

    private Double getPreciseES(Location location, GridCoverage2D raster) {
        Double result = null;

        if (raster != null) {
            Point point = location.getGeom();
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
