package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseExtentGeoJsonTests {

    public static AdminUnitDiseaseExtentClass defaultAdminUnitDiseaseExtentClassWithoutReview() {
        return new AdminUnitDiseaseExtentClass(
                defaultAdminUnitGlobal(),
                new DiseaseGroup(),
                new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
                0);
    }

    public static AdminUnitDiseaseExtentClass defaultAdminUnitDiseaseExtentClassWithReview(
            List<AdminUnitReview> reviews, boolean classChangedLaterThanReview) {
        AdminUnitGlobal adminUnitGlobal = defaultAdminUnitGlobal();
        AdminUnitReview review = mockAdminUnitReview(adminUnitGlobal);
        reviews.add(review);
        return new AdminUnitDiseaseExtentClass(
            adminUnitGlobal,
            new DiseaseGroup(),
            new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
            0,
            classChangedLaterThanReview ? review.getCreatedDate().plusDays(1) : review.getCreatedDate().minusDays(1));
    }

    public static AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClassWithTwoReviews(List<AdminUnitReview> reviews) {
        AdminUnitGlobal adminUnitGlobal = defaultAdminUnitGlobal();
        AdminUnitReview review1 = mockAdminUnitReviewWithDate(adminUnitGlobal, DateTime.now().minusDays(1));
        DateTime classChangedDate = DateTime.now();
        AdminUnitReview review2 = mockAdminUnitReviewWithDate(adminUnitGlobal, DateTime.now().plusDays(1));

        reviews.add(review1);
        reviews.add(review2);

        return new AdminUnitDiseaseExtentClass(
            adminUnitGlobal,
            new DiseaseGroup(),
            new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
            0,
            classChangedDate
        );
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

    private static AdminUnitReview mockAdminUnitReview(AdminUnitGlobal adminUnitGlobal) {
        return mockAdminUnitReviewWithDate(adminUnitGlobal, DateTime.now());
    }

    private static AdminUnitReview mockAdminUnitReviewWithDate(AdminUnitGlobal adminUnitGlobal, DateTime createdDate) {
        AdminUnitReview review = mock(AdminUnitReview.class);
        when(review.getAdminUnitGlobalOrTropicalGaulCode()).thenReturn(adminUnitGlobal.getGaulCode());
        when(review.getCreatedDate()).thenReturn(createdDate);
        return review;
    }
}
