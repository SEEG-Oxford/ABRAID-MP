package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

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
    private CountryDao countryDao;
    @Autowired
    private NativeSQLImpl nativeSQL;
    @Autowired
    private LocationDao locationDao;

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
        Point point = GeometryUtils.createPoint(176, -38);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, true);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitThatContainsPoint(point, false);
        assertThat(gaulCode).isEqualTo(46);
    }

    @Test
    public void findAdminUnitTropicalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(177, -39);
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
    public void doesLandSeaBorderContainPointReturnsTrueIfPointOnLand() {
        Point point = GeometryUtils.createPoint(-1.43547, 52.41617);
        boolean result = nativeSQL.doesLandSeaBorderContainPoint(point);
        assertThat(result).isTrue();
    }

    @Test
    public void doesLandSeaBorderContainPointReturnsFalseIfPointNotOnLand() {
        Point point = GeometryUtils.createPoint(0, 0);
        boolean result = nativeSQL.doesLandSeaBorderContainPoint(point);
        assertThat(result).isFalse();
    }

    @Test
    public void updateAggregatedDiseaseExtentForNewTropicalExtent() {
        // Arrange - set the disease extent of admin units that have test geometries
        int diseaseGroupId = 87;
        updateExtentForTropicalDisease(diseaseGroupId);

        // Arrange - find the number of polygons in the non-aggregated disease extent (i.e. that in
        // admin_unit_disease_extent_class)
        int expectedNumGeoms = getNumberOfPolygonsInAdminUnitDiseaseExtentClasses(diseaseGroupId, false);
        int expectedNumGeomsOutside = getNumberOfPolygonsInAdminUnitDiseaseExtentClassesOutside(diseaseGroupId, false);

        // Act
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, false);

        // Assert - Check that number of polygons in the aggregated disease extent is as expected
        int actualNumGeoms = getNumberOfPolygonsInDiseaseExtent(diseaseGroupId);
        int actualNumGeomsOutside = getNumberOfPolygonsInOutsideDiseaseExtent(diseaseGroupId);
        assertThat(actualNumGeoms).isEqualTo(expectedNumGeoms);
        assertThat(actualNumGeomsOutside).isEqualTo(expectedNumGeomsOutside);
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
        int expectedNewNumGeomsOutside = getNumberOfPolygonsInAdminUnitDiseaseExtentClassesOutside(diseaseGroupId, true);
        assertThat(oldNumGeoms).isNotEqualTo(expectedNewNumGeoms);

        // Act
        nativeSQL.updateAggregatedDiseaseExtent(diseaseGroupId, true);

        // Assert - Check that number of polygons in the aggregated disease extent is as expected
        int actualNewNumGeoms = getNumberOfPolygonsInDiseaseExtent(diseaseGroupId);
        int actualNewNumGeomsOutside = getNumberOfPolygonsInOutsideDiseaseExtent(diseaseGroupId);
        assertThat(actualNewNumGeoms).isEqualTo(expectedNewNumGeoms);
        assertThat(actualNewNumGeomsOutside).isEqualTo(expectedNewNumGeomsOutside);
    }

    private void insertExtentForGlobalDisease(int diseaseGroupId) {
        insertAdminUnitDiseaseExtentClass(153, diseaseGroupId, DiseaseExtentClass.ABSENCE, DiseaseExtentClass.ABSENCE);
        insertAdminUnitDiseaseExtentClass(179, diseaseGroupId, DiseaseExtentClass.POSSIBLE_PRESENCE, DiseaseExtentClass.ABSENCE);
        insertAdminUnitDiseaseExtentClass(826, diseaseGroupId, DiseaseExtentClass.PRESENCE, DiseaseExtentClass.POSSIBLE_PRESENCE);
        flushAndClear();
    }

    private void updateExtentForTropicalDisease(int diseaseGroupId) {
        List<AdminUnitDiseaseExtentClass> diseaseExtent =
                adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        updateAdminUnitDiseaseExtentClass(diseaseExtent, 153, DiseaseExtentClass.ABSENCE);
        updateAdminUnitDiseaseExtentClass(diseaseExtent, 179, DiseaseExtentClass.POSSIBLE_PRESENCE);
        updateAdminUnitDiseaseExtentClass(diseaseExtent, 64, DiseaseExtentClass.PRESENCE);
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

    private int getNumberOfPolygonsInAdminUnitDiseaseExtentClassesOutside(int diseaseGroupId, boolean isGlobal) {
        String globalOrTropical = isGlobal ? "global" : "tropical";
        String expectedNumGeomsQuery =
                "SELECT SUM(ST_NumGeometries(geom)) FROM admin_unit_%1$s WHERE gaul_code in " +
                        "(SELECT %1$s_gaul_code FROM admin_unit_disease_extent_class WHERE disease_group_id = " +
                        diseaseGroupId + " AND disease_extent_class NOT IN ('POSSIBLE_PRESENCE', 'PRESENCE'))";
        String formattedQuery = String.format(expectedNumGeomsQuery, globalOrTropical);
        return ((BigInteger) uniqueSQLResult(formattedQuery)).intValue();
    }

    private int getNumberOfPolygonsInDiseaseExtent(int diseaseGroupId) {
        String actualNumGeomsQuery =
                "SELECT ST_NumGeometries(geom) FROM disease_extent where disease_group_id = " + diseaseGroupId;
        return (Integer) uniqueSQLResult(actualNumGeomsQuery);
    }

    private int getNumberOfPolygonsInOutsideDiseaseExtent(int diseaseGroupId) {
        String actualNumGeomsQuery =
                "SELECT ST_NumGeometries(outside_geom) FROM disease_extent where disease_group_id = " + diseaseGroupId;
        return (Integer) uniqueSQLResult(actualNumGeomsQuery);
    }

    private void insertAdminUnitDiseaseExtentClass(int globalGaulCode, int diseaseGroupId, String extentClassName, String validatorExtentClassName) {
        AdminUnitDiseaseExtentClass extentClass = new AdminUnitDiseaseExtentClass();
        extentClass.setAdminUnitGlobal(adminUnitGlobalDao.getByGaulCode(globalGaulCode));
        extentClass.setDiseaseGroup(diseaseGroupDao.getById(diseaseGroupId));
        extentClass.setDiseaseExtentClass(diseaseExtentClassDao.getByName(extentClassName));
        extentClass.setValidatorDiseaseExtentClass(diseaseExtentClassDao.getByName(validatorExtentClassName));
        extentClass.setValidatorOccurrenceCount(0);
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

    @Test
    public void findDistanceToDiseaseExtentPointGlobal() {
        insertDiseaseExtent();
        int id = setupDistanceTest(LocationPrecision.PRECISE, 1d, 4d, null, true, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isEqualTo(156.75914, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isEqualTo(4460.23046, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentPointTropical() {
        insertDiseaseExtent();
        int id = setupDistanceTest(LocationPrecision.PRECISE, 1d, 4d, null, false, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, false, id)).isEqualTo(156.75914, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, false, id)).isEqualTo(4460.23046, offset(0.00005));
    }

    @Test
      public void findDistanceToDiseaseExtentAdmin1Global() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.ADMIN1, 1d, 4d, geom, true, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentAdmin1Tropical() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.ADMIN1, 1d, 4d, geom, false, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, false, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, false, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentAdmin2Global() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.ADMIN2, 1d, 4d, geom, true, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentAdmin2Tropical() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.ADMIN2, 1d, 4d, geom, false, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, false, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, false, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentCountryGlobal() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.COUNTRY, 1d, 4d, geom, true, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentCountryTropical() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        int id = setupDistanceTest(LocationPrecision.COUNTRY, 1d, 4d, geom, false, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, false, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, false, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentCountrySplitGlobal() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12), getTriangle(20, 82), getTriangle(0, 5));
        MultiPolygon subGeom1 = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        MultiPolygon subGeom2 = GeometryUtils.createMultiPolygon(getTriangle(20, 82));
        int id = setupDistanceTest(LocationPrecision.COUNTRY, 1d, 4d, geom, true, subGeom1, subGeom2);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentCountrySplitTropical() {
        insertDiseaseExtent();
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12), getTriangle(20, 82), getTriangle(0, 5));
        MultiPolygon subGeom1 = GeometryUtils.createMultiPolygon(getTriangle(0, 10), getTriangle(10, 12));
        MultiPolygon subGeom2 = GeometryUtils.createMultiPolygon(getTriangle(20, 82));
        int id = setupDistanceTest(LocationPrecision.COUNTRY, 1d, 4d, geom, false, subGeom1, subGeom2);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, false, id)).isEqualTo(891.60176, offset(0.00005));
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, false, id)).isEqualTo(3401.72083, offset(0.00005));
    }

    @Test
    public void findDistanceToDiseaseExtentWithoutExtent() {
        insertNullDiseaseExtent();
        int id = setupDistanceTest(LocationPrecision.PRECISE, 1d, 4d, null, true, null, null);
        assertThat(nativeSQL.findDistanceOutsideDiseaseExtent(87, true, id)).isNull();
        assertThat(nativeSQL.findDistanceInsideDiseaseExtent(87, true, id)).isNull();
    }

    private Polygon getTriangle(double xOffset, double yOffset) {
        return GeometryUtils.createPolygon(
                xOffset + 1, yOffset + 1,
                xOffset + 3, yOffset + 2,
                xOffset + 2, yOffset + 3,
                xOffset + 1, yOffset + 1);
    }

    private void insertDiseaseExtent() {
        MultiPolygon geom = GeometryUtils.createMultiPolygon(getTriangle(0, 0), getTriangle(10, 0), getTriangle(20, 0), getTriangle(30, 0));
        MultiPolygon outsideGeom = GeometryUtils.createMultiPolygon(getTriangle(40, 0), getTriangle(50, 0), getTriangle(60, 0), getTriangle(70, 0));
        executeSQLUpdate("UPDATE disease_extent set geom=:geom, outside_geom=:outsideGeom where disease_group_id=:diseaseGroupId",
                "diseaseGroupId", 87, "geom", geom, "outsideGeom", outsideGeom);
    }

    private void insertNullDiseaseExtent() {
        executeSQLUpdate("UPDATE disease_extent set geom=:geom, outside_geom=:outsideGeom where disease_group_id=:diseaseGroupId",
                "diseaseGroupId", 87, "geom", null, "outsideGeom", null);
    }

    private int setupDistanceTest(LocationPrecision precision, double x, double y, MultiPolygon linkedGeom, boolean isGlobal, MultiPolygon subLinkedGeom1, MultiPolygon subLinkedGeom2) {
        Location location = new Location(x, y, precision);
        if (precision == LocationPrecision.COUNTRY) {
            executeSQLUpdate("UPDATE country SET geom=:geom WHERE gaul_code=93", "geom", linkedGeom);
            location.setCountry(countryDao.getByName("Germany")); // GAUL 93 = "Germany"
            if (subLinkedGeom1 != null || subLinkedGeom2 != null) {
                executeSQLUpdate("UPDATE admin_unit_" + (isGlobal ? "global" : "tropical") + " SET geom=:geom WHERE gaul_code=27", "geom", subLinkedGeom1);
                executeSQLUpdate("UPDATE admin_unit_" + (isGlobal ? "global" : "tropical") + " SET geom=:geom WHERE gaul_code=18", "geom", subLinkedGeom2);
                executeSQLUpdate("INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (27, 93)");
                executeSQLUpdate("INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (18, 93)");
            }
        } else if (precision == LocationPrecision.ADMIN1) {
            executeSQLUpdate("UPDATE admin_unit_qc SET geom=:geom WHERE gaul_code=124", "geom", linkedGeom);
            location.setAdminUnitQCGaulCode(124);
        } else if (precision == LocationPrecision.ADMIN2) {
            executeSQLUpdate("UPDATE admin_unit_qc SET geom=:geom WHERE gaul_code=125", "geom", linkedGeom);
            location.setAdminUnitQCGaulCode(125);
        }
        locationDao.save(location);
        return location.getId();
    }
}
