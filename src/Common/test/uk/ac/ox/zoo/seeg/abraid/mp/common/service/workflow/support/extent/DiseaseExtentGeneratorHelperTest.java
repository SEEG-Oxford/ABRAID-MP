package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DiseaseExtentGeneratorHelper.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGeneratorHelperTest extends BaseDiseaseExtentGenerationTests {
    @Before
    public void setup() {
        baseSetup();
    }

    @Test
    public void computeDiseaseExtentReturnsCorrectResultForUpdatedExtent() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputData inputData = createInputData(createReviews(), createOccurrences());
        DiseaseExtentGenerationOutputData expectedResult = createUpdatedDiseaseExtentOccurrencesAndReviewsResults();

        // Act
        DiseaseExtentGeneratorHelper target = new DiseaseExtentGeneratorHelper(inputData, parameters);
        DiseaseExtentGenerationOutputData result = target.computeDiseaseExtent();

        // Assert
        assertThatExtentsMatch(result, expectedResult);
    }

    @Test
    public void computeDiseaseExtentReturnsCorrectResultForUpdatedExtentWithZeroOccurrences() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputData inputData = createInputData(createReviews(), null);
        DiseaseExtentGenerationOutputData expectedResult = createUpdatedDiseaseExtentReviewOnlyResults();

        // Act
        DiseaseExtentGeneratorHelper target = new DiseaseExtentGeneratorHelper(inputData, parameters);
        DiseaseExtentGenerationOutputData result = target.computeDiseaseExtent();

        // Assert
        assertThatExtentsMatch(result, expectedResult);
    }

    @Test
    public void computeDiseaseExtentReturnsCorrectResultForUpdatedExtentWithZeroReviews() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputData inputData = createInputData(null, createOccurrences());
        DiseaseExtentGenerationOutputData expectedResult = createUpdatedDiseaseExtentOccurrencesOnlyResults();

        // Act
        DiseaseExtentGeneratorHelper target = new DiseaseExtentGeneratorHelper(inputData, parameters);
        DiseaseExtentGenerationOutputData result = target.computeDiseaseExtent();

        // Assert
        assertThatExtentsMatch(result, expectedResult);
    }

    @Test
    public void computeDiseaseExtentReturnsCorrectResultForUpdatedExtentWithZeroOccurrencesOrReviews() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputData inputData = createInputData(null, null);
        DiseaseExtentGenerationOutputData expectedResult = createAllUncertainExtentResults();

        // Act
        DiseaseExtentGeneratorHelper target = new DiseaseExtentGeneratorHelper(inputData, parameters);
        DiseaseExtentGenerationOutputData result = target.computeDiseaseExtent();

        // Assert
        assertThatExtentsMatch(result, expectedResult);
    }

    protected void assertThatExtentsMatch(DiseaseExtentGenerationOutputData actual, DiseaseExtentGenerationOutputData expectation) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            assertThat(extractDiseaseExtentClass(actual, adminUnit).getName()).isEqualTo(extractDiseaseExtentClass(expectation, adminUnit).getName());
            assertThat(extractDiseaseExtentClass(actual, adminUnit)).isEqualTo(extractDiseaseExtentClass(expectation, adminUnit));
            assertThat(extractOccurrenceCount(actual, adminUnit)).isEqualTo(extractOccurrenceCount(expectation, adminUnit));
            assertThat(extractLatestOccurrences(actual, adminUnit)).hasSize(Math.min(extractOccurrenceCount(expectation, adminUnit), 5));
        }
    }
}
