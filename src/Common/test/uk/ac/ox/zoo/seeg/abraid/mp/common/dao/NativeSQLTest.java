package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;

/**
 * Tests the NativeSQL class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;
    @Autowired
    private NativeSQLImpl nativeSQL;

    // Parameters taken from the test raster files
    private static final String SMALL_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_small_int.tif";
    private static final String LARGE_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.tif";
    private static final double LARGE_RASTER_COLUMNS = 720;
    private static final double LARGE_RASTER_ROWS = 240;
    private static final double LARGE_RASTER_XLLCORNER = -180;
    private static final double LARGE_RASTER_YLLCORNER = -60;
    private static final double LARGE_RASTER_CELLSIZE = 0.5;

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(826);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitGlobalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitGlobalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, '0');
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void findAdminUnitTropicalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, '0');
        // And it is assigned GAUL code 825 (Canada) which is an admin0
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void updateAndReloadMeanPredictionRasterForModelRun() throws Exception {
        updateAndReloadRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, SMALL_RASTER_FILENAME);
    }

    @Test
    public void updateAndReloadPredictionUncertaintyRasterForModelRun() throws Exception {
        updateAndReloadRasterForModelRun(NativeSQLImpl.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME, SMALL_RASTER_FILENAME);
    }

    @Test
    public void findEnvironmentalSuitabilityReturnsNullIfNoRelevantModelRunsForThisDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        Point point = GeometryUtils.createPoint(-170, 50);

        // Arrange - 3 irrelevant model runs
        ModelRun modelRun1 = createAndSaveModelRun("failed with a raster", diseaseGroupId, ModelRunStatus.FAILED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun1, LARGE_RASTER_FILENAME);
        createAndSaveModelRun("completed without a raster (for some reason)", diseaseGroupId, ModelRunStatus.COMPLETED);
        ModelRun modelRun2 = createAndSaveModelRun("different disease group", 1, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun2, LARGE_RASTER_FILENAME);

        // Act
        Double suitability = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);

        // Assert
        assertThat(suitability).isNull();
    }

    @Test
    public void findEnvironmentalSuitabilityLowerLeftCorner() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        Point point = createOffsetPoint(LARGE_RASTER_XLLCORNER, LARGE_RASTER_YLLCORNER);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroupId, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

        // Act
        Double suitability = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);

        // Assert
        assertThat(suitability).isEqualTo(0.89, offset(0.0000005));
    }

    @Test
    public void findEnvironmentalSuitabilityUpperRightCorner() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        double upperRightCornerX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_COLUMNS - 1) * LARGE_RASTER_CELLSIZE;
        double upperRightCornerY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 1) * LARGE_RASTER_CELLSIZE;
        Point point = createOffsetPoint(upperRightCornerX, upperRightCornerY);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroupId, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

        // Act
        Double suitability = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);

        // Assert
        assertThat(suitability).isEqualTo(0.79, offset(0.0000005));
    }

    @Test
    public void findEnvironmentalSuitabilityInterpolated() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        double lowerLeftCornerSlightlyShiftedX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        double lowerLeftCornerSlightlyShiftedY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        Point point = createOffsetPoint(lowerLeftCornerSlightlyShiftedX, lowerLeftCornerSlightlyShiftedY);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroupId, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

        // Act
        Double suitability = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);

        // Assert
        assertThat(suitability).isEqualTo(0.89, offset(0.0000005));
    }

    @Test
    public void findEnvironmentalSuitabilityOutOfRasterRange() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        double oneCellBeyondUpperRightCornerX = LARGE_RASTER_XLLCORNER + LARGE_RASTER_COLUMNS * LARGE_RASTER_CELLSIZE;
        double oneCellBeyondUpperRightCornerY = LARGE_RASTER_YLLCORNER + LARGE_RASTER_ROWS * LARGE_RASTER_CELLSIZE;
        Point point = createOffsetPoint(oneCellBeyondUpperRightCornerX, oneCellBeyondUpperRightCornerY);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroupId, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

        // Act
        Double suitability = nativeSQL.findEnvironmentalSuitability(diseaseGroupId, point);

        // Assert
        assertThat(suitability).isNull();
    }

    private Point createOffsetPoint(double x, double y) {
        double offsetForRounding = 0.00005;
        return GeometryUtils.createPoint(x + offsetForRounding, y + offsetForRounding);
    }

    private void updateAndReloadRasterForModelRun(String rasterColumnName, String filename) throws Exception {
        // Arrange - create a model run
        ModelRun modelRun = createAndSaveModelRun("test name", 87, ModelRunStatus.COMPLETED);

        // Act - update model run with loaded raster
        byte[] actualGDALRaster = updateRasterForModelRun(rasterColumnName, modelRun, filename);

        // Assert - load mean prediction raster from model run and compare for equality (ignoring whitespace)
        byte[] expectedGDALRaster = nativeSQL.loadRasterForModelRun(modelRun.getId(), rasterColumnName);
        assertThat(new String(actualGDALRaster)).isEqualTo(new String(expectedGDALRaster));
    }

    private ModelRun createAndSaveModelRun(String name, int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, DateTime.now());
        modelRun.setStatus(status);
        modelRunDao.save(modelRun);
        return modelRun;
    }

    private byte[] updateRasterForModelRun(String rasterColumnName, ModelRun modelRun, String filename) throws Exception {
        byte[] gdalRaster = FileUtils.readFileToByteArray(new File(filename));
        nativeSQL.updateRasterForModelRun(modelRun.getId(), gdalRaster, rasterColumnName);
        return gdalRaster;
    }
}
