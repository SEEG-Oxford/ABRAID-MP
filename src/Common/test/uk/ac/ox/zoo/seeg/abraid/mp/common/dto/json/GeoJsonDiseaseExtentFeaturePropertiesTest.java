package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseExtentGeoJsonTests;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the GeoJsonDiseaseExtentFeatureProperties class.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeaturePropertiesTest extends AbstractDiseaseExtentGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseExtentFeaturePropertiesBindsParametersCorrectly() throws Exception {
        // Arrange
        String adminUnitName = "Admin Unit";
        String validatorDiseaseExtentClassName = "foo2";

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                createAdminUnitGlobal(101, adminUnitName),
                new DiseaseGroup(),
                new DiseaseExtentClass("Not Used"),
                new DiseaseExtentClass(validatorDiseaseExtentClassName),
                0);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
               new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());

        // Assert
        assertThat(result.getName()).isEqualTo(adminUnitName);
        assertThat(result.getDiseaseExtentClass()).isEqualTo(validatorDiseaseExtentClassName);
        assertThat(result.getOccurrenceCount()).isEqualTo(0);
        assertThat(result.needsReview()).isTrue();  // since the admin unit has never been reviewed
    }

    @Test
    public void formatDisplayStringRemovesUnderscoreAndCapitalisesCorrectly() {
        // Arrange
        String inputName = "POSSIBLE_PRESENCE";
        String outputName = "Possible presence";

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                defaultAdminUnitGlobal(),
                new DiseaseGroup(),
                new DiseaseExtentClass("NotUSED"),
                new DiseaseExtentClass(inputName),
                0);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
               new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());

        // Assert
        assertThat(result.getDiseaseExtentClass()).isEqualTo(outputName);
    }

    @Test
    public void getComparisonDateReturnsNullIfBothDatesAreNull() {
        testGetComparisonDate(null, null, null);
    }

    @Test
    public void getComparisonDateReturnsLastExtentGenerationDateIfAutomaticModelRunsStartDateIsNull() {
        DateTime lastExtentGenerationDate = DateTime.now();
        testGetComparisonDate(lastExtentGenerationDate, null, lastExtentGenerationDate);
    }

    @Test
    public void getComparisonDateReturnsAutomaticModelRunsStartDateIfLastExtentGenerationDateIsNull() {
        DateTime automaticModelRunsStartDate = DateTime.now();
        testGetComparisonDate(null, automaticModelRunsStartDate, automaticModelRunsStartDate);
    }

    @Test
    public void getComparisonDateReturnsLastExtentGenerationDateIfLaterThanAutomaticModelRunsStartDate() {
        DateTime lastExtentGenerationDate = DateTime.now();
        DateTime automaticModelRunsStartDate  = DateTime.now().minusDays(1);
        testGetComparisonDate(lastExtentGenerationDate, automaticModelRunsStartDate, lastExtentGenerationDate);
    }

    @Test
    public void getComparisonDateReturnsAutomaticModelRunsStartDateIfLaterThanLastExtentGenerationDate() {
        DateTime automaticModelRunsStartDate = DateTime.now();
        DateTime lastExtentGenerationDate = DateTime.now().minusDays(1);
        testGetComparisonDate(lastExtentGenerationDate, automaticModelRunsStartDate, automaticModelRunsStartDate);
    }

    private void testGetComparisonDate(DateTime lastExtentGenerationDate, DateTime automaticModelRunsStartDate, DateTime expectedComparisonDate) {
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = defaultAdminUnitDiseaseExtentClassWithoutReview();
        GeoJsonDiseaseExtentFeatureProperties properties = new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(lastExtentGenerationDate);
        when(diseaseGroup.getAutomaticModelRunsStartDate()).thenReturn(automaticModelRunsStartDate);

        DateTime comparisonDate = properties.getComparisonDate(diseaseGroup);
        assertThat(comparisonDate).isEqualTo(expectedComparisonDate);
    }

    @Test
    public void needsReviewIsTrueIfAdminUnitDiseaseExtentClassHasNeverBeenReviewedAndComparisonDateIsNull() {
        testNeedsReview(null);
    }

    @Test
    public void needsReviewIsTrueIfAdminUnitDiseaseExtentClassHasNeverBeenReviewedAndComparisonDateIsNotNull() {
        testNeedsReview(DateTime.now());
    }

    private void testNeedsReview(DateTime comparisonDate) {
        // Arrange
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                new AdminUnitGlobal(),
                mockDiseaseGroupWithComparisonDate(comparisonDate),
                new DiseaseExtentClass("name"),
                new DiseaseExtentClass("name"),
                0
        );

        // Act
        GeoJsonDiseaseExtentFeatureProperties result =
                new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, new ArrayList<AdminUnitReview>());
        // Assert
        assertThat(result.needsReview()).isTrue();
    }

    private DiseaseGroup mockDiseaseGroupWithComparisonDate(DateTime comparisonDate) {
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getAutomaticModelRunsStartDate()).thenReturn(comparisonDate);
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(null);
        return diseaseGroup;
    }

    @Test
    public void needsReviewIsFalseIfAdminUnitDiseaseExtentClassHasBeenReviewedAndComparisonDateIsNull() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithReview(reviews, null);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isFalse();
    }

    @Test
    public void needsReviewIsTrueIfReviewedDateIsBeforeComparisonDate() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithReview(reviews, true);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isTrue();
    }

    @Test
    public void needsReviewIsFalseIfReviewedDateIsAfterComparisonDate() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        AdminUnitDiseaseExtentClass extentClass = defaultAdminUnitDiseaseExtentClassWithReview(reviews, false);

        // Act
        GeoJsonDiseaseExtentFeatureProperties result = new GeoJsonDiseaseExtentFeatureProperties(extentClass, reviews);

        // Assert
        assertThat(result.needsReview()).isFalse();
    }

}
