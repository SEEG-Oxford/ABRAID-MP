package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper class to find the environmental suitability of a location.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelper {
    private static final Logger LOGGER = Logger.getLogger(EnvironmentalSuitabilityHelper.class);
    private static final String LOG_COULD_NOT_DELETE_TEMP_FILE = "Could not delete temporary file \"%s\"";

    private ModelRunService modelRunService;
    private NativeSQL nativeSQL;

    public EnvironmentalSuitabilityHelper(ModelRunService modelRunService, NativeSQL nativeSQL) {
        this.modelRunService = modelRunService;
        this.nativeSQL = nativeSQL;
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
            byte[] raster = modelRunService.getMeanPredictionRasterForModelRun(modelRun.getId());
            if (raster != null && raster.length > 0) {
                return convertRaster(raster);
            }
        }
        return null;
    }

    /**
     * Finds the environmental suitability of the given occurrence, using the specified raster.
     * If the raster is null, finds the environmental suitability using a database query instead. This is much slower
     * but does not include the initial overhead (both time and memory) of needing to load the raster from the
     * database using getLatestMeanPredictionRaster.
     * @param occurrence The occurrence.
     * @param raster The raster.
     * @return The environmental suitability of the occurrence according to the raster, or null if not found.
     */
    public Double findEnvironmentalSuitability(DiseaseOccurrence occurrence, GridCoverage2D raster) {
        Double result = null;
        int diseaseGroupId = occurrence.getDiseaseGroup().getId();
        Point point = occurrence.getLocation().getGeom();

        if (raster != null) {
            try {
                // We previously loaded the raster, so find the environmental suitability directly
                DirectPosition position = JTS.toDirectPosition(point.getCoordinate(), GeometryUtils.WGS_84_CRS);
                double[] resultArray = raster.evaluate(position, (double[]) null);
                result = resultArray[0];
            } catch (PointOutsideCoverageException e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Ignore the exception - if the point is outside of the raster area, the result is null
            }
        } else {
            // We have not previously loaded the raster, so find the environmental suitability using a database query
            result = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);
        }

        return result;
    }

    private GridCoverage2D convertRaster(byte[] raster) {
        File rasterFile = null;

        try {
            // GeoTiffReader cannot read directly from a byte array, even if wrapped in a ByteArrayInputStream
            // (see http://jira.codehaus.org/browse/GEOT-3318). So firstly dump the byte array to a temporary file.
            rasterFile = dumpRasterToTemporaryFile(raster);
            // Allow the byte array to be garbage collected if necessary
            raster = null;
            // Read and return the raster
            GridCoverage2DReader reader = new GeoTiffReader(rasterFile);
            return reader.read(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            deleteTemporaryFile(rasterFile);
        }
    }

    private File dumpRasterToTemporaryFile(byte[] raster) throws IOException {
        File rasterFile = File.createTempFile("raster", null);
        try (FileOutputStream outputStream = new FileOutputStream(rasterFile)) {
            outputStream.write(raster);
        }
        return rasterFile;
    }

    private void deleteTemporaryFile(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                // Could not delete temporary file - just log the error
                LOGGER.error(String.format(LOG_COULD_NOT_DELETE_TEMP_FILE, file.getAbsolutePath()));
            }
        }
    }
}
