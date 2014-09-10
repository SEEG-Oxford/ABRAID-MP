package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseExtentGeoJsonTests;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the GeoJsonDiseaseExtentFeatureProperties class.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeaturePropertiesTest extends AbstractDiseaseExtentGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseExtentFeaturePropertiesBindsParametersCorrectly() throws Exception {
        // Arrange
        String adminUnitName = "Admin Unit";
        String diseaseExtentClassName = "foo";

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                createAdminUnitGlobal(101, adminUnitName),
                new DiseaseGroup(),
                new DiseaseExtentClass(diseaseExtentClassName),
                0);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
               new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());

        // Assert
        assertThat(result.getName()).isEqualTo(adminUnitName);
        assertThat(result.getDiseaseExtentClass()).isEqualTo(diseaseExtentClassName);
        assertThat(result.getOccurrenceCount()).isEqualTo(0);
        assertThat(result.needsReview()).isTrue();
    }

    @Test
    public void formatDisplayStringRemovesUnderscoreAndCapitalisesCorrectly() {
        // Arrange
        String inputName = "POSSIBLE_PRESENCE";
        String outputName = "Possible presence";

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                defaultAdminUnitGlobal(),
                new DiseaseGroup(),
                new DiseaseExtentClass(inputName),
                0);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
               new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());

        // Assert
        assertThat(result.getDiseaseExtentClass()).isEqualTo(outputName);

    }

    @Test
    public void needsReviewIsFalseIfAdminUnitAppearsInReviewsListAndClassChangedIsNotLaterThanReviewDate() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithReview(reviews, false);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isFalse();
    }

    @Test
    public void needsReviewIsTrueIfAdminUnitAppearsInReviewsListButClassChangedDateIsLaterThanReviewDate() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithReview(reviews, true);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isTrue();
    }

    @Test
    public void needsReviewIsTrueIfAdminUnitDoesNotAppearInReviewsList() {
        // Arrange
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithoutReview();

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
                new GeoJsonDiseaseExtentFeatureProperties(extentClass, new ArrayList<AdminUnitReview>());

        // Assert
        assertThat(result.needsReview()).isTrue();
    }

    @Test
    public void needsReviewIsTrueIfClassChangedDateIsLaterThanLatestReviewDate() {
        // Arrange
        // One review before the class changed date and one after. Verify that the latest review date is successfully extracted.
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = adminUnitDiseaseExtentClassWithTwoReviews(reviews);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isFalse();
    }
}
