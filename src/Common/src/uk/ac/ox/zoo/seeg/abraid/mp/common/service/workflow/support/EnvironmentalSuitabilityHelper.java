package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ValidationParameterCacheService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterTransformation;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Helper class to find the environmental suitability of a location.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelper {
    private static final int NUMBER_OF_ADMIN_LEVELS = 3;
    private static final String COUNTRY_NO_PIXELS_WARNING =
            "The specified country does not appear to cover any raster pixels.";
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
    private static final String TEMP_FILE_NOT_REMOVED =
            "An intermediary file could not be removed (%s)";

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
                //System.out.println("av");
                int adminValue = adminData.getSample(i, j, 0);
                if (adminValue == gaul) {
                    //System.out.println("sd");
                    double rasterValue = suitabilityData.getSampleDouble(i, j, 0);
                    if (rasterValue != RasterUtils.NO_DATA_VALUE) {
                        //System.out.println("add");
                        count++;
                        sum += rasterValue;
                    }
                }
            }
        }
        return (count != 0) ? (sum / count) : null;
    }

    private Double getPreciseES(Location location, GridCoverage2D raster) {
        Point point = location.getGeom();

        return getPointES(raster, point);
    }

    private Double getPointES(GridCoverage2D raster, Point point) {
        Double result = null;
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
        return result;
    }

    /**
     * Finds the environmental suitability of the given point, using the specified rasters.
     * @param point The point.
     * @param rasterFile The environmental suitability raster.
     * @return The environmental suitability of the point according to the raster, or null if not found.
     * @throws IOException If a value can not be extracted.
     */
    public Double findPointEnvironmentalSuitability(File rasterFile, Point point) throws IOException {
        Double result = null;
        if (rasterFile != null) {
            GridCoverage2D raster = null;
            try {
                raster = RasterUtils.loadRaster(rasterFile);
                result = getPointES(raster, point);
            } finally {
                RasterUtils.disposeRaster(raster);
            }
        }

        return result;
    }

    /**
     * Create a temporary file containing a cropped version of the specified suitability raster, limited to a masked set
     * of pixels associated with the given gaul code.
     * @param gaulCode The gaul code.
     * @param adminRaster The admin layer raster.
     * @param suitabilityRaster The suitability raster.
     * @return A temporary file containing the cropped raster.
     * @throws IOException If a cropped raster can not be extracted.
     */
    public File createCroppedEnvironmentalSuitabilityRaster(final int gaulCode,
                final File adminRaster, final File suitabilityRaster) throws IOException {
        File maskedFile = File.createTempFile("masked", "tif");
        File croppedFile = File.createTempFile("cropped", "tif");

        // Mask the raster and record pixel bounds
        final Rectangle cropRectangle = new Rectangle();
        RasterUtils.transformRaster(suitabilityRaster, maskedFile, new File[]{adminRaster}, new RasterTransformation() {
            @Override
            public void transform(WritableRaster raster, Raster[] referenceRasters) throws IOException {
                Boolean containedAtLeastOnePixel = false;
                int minI = Integer.MAX_VALUE;
                int minJ = Integer.MAX_VALUE;
                int maxI = Integer.MIN_VALUE;
                int maxJ = Integer.MIN_VALUE;
                for (int i = 0; i < raster.getWidth(); i++) {
                    for (int j = 0; j < raster.getHeight(); j++) {
                        int rasterValue = raster.getSample(i, j, 0);
                        if (rasterValue != RasterUtils.NO_DATA_VALUE) {
                            int adminValue = referenceRasters[0].getSample(i, j, 0);
                            if (adminValue != gaulCode) {
                                raster.setSample(i, j, 0, RasterUtils.NO_DATA_VALUE);
                            } else {
                                containedAtLeastOnePixel = true;
                                minI = (minI > i) ? i : minI;
                                minJ = (minJ > j) ? j : minJ;
                                maxI = (maxI < i) ? i : maxI;
                                maxJ = (maxJ < j) ? j : maxJ;
                            }
                        }
                    }
                }
                if (containedAtLeastOnePixel) {
                    // Pad
                    minI = Math.max(minI - 1, 0);
                    minJ = Math.max(minJ - 1, 0);
                    maxI = Math.min(maxI + 1, raster.getWidth());
                    maxJ = Math.min(maxJ + 1, raster.getHeight());
                    cropRectangle.setBounds(minI, minJ, maxI - minI, maxJ - minJ);
                } else {
                    throw new IOException(COUNTRY_NO_PIXELS_WARNING);
                }
            }
        });

        // Cut to masked pixel bounds
        GridCoverage2D maskedRaster = null;
        GridCoverage2D croppedRaster = null;
        try {
            maskedRaster = RasterUtils.loadRaster(maskedFile);
            Envelope2D cropEnvelope = null;
            try {
                cropEnvelope = maskedRaster.getGridGeometry().gridToWorld(new GridEnvelope2D(cropRectangle));
            } catch (TransformException e) {
                throw new IOException(e);
            }
            croppedRaster = (GridCoverage2D) (new Operations(null)).crop(maskedRaster, cropEnvelope);

            // Save as compressed geotiff
            final GeoTiffFormat format = new GeoTiffFormat();
            final GridCoverageWriter writer = format.getWriter(croppedFile);
            final GeoTiffWriteParams writeParams = new GeoTiffWriteParams();
            writeParams.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
            writeParams.setCompressionType("Deflate");
            writeParams.setCompressionQuality(1); // this equates to "level 9" (level = (int)(1 + 8*quality))
            // https://github.com/geosolutions-it/imageio-ext/blob/master/plugin/tiff/src
            // /main/java/it/geosolutions/imageioimpl/plugins/tiff/TIFFDeflater.java#L105
            final ParameterValueGroup params = format.getWriteParameters();
            params.parameter(
                    AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString())
                    .setValue(writeParams);
            writer.write(croppedRaster, params.values()
                    .toArray(new GeneralParameterValue[1]));

            // Clean up
        } finally {
            RasterUtils.disposeRaster(maskedRaster);
            RasterUtils.disposeRaster(croppedRaster);
        }

        if (maskedFile.exists() && !maskedFile.delete()) {
            LOGGER.warn(String.format(TEMP_FILE_NOT_REMOVED, maskedFile.getAbsolutePath()));
        }

        return croppedFile;
    }
}
