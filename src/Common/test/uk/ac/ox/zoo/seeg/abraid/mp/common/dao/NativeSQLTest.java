package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

/**
 * Tests the NativeSQL class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;
    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;
    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;
    @Autowired
    private ModelRunDao modelRunDao;
    @Autowired
    private NativeSQLImpl nativeSQL;

    // Parameters taken from the test raster files
    private static final String SMALL_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_small_int.asc";
    private static final String LARGE_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.asc";
    private static final double LARGE_RASTER_COLUMNS = 720;
    private static final double LARGE_RASTER_ROWS = 240;
    private static final double LARGE_RASTER_XLLCORNER = -180;
    private static final double LARGE_RASTER_YLLCORNER = -60;
    private static final double LARGE_RASTER_CELLSIZE = 0.5;

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true, null);
        assertThat(gaulCode).isEqualTo(826);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitGlobalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitGlobalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true, '0');
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false, null);
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void findAdminUnitTropicalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false, '0');
        // And it is assigned GAUL code 825 (Canada) which is an admin0
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void updateAndReloadMeanPredictionRasterForModelRun() throws Exception {
        updateAndReloadRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, SMALL_RASTER_FILENAME);
    }

    @Test
    public void updateAndReloadPredictionUncertaintyRasterForModelRun() throws Exception {
        updateAndReloadRasterForModelRun(NativeSQLConstants.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME, SMALL_RASTER_FILENAME);
    }

    @Test
    public void updateAggregatedDiseaseExtentForNewTropicalExtent() {
        // Arrange - set the disease extent of admin units that have test geometries
        int diseaseGroupId = 87;
        List<AdminUnitDiseaseExtentClass> dengueDiseaseExtent =
                adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 153, DiseaseExtentClass.ABSENCE);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 179, DiseaseExtentClass.POSSIBLE_PRESENCE);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 825, DiseaseExtentClass.PRESENCE);
        flushAndClear();

        // Arrange - find the number of polygons in the non-aggregated disease extent (i.e. that in
        // admin_unit_disease_extent_class)
        int expectedNumGeoms = getNumberOfPolygonsInAdminUnitDiseaseExtentClasses(diseaseGroupId, false);

        // Act
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, false);

        // Assert - Check that number of polygons in the aggregated disease extent is as expected
        int actualNumGeoms = getNumberOfPolygonsInDiseaseExtent(diseaseGroupId);
        assertThat(actualNumGeoms).isEqualTo(expectedNumGeoms);
    }

    @Test
    public void updateAggregatedDiseaseExtentForExistingGlobalExtent() {
        // Arrange - insert and calculate an existing extent, for a global disease (cholera)
        int diseaseGroupId = 64;
        insertAdminUnitDiseaseExtentClass(153, 64, DiseaseExtentClass.ABSENCE);
        insertAdminUnitDiseaseExtentClass(179, 64, DiseaseExtentClass.POSSIBLE_PRESENCE);
        insertAdminUnitDiseaseExtentClass(826, 64, DiseaseExtentClass.PRESENCE);
        flushAndClear();
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, true);
        int oldNumGeoms = getNumberOfPolygonsInAdminUnitDiseaseExtentClasses(diseaseGroupId, true);

        // Arrange - update the extent and find new number of polygons
        List<AdminUnitDiseaseExtentClass> dengueDiseaseExtent =
                adminUnitDiseaseExtentClassDao.getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 179, DiseaseExtentClass.ABSENCE);
        flushAndClear();
        int expectedNewNumGeoms = getNumberOfPolygonsInAdminUnitDiseaseExtentClasses(diseaseGroupId, true);
        assertThat(oldNumGeoms).isNotEqualTo(expectedNewNumGeoms);

        // Act
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, true);

        // Assert - Check that number of polygons in the aggregated disease extent is as expected
        int actualNewNumGeoms = getNumberOfPolygonsInDiseaseExtent(diseaseGroupId);
        assertThat(actualNewNumGeoms).isEqualTo(expectedNewNumGeoms);
    }

    private int getNumberOfPolygonsInAdminUnitDiseaseExtentClasses(int diseaseGroupId, boolean isGlobal) {
        String globalOrTropical = isGlobal ? "global" : "tropical";
        String expectedNumGeomsQuery =
                "SELECT SUM(ST_NumGeometries(geom)) FROM admin_unit_%1$s WHERE gaul_code in " +
                        "(SELECT %1$s_gaul_code FROM admin_unit_disease_extent_class WHERE disease_group_id = " +
                        diseaseGroupId + " AND disease_extent_class IN ('POSSIBLE_PRESENCE', 'PRESENCE'))";
        String formattedQuery = String.format(expectedNumGeomsQuery, globalOrTropical);
        return ((BigInteger) uniqueSQLResult(formattedQuery)).intValue();
    }

    private int getNumberOfPolygonsInDiseaseExtent(int diseaseGroupId) {
        String actualNumGeomsQuery =
                "SELECT ST_NumGeometries(geom) FROM disease_extent where disease_group_id = " + diseaseGroupId;
        return (Integer) uniqueSQLResult(actualNumGeomsQuery);
    }

    @Test
    public void findEnvironmentalSuitabilityReturnsNullIfNoRelevantModelRunsForThisDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        Point point = GeometryUtils.createPoint(-170, 50);

        // Arrange - 3 irrelevant model runs
        ModelRun modelRun1 = createAndSaveModelRun("failed with a raster", diseaseGroupId, ModelRunStatus.FAILED);
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun1, LARGE_RASTER_FILENAME);
        createAndSaveModelRun("completed without a raster (for some reason)", diseaseGroupId, ModelRunStatus.COMPLETED);
        ModelRun modelRun2 = createAndSaveModelRun("different disease group", 1, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun2, LARGE_RASTER_FILENAME);

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
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

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
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

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
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

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
        updateRasterForModelRun(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME, modelRun, LARGE_RASTER_FILENAME);

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
        Assert.assertThat(new String(actualGDALRaster), equalToIgnoringWhiteSpace(new String(expectedGDALRaster)));
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

    private void insertAdminUnitDiseaseExtentClass(int globalGaulCode, int diseaseGroupId, String extentClassName) {
        AdminUnitDiseaseExtentClass extentClass = new AdminUnitDiseaseExtentClass();
        extentClass.setAdminUnitGlobal(adminUnitGlobalDao.getByGaulCode(globalGaulCode));
        extentClass.setDiseaseGroup(diseaseGroupDao.getById(diseaseGroupId));
        extentClass.setDiseaseExtentClass(diseaseExtentClassDao.getByName(extentClassName));
        extentClass.setOccurrenceCount(0);
        adminUnitDiseaseExtentClassDao.save(extentClass);
    }

    private void updateAdminUnitDiseaseExtentClass(List<AdminUnitDiseaseExtentClass> dengueDiseaseExtent,
                                                   int gaulCode, String extentClassName) {
        AdminUnitDiseaseExtentClass extentClass = selectUnique(dengueDiseaseExtent,
                having(on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode(),
                        IsEqual.equalTo(gaulCode)));
        extentClass.setDiseaseExtentClass(diseaseExtentClassDao.getByName(extentClassName));
        adminUnitDiseaseExtentClassDao.save(extentClass);
    }
}
