package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.List;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseExtentGeoJsonTests {

    public static AdminUnitDiseaseExtentClass defaultAdminUnitDiseaseExtentClass() {
        return defaultAdminUnitDiseaseExtentClassWithoutReview(false);
    }

    public static AdminUnitDiseaseExtentClass defaultAdminUnitDiseaseExtentClassWithoutReview(boolean hasChanged) {
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                defaultAdminUnitGlobal(),
                new DiseaseGroup(),
                new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
                0);
        adminUnitDiseaseExtentClass.setHasClassChanged(hasChanged);
        return adminUnitDiseaseExtentClass;
    }

    public static AdminUnitDiseaseExtentClass
        defaultAdminUnitDiseaseExtentClassWithReview(List<AdminUnitReview> reviews, boolean hasChanged) {
            AdminUnitGlobal adminUnitGlobal = defaultAdminUnitGlobal();
            AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                adminUnitGlobal,
                new DiseaseGroup(),
                new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
                0);
            adminUnitDiseaseExtentClass.setHasClassChanged(hasChanged);
            AdminUnitReview review = createAdminUnitReview(adminUnitGlobal);
            reviews.add(review);
            return adminUnitDiseaseExtentClass;
    }

    public static AdminUnitGlobal defaultAdminUnitGlobal() {
        return createAdminUnitGlobal(1, "Admin Unit");
    }

    public static AdminUnitGlobal createAdminUnitGlobal(int gaulCode, String name) {
        AdminUnitGlobal adminUnit = new AdminUnitGlobal(gaulCode);
        adminUnit.setPublicName(name);
        MultiPolygon geom = createGeom();
        adminUnit.setSimplifiedGeom(geom);
        return adminUnit;
    }

    private static MultiPolygon createGeom() {
        Polygon polygon = GeometryUtils.createPolygon(1, 1, 2, 2, 3, 3, 1, 1);
        return GeometryUtils.createMultiPolygon(polygon);
    }

    private static AdminUnitReview createAdminUnitReview(AdminUnitGlobal adminUnitGlobal) {
        AdminUnitReview review = new AdminUnitReview();
        review.setAdminUnitGlobalGaulCode(adminUnitGlobal.getGaulCode());
        return review;
    }
}
