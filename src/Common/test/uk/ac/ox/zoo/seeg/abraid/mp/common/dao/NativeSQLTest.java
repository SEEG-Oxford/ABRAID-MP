package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.hamcrest.core.IsEqual.equalTo;

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
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true);
        assertThat(gaulCode).isEqualTo(826);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitGlobalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false);
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void findAdminUnitTropicalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false);
        assertThat(gaulCode).isNull();
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
        updateExtentForTropicalDisease(diseaseGroupId);

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
        insertExtentForGlobalDisease(diseaseGroupId);
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

    @Test
    public void findDistanceOutsideDiseaseExtentWhenLocationIsOnVertexReturnsZero() {
        findDistanceOutsideDiseaseExtent(20, 20, 20, 20, GeometryUtils.createMultiPolygon(getSquare()));
    }

    @Test
    public void findDistanceOutsideDiseaseExtentWhenLocationIsInsideReturnsZero() {
        findDistanceOutsideDiseaseExtent(19, 18, 19, 18, GeometryUtils.createMultiPolygon(getSquare()));
    }

    @Test
    public void findDistanceOutsideDiseaseExtentWhenLocationIsOutsideReturnsDistanceToVertex() {
        findDistanceOutsideDiseaseExtent(26, 24, 20, 20, GeometryUtils.createMultiPolygon(getSquare()));
    }

    @Test
    public void findDistanceOutsideDiseaseExtentWhenLocationIsOutsideReturnsDistanceToClosestInterpolatedPoint() {
        findDistanceOutsideDiseaseExtent(25, 11, 20, 11, GeometryUtils.createMultiPolygon(getSquare()));
    }

    @Test
    public void findDistanceOutsideDiseaseExtentWhenLocationIsFarOutsideReturnsDistanceToClosestInterpolatedPoint() {
        findDistanceOutsideDiseaseExtent(15, -89.9, 15, 10, GeometryUtils.createMultiPolygon(getSquare()));
    }

    @Test
    public void findDistanceOutsideDiseaseExtentWithMultiplePolygons() {
        findDistanceOutsideDiseaseExtent(25, 11, 20, 11,
                GeometryUtils.createMultiPolygon(getSquare(), getFivePointedPolygon(), getTriangle()));
    }

    @Test
    public void findDistanceWithinDiseaseExtentOnVertexOfPossiblePresenceForTropicalDisease() {
        // Arrange
        int diseaseGroupId = 87;
        updateExtentForTropicalDisease(diseaseGroupId);
        Point point = GeometryUtils.createPoint(172, -42);
        double expectedDistance = -300; // Nominal distance for possible presence

        // Act
        double actualDistance = nativeSQL.findDistanceWithinDiseaseExtent(diseaseGroupId, false, point);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    public void findDistanceWithinDiseaseExtentInsidePresenceForTropicalDisease() {
        // Arrange
        int diseaseGroupId = 87;
        updateExtentForTropicalDisease(diseaseGroupId);
        Point point = GeometryUtils.createPoint(-120.5, 50.5);
        double expectedDistance = -1000; // Nominal distance for presence

        // Act
        double actualDistance = nativeSQL.findDistanceWithinDiseaseExtent(diseaseGroupId, false, point);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    public void findDistanceWithinDiseaseExtentInsidePresenceForGlobalDisease() {
        // Arrange
        int diseaseGroupId = 64;
        insertExtentForGlobalDisease(diseaseGroupId);
        Point point = GeometryUtils.createPoint(-124.1, 54.8);
        double expectedDistance = -1000; // Nominal distance for presence

        // Act
        double actualDistance = nativeSQL.findDistanceWithinDiseaseExtent(diseaseGroupId, true, point);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    public void findDistanceWithinDiseaseExtentReturnsNullIfOutsideDiseaseExtent() {
        // Arrange
        int diseaseGroupId = 64;
        insertExtentForGlobalDisease(diseaseGroupId);
        Point point = GeometryUtils.createPoint(-1, -1);

        // Act
        Double actualDistance = nativeSQL.findDistanceWithinDiseaseExtent(diseaseGroupId, true, point);

        // Assert
        assertThat(actualDistance).isNull();
    }

    private void findDistanceOutsideDiseaseExtent(double locationX, double locationY,
                                                  double expectedClosestX, double expectedClosestY,
                                                  MultiPolygon diseaseExtent) {
        // Arrange
        int diseaseGroupId = 87;
        insertDiseaseExtent(diseaseGroupId, diseaseExtent);
        Point point = GeometryUtils.createPoint(locationX, locationY);
        Point expectedClosestPoint = GeometryUtils.createPoint(expectedClosestX, expectedClosestY);
        double expectedDistance = GeometryUtils.findOrthodromicDistance(point, expectedClosestPoint);

        // Act
        double actualDistance = nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, point);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance, offset(0.00005));
    }

    private Point createOffsetPoint(double x, double y) {
        double offsetForRounding = 0.00005;
        return GeometryUtils.createPoint(x + offsetForRounding, y + offsetForRounding);
    }

    private void updateAndReloadRasterForModelRun(String rasterColumnName, String filename) throws Exception {
        // Arrange - create a model run
        ModelRun modelRun = createAndSaveModelRun("test name", 87, ModelRunStatus.COMPLETED);

        // Act - update model run with loaded raster
        byte[] expectedGDALRasterResult = updateRasterForModelRun(rasterColumnName, modelRun, filename);

        // Assert - load mean prediction raster from model run and compare for equality (ignoring whitespace)
        byte[] actualGDALRasterResult = nativeSQL.getRasterForModelRun(modelRun.getId(), rasterColumnName);
        assertThat(new String(actualGDALRasterResult)).isEqualTo(new String(expectedGDALRasterResult));
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

    private void insertExtentForGlobalDisease(int diseaseGroupId) {
        insertAdminUnitDiseaseExtentClass(153, diseaseGroupId, DiseaseExtentClass.ABSENCE);
        insertAdminUnitDiseaseExtentClass(179, diseaseGroupId, DiseaseExtentClass.POSSIBLE_PRESENCE);
        insertAdminUnitDiseaseExtentClass(826, diseaseGroupId, DiseaseExtentClass.PRESENCE);
        flushAndClear();
        insertDiseaseExtentRow(diseaseGroupId);
    }

    private void updateExtentForTropicalDisease(int diseaseGroupId) {
        List<AdminUnitDiseaseExtentClass> dengueDiseaseExtent =
                adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 153, DiseaseExtentClass.ABSENCE);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 179, DiseaseExtentClass.POSSIBLE_PRESENCE);
        updateAdminUnitDiseaseExtentClass(dengueDiseaseExtent, 825, DiseaseExtentClass.PRESENCE);
        flushAndClear();
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

    private void insertAdminUnitDiseaseExtentClass(int globalGaulCode, int diseaseGroupId, String extentClassName) {
        AdminUnitDiseaseExtentClass extentClass = new AdminUnitDiseaseExtentClass();
        extentClass.setAdminUnitGlobal(adminUnitGlobalDao.getByGaulCode(globalGaulCode));
        extentClass.setDiseaseGroup(diseaseGroupDao.getById(diseaseGroupId));
        extentClass.setDiseaseExtentClass(diseaseExtentClassDao.getByName(extentClassName));
        extentClass.setOccurrenceCount(0);
        extentClass.setClassChangedDate(DateTime.now());
        adminUnitDiseaseExtentClassDao.save(extentClass);
    }

    private void updateAdminUnitDiseaseExtentClass(List<AdminUnitDiseaseExtentClass> dengueDiseaseExtent,
                                                   int gaulCode, String extentClassName) {
        AdminUnitDiseaseExtentClass extentClass = selectUnique(dengueDiseaseExtent,
                having(on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode(),
                        equalTo(gaulCode)));
        extentClass.setDiseaseExtentClass(diseaseExtentClassDao.getByName(extentClassName));
        adminUnitDiseaseExtentClassDao.save(extentClass);
    }

    private Polygon getTriangle() {
        return GeometryUtils.createPolygon(1, 1, 3, 2, 2, 3, 1, 1);
    }

    private Polygon getSquare() {
        return GeometryUtils.createPolygon(10, 10, 10, 20, 20, 20, 20, 10, 10, 10);
    }

    private Polygon getFivePointedPolygon() {
        return GeometryUtils.createPolygon(3, 4, 5, 11, 12, 8, 9, 5, 5, 6, 3, 4);
    }

    private void insertDiseaseExtentRow(int diseaseGroupId) {
        String queryString = "INSERT INTO disease_extent (disease_group_id) VALUES (" + diseaseGroupId + ")";
        sessionFactory.getCurrentSession().createSQLQuery(queryString).executeUpdate();
    }

    private void insertDiseaseExtent(int diseaseGroupId, Geometry geom) {
        executeSQLUpdate("UPDATE disease_extent set geom=:geom where disease_group_id=:diseaseGroupId",
                "diseaseGroupId", diseaseGroupId, "geom", geom);
    }
}
