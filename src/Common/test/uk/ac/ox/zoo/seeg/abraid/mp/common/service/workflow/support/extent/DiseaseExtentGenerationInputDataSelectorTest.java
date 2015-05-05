package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for DiseaseExtentGenerationInputDataSelector.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationInputDataSelectorTest {

    private DiseaseService diseaseService;
    private ExpertService expertService;
    private DiseaseGroup diseaseGroup;
    private Integer diseaseGroupId = 87;
    private DateTime minimumOccurrenceDate = DateTime.now().minusYears(1);
    private Double minimumValidationWeighting = 0.2;
    private List<? extends AdminUnitGlobalOrTropical> adminUnits = Arrays.asList(mock(AdminUnitGlobal.class));


    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        expertService = mock(ExpertService.class);

        diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(diseaseGroupId);
        when(diseaseGroup.isGlobal()).thenReturn(false);

        DiseaseExtent parameters = mock(DiseaseExtent.class);
        when(diseaseGroup.getDiseaseExtentParameters()).thenReturn(parameters);
        when(diseaseGroup.getDiseaseExtentParameters().getMinValidationWeighting()).thenReturn(minimumValidationWeighting);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForManualInitialRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(null, null, false);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, true, DiseaseProcessType.MANUAL, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isNull();
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForManualRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(minimumValidationWeighting, null, false);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, false, DiseaseProcessType.MANUAL, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForManualInitialGoldStandardRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(null, null, true);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, true, DiseaseProcessType.MANUAL_GOLD_STANDARD, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isNull();
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForManualGoldStandardRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(minimumValidationWeighting, null, true);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, false, DiseaseProcessType.MANUAL_GOLD_STANDARD, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForAutoRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(minimumValidationWeighting, minimumOccurrenceDate, false);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, false, DiseaseProcessType.AUTOMATIC, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForValidatorExtentPicksCorrectDataForAutoInitialRun() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);
        Collection<DiseaseExtentClass> diseaseExtentClasses = setUpExtentClasses();
        Collection<AdminUnitReview> reviews = setUpReviews();
        Collection<DiseaseOccurrence> occurrences = setUpOccurrences(null, null, false);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForValidatorExtent(diseaseGroup, adminUnits, true, DiseaseProcessType.AUTOMATIC, minimumOccurrenceDate);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isNull();
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }

    @Test
    public void selectForModellingExtentWhenPreviousOccurrencesPresent() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);

        Collection<DiseaseExtentClass> diseaseExtentClasses = new ArrayList<>();
        Collection<AdminUnitReview> reviews = new ArrayList<>();
        Collection<DiseaseOccurrence> validatorOccurrences = new ArrayList<>();
        DiseaseExtentGenerationInputData validatorExtentData = new DiseaseExtentGenerationInputData(diseaseExtentClasses, adminUnits, reviews, validatorOccurrences);

        Collection<DiseaseOccurrence> modellingOccurrences = new ArrayList<>();
        modellingOccurrences.add(mock(DiseaseOccurrence.class));
        when(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).thenReturn(modellingOccurrences);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForModellingExtent(diseaseGroup, validatorExtentData);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(modellingOccurrences);
    }

    @Test
    public void selectForModellingExtentWhenPreviousOccurrencesNotPresent() throws Exception {
        // Arrange
        DiseaseExtentGenerationInputDataSelector dataSelector = new DiseaseExtentGenerationInputDataSelector(
                diseaseService, expertService);

        Collection<DiseaseExtentClass> diseaseExtentClasses = new ArrayList<>();
        Collection<AdminUnitReview> reviews = new ArrayList<>();
        Collection<DiseaseOccurrence> validatorOccurrences = new ArrayList<>();
        DiseaseExtentGenerationInputData validatorExtentData = new DiseaseExtentGenerationInputData(diseaseExtentClasses, adminUnits, reviews, validatorOccurrences);

        Collection<DiseaseOccurrence> modellingOccurrences = new ArrayList<>();
        when(diseaseGroup.getDiseaseExtentParameters().getLastValidatorExtentUpdateInputOccurrences()).thenReturn(modellingOccurrences);

        // Act
        DiseaseExtentGenerationInputData result = dataSelector.selectForModellingExtent(diseaseGroup, validatorExtentData);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(validatorOccurrences);
    }

    private Collection<DiseaseExtentClass> setUpExtentClasses() {
        ArrayList<DiseaseExtentClass> expectation = new ArrayList<>();
        when(diseaseService.getAllDiseaseExtentClasses()).thenReturn(expectation);
        return expectation;
    }

    private Collection<DiseaseOccurrence> setUpOccurrences(Double expectedMinimumValidationWeighting, DateTime expectedMinimumOccurrenceDate, boolean expectedOnlyGoldStandard) {
        ArrayList<DiseaseOccurrence> expectation = new ArrayList<>();
        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroupId,
                expectedMinimumValidationWeighting,
                expectedMinimumOccurrenceDate,
                expectedOnlyGoldStandard)).thenReturn(expectation);
        return expectation;
    }

    private Collection<AdminUnitReview> setUpReviews() {
        ArrayList<AdminUnitReview> expectation = new ArrayList<>();
        when(expertService.getCurrentAdminUnitReviewsForDiseaseGroup(diseaseGroupId)).thenReturn(expectation);
        return expectation;
    }
}
