package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the WeightingsCalculator class.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculatorIntegrationTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Before
    public void setFixedTime() {
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsGetsReviewsForAllTimeIfLastModelRunPrepDateIsNull() {
        // Arrange
        DateTime lastModelRunPrepDate = null;
        int diseaseGroupId = 1;
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, 1);

        // Assert
        verify(mockDiseaseService, times(1)).getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId);
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsGetsReviewsForModelRunPrepForNonNullLastModelRunPrepDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        int diseaseGroupId = 1;
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        verify(mockDiseaseService, times(1)).getDiseaseOccurrenceReviewsForModelRunPrep(lastModelRunPrepDate, diseaseGroupId);
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsLogsNoReviews() {
        // Arrange
        DateTime lastModelRunPrepDate = null;
        int diseaseGroupId = 1;

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId))
                .thenReturn(new ArrayList<DiseaseOccurrenceReview>());

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        verify(logger, times(1)).info(eq("No new reviews have been submitted - expert weightings of disease occurrences will not be updated"));
    }

    // One expert (who has a weighting of 0.9) has reviewed YES (value of 1) to an occurrence. The total weighting
    // (expert's weighting x response value = 0.9) is normalised by the sum of all the experts' weightings (in this case
    // just 0.9), so the occurrence's resulting weighting should be 1, even after the shift from range [-1, 1] to [0,1].
    @Test
    public void updateDiseaseOccurrenceExpertWeightingsGivesExpectedResult() throws Exception {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now().minusDays(7);
        int diseaseGroupId = 87;

        double expertsWeighting = 0.9;
        double initialWeighting = 0.0;
        double expectedWeighting = 1.0;

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setExpertWeighting(initialWeighting);

        Expert expert = createExpert(1, "expert", expertsWeighting);

        DiseaseService mockDiseaseService = mockUpDiseaseServiceWithOneReview(expert, occurrence,
                DiseaseOccurrenceReviewResponse.YES, diseaseGroupId);
        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mockExpertService, modelRunService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        assertThat(occurrence.getExpertWeighting()).isEqualTo(expectedWeighting);
    }

    private DiseaseService mockUpDiseaseServiceWithOneReview(Expert expert, DiseaseOccurrence occurrence,
                                                             DiseaseOccurrenceReviewResponse response, int diseaseGroupId) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, occurrence, response);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(mockDiseaseService.getDiseaseOccurrenceReviewsForModelRunPrep(
                (DateTime) any(), anyInt())).thenReturn(new ArrayList<>(Arrays.asList(review)));
        return mockDiseaseService;
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsCalculatesNewDiseaseOccurrenceExpertWeightings() {
        // Arrange
        DateTime lastModelRunPrepDate = null;
        int diseaseGroupId = 87;

        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, false);
        DiseaseOccurrence occ1 = occurrences.get(0);
        DiseaseOccurrence occ2 = occurrences.get(1);
        DiseaseOccurrence occ3 = occurrences.get(2);
        DiseaseService mockDiseaseService = mockUpDiseaseServiceWithManyReviews(occ1, occ2, occ3);

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        verify(logger, times(1)).info(eq("Recalculating expert weightings for 3 disease occurrence(s) given 9 new review(s)"));
        // All experts reviewed YES (+1) for occ1, shifted from [-1, +1] to [0,1]
        assertThat(occ1.getExpertWeighting()).isEqualTo(1.0);
        // Split responses (1 YES, 1 UNSURE, 1 NO) for occ2, so the expert weighting is the total weighted response, normalised by sum of all expert weightings
        assertThat(occ2.getExpertWeighting()).isEqualTo(0.6476, offset(0.05));
        // All experts reviewed NO (-1) for occ3, shifted from [-1, +1] to [0,1]
        assertThat(occ3.getExpertWeighting()).isEqualTo(0.0);
    }

    private DiseaseService mockUpDiseaseServiceWithManyReviews(DiseaseOccurrence occ1, DiseaseOccurrence occ2, DiseaseOccurrence occ3) {
        List<DiseaseOccurrenceReview> reviews = createListOfManyReviews(occ1, occ2, occ3,
                createExpert(1, "ex1", 1.0), createExpert(2, "ex2", 0.2), createExpert(3, "ex3", 0.5));
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(87)).thenReturn(reviews);
        return mockDiseaseService;
    }

    @Test
    public void updateDiseaseOccurrenceExpertWeightingsReturnsZeroWhenAllExpertsWeightingsAreZero() throws  Exception {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(anyInt())).thenReturn(Arrays.asList(
                new DiseaseOccurrenceReview(createExpert(1, "ex1", 0.0), occurrence, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(createExpert(2, "ex2", 0.0), occurrence, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(createExpert(3, "ex3", 0.0), occurrence, DiseaseOccurrenceReviewResponse.NO)
        ));

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(null, 1);

        // Assert
        assertThat(occurrence.getExpertWeighting()).isEqualTo(0.0);
    }

    @Test
    public void updateDiseaseOccurrenceValidationAndFinalWeightingsLogsMessageWhenNoOccurrencesForModelRunRequest() {
        // Arrange
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        List<DiseaseOccurrence> emptyList = new ArrayList<>();
        when(mockDiseaseService.getDiseaseOccurrencesForModelRunRequest(anyInt(), anyBoolean())).thenReturn(emptyList);

        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, expertService, modelRunService);
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(1);

        // Assert
        verify(logger, times(1)).info(eq("No occurrences for model run - validation and final weightings will not be updated"));
    }

    @Test
    public void updateDiseaseOccurrenceValidationAndFinalWeightingsSetsAppropriateValidationWeighting() {
        // Arrange
        int diseaseGroupId = 87;
        double machineWeighting = 0.3;
        double expertWeighting = 0.2;
        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, false);
        DiseaseOccurrence occ1 = setWeightings(occurrences.get(0), null, machineWeighting);
        DiseaseOccurrence occ2 = setWeightings(occurrences.get(1), expertWeighting, machineWeighting);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
                diseaseGroupId, false)).thenReturn(Arrays.asList(occ1, occ2));
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ1.getValidationWeighting()).isEqualTo(machineWeighting);
        assertThat(occ2.getValidationWeighting()).isEqualTo(expertWeighting);
    }

    private DiseaseOccurrence setWeightings(DiseaseOccurrence occ, Double expertWeighting, double machineWeighting) {
        occ.setExpertWeighting(expertWeighting);
        occ.setMachineWeighting(machineWeighting);
        diseaseService.saveDiseaseOccurrence(occ);
        return occ;
    }

    @Test
    public void updateDiseaseOccurrenceValidationAndFinalWeightingsSetToZeroForZeroLocationResolutionWeighting() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrence occ = getDiseaseOccurrenceWithCountryPrecision(diseaseGroupId);
        DiseaseService mockDiseaseService = mockDiseaseServiceWithOccurrence(diseaseGroupId, occ, false);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ.getLocation().getPrecision().getWeighting()).isEqualTo(0.0);
        assertThat(occ.getFinalWeighting()).isEqualTo(0.0);
    }

    private DiseaseOccurrence getDiseaseOccurrenceWithCountryPrecision(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences =
                filter(having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(), equalTo(diseaseGroupId)),
                       diseaseOccurrenceDao.getAll());
        return selectFirst(occurrences, having(on(DiseaseOccurrence.class).getLocation().getPrecision(),
                equalTo(LocationPrecision.COUNTRY)));
    }

    private DiseaseService mockDiseaseServiceWithOccurrence(int diseaseGroupId, DiseaseOccurrence occ,
                                                            boolean mustHaveEnvironmentalSuitability) {
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId,
                mustHaveEnvironmentalSuitability)).thenReturn(Arrays.asList(occ));
        return mockDiseaseService;
    }

    @Test
    public void calculateNewFinalWeightingsSetsExpectedValues() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseOccurrence occ = new DiseaseOccurrence(1,
                createDiseaseGroupWithWeighting(0.4),
                createLocationWithWeighting(0.3),
                createAlertWithFeed(0.5),
                true, null, DateTime.now());
        occ.setExpertWeighting(null);
        occ.setMachineWeighting(0.6);
        DiseaseService mockDiseaseService = mockDiseaseServiceWithOccurrence(diseaseGroupId, occ, false);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ.getFinalWeighting()).isEqualTo(0.45, offset(0.000005));                // Average of (0.3, 0.4, 0.5, 0.6)
        assertThat(occ.getFinalWeightingExcludingSpatial()).isEqualTo(0.5, offset(0.000005)); // Average of (0.4, 0.5, 0.6)
        // N.B. Very small rounding error here, should this be corrected before the new weightings are saved?
    }

    @Test
    public void calculateNewFinalWeightingsSetsExpectedValuesWithNullValidationWeighting() {
        // Arrange
        int diseaseGroupId = 1;
        double locationResolutionWeighting = 0.3;
        DiseaseOccurrence occ = new DiseaseOccurrence(1,
                createDiseaseGroupWithWeighting(0.4),
                createLocationWithWeighting(locationResolutionWeighting),
                createAlertWithFeed(0.5),
                true, null, DateTime.now());
        occ.setExpertWeighting(null);
        occ.setMachineWeighting(null);
        occ.setEnvironmentalSuitability(0.5);
        DiseaseService mockDiseaseService = mockDiseaseServiceWithOccurrence(diseaseGroupId, occ, false);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ.getFinalWeighting()).isEqualTo(locationResolutionWeighting);
        assertThat(occ.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
    }

    @Test
    public void calculateNewFinalWeightingsEnsuresOccurrencesHaveAnEnvironmentalSuitabilityIfACompletedModelRunExists() {
        // Arrange
        createModelRun("dengue 2", 87, ModelRunStatus.COMPLETED, "2014-07-01", "2014-07-04");
        int diseaseGroupId = 87;
        double locationResolutionWeighting = 0.3;
        DiseaseOccurrence occ = new DiseaseOccurrence(1,
                createDiseaseGroupWithWeighting(0.4),
                createLocationWithWeighting(locationResolutionWeighting),
                createAlertWithFeed(0.5),
                true, null, DateTime.now());
        occ.setExpertWeighting(null);
        occ.setMachineWeighting(null);
        occ.setEnvironmentalSuitability(0.5);
        DiseaseService mockDiseaseService = mockDiseaseServiceWithOccurrence(diseaseGroupId, occ, true);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ.getFinalWeighting()).isEqualTo(locationResolutionWeighting);
        assertThat(occ.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
    }

    private Location createLocationWithWeighting(double weighting) {
        Location location = new Location();
        location.setResolutionWeighting(weighting);
        return location;
    }

    private DiseaseGroup createDiseaseGroupWithWeighting(double weighting) {
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setWeighting(weighting);
        return diseaseGroup;
    }

    @Test
    public void updateDiseaseOccurrenceValidationAndFinalWeightingsSetToZeroForZeroDiseaseGroupTypeWeighting() {
        // Arrange
        int diseaseGroupId = 8; // Alga, a CLUSTER
        double weighting = 0.7;
        DiseaseOccurrence occ = new DiseaseOccurrence(1,
                diseaseService.getDiseaseGroupById(diseaseGroupId),
                locationService.getLocationByGeoNameId(1880252),    // Singapore, a PRECISE location
                createAlertWithFeed(weighting),
                true, weighting, DateTime.now());
        occ.setMachineWeighting(weighting);
        DiseaseService mockDiseaseService = mockDiseaseServiceWithOccurrence(diseaseGroupId, occ, false);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        target.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        assertThat(occ.getDiseaseGroup().getWeighting()).isEqualTo(0.0);
        assertThat(occ.getFinalWeighting()).isEqualTo(0.0);
    }

    private Alert createAlertWithFeed(double weighting) {
        Alert alert = new Alert();
        alert.setFeed(new Feed(weighting));
        return alert;
    }

    @Test
    public void updateExpertsWeightingsWithEmptyReviewsOfOccurrenceReturnsExpectedResult() {
        // Arrange - Only one expert has reviewed an occurrence so "reviewsOfOccurrence" in calculateDifference is empty
        int expertId = 1;
        Expert expert = createExpert(expertId, "expert", 0.9);
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, new DiseaseOccurrence(), DiseaseOccurrenceReviewResponse.YES);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(Arrays.asList(review));
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class), modelRunService);

        // Act
        Map<Integer, Double> map = target.calculateNewExpertsWeightings();

        // Assert
        assertThat(map.get(expertId)).isEqualTo(1.0);
    }

    @Test
    public void calculateNewExpertsWeightingsReturnsExpectedMap() {
        // Arrange - Experts 1 and 2 submit YES reviews for an occurrence. Their new weightings will be 1.0
        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(87, false);
        DiseaseOccurrence occ = occurrences.get(0);
        int expert1Id = 1;
        int expert2Id = 2;
        Expert ex1 = createExpert(expert1Id, "expert1", 1.0);
        Expert ex2 = createExpert(expert2Id, "expert2", 0.5);

        DiseaseOccurrenceReview review1 = new DiseaseOccurrenceReview(ex1, occ, DiseaseOccurrenceReviewResponse.YES);
        DiseaseOccurrenceReview review2 = new DiseaseOccurrenceReview(ex2, occ, DiseaseOccurrenceReviewResponse.YES);

        ExpertService mockExpertService = mock(ExpertService.class);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(Arrays.asList(review1, review2));

        WeightingsCalculator weightingsCalculator = new WeightingsCalculator(mockDiseaseService, mockExpertService, modelRunService);

        // Act
        Map<Integer, Double> map = weightingsCalculator.calculateNewExpertsWeightings();

        // Assert - NB. The map returns only the experts whose weightings have changed.
        assertThat(map.keySet().contains(expert2Id)).isTrue();
        assertThat(map.get(expert2Id)).isEqualTo(1.0);
    }

    @Test
    public void calculateNewExpertsWeightingsReturnsExpectedValuesAcrossMultipleOccurrences() {
        // Arrange
        DiseaseService mockDiseaseService = mockUpDiseaseServiceWithManyReviewsForExpertsTest();
        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator weightingsCalculator = new WeightingsCalculator(mockDiseaseService, mockExpertService, modelRunService);

        // Act
        Map<Integer, Double> map = weightingsCalculator.calculateNewExpertsWeightings();

        // Assert - These values were calculated in Weights spreadsheet, according to the formula defined there.
        assertThat(map.keySet()).hasSize(3);
        List<Double> values = new ArrayList<>();
        values.addAll(map.values());
        assertThat(values.get(0)).isEqualTo(0.5016, offset(0.05));
        assertThat(values.get(1)).isEqualTo(0.9966, offset(0.05));
        assertThat(values.get(2)).isEqualTo(0.4983, offset(0.05));
    }

    private DiseaseService mockUpDiseaseServiceWithManyReviewsForExpertsTest() {
        List<DiseaseOccurrenceReview> reviews = defaultListOfManyReviews(87);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(reviews);
        return mockDiseaseService;
    }

    @Test
    public void saveExpertsWeightingsUpdatesAllExpertsInMap() {
        // Arrange
        Map<Integer, Double> newExpertsWeightings =  new HashMap<>();
        for (Expert expert : expertService.getAllExperts()) {
            newExpertsWeightings.put(expert.getId(), 0.2);
        }
        WeightingsCalculator weightingsCalculator = new WeightingsCalculator(diseaseService, expertService, modelRunService);

        // Act
        weightingsCalculator.saveExpertsWeightings(newExpertsWeightings);
        flushAndClear();

        // Assert
        for (Expert expert : expertService.getAllExperts()) {
            assertThat(expert.getWeighting()).isEqualTo(0.2);
        }
    }

    @Test
    public void averageReturnsExpectedValue() {
        // Act
        double result = WeightingsCalculator.average(0.1, 0.2, 0.3);

        // Assert
        assertThat(result).isEqualTo(0.2, offset(0.0001));
    }

    @Test
    public void averageReturnsExpectedValueDiscountingNullValue() {
        // Act
        double result = WeightingsCalculator.average(0.1, 0.2, null);

        // Assert
        assertThat(result).isEqualTo(0.15, offset(0.0001));
    }


    @Test
    public void averageReturnsExpectedValueDiscountingAllNulls() {
        // Act
        double result = WeightingsCalculator.average(null, null, null);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    private List<DiseaseOccurrenceReview> defaultListOfManyReviews(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, false).subList(0, 3);

        Expert ex1 = createExpert(1, "ex1", 0.0);
        Expert ex2 = createExpert(2, "ex2", 0.0);
        Expert ex3 = createExpert(3, "ex3", 0.0);

        return createListOfManyReviews(occurrences.get(0), occurrences.get(1), occurrences.get(2), ex1, ex2, ex3);
    }

    private List<DiseaseOccurrenceReview> createListOfManyReviews(DiseaseOccurrence occ1, DiseaseOccurrence occ2,
                                                                  DiseaseOccurrence occ3, Expert ex1, Expert ex2, Expert ex3) {
        return Arrays.asList(
                new DiseaseOccurrenceReview(ex1, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex2, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex3, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex1, occ2, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex2, occ2, DiseaseOccurrenceReviewResponse.UNSURE),
                new DiseaseOccurrenceReview(ex3, occ2, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex1, occ3, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex2, occ3, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex3, occ3, DiseaseOccurrenceReviewResponse.NO));
    }

    private Expert createExpert(int id, String name, Double expertsWeighting) {
        Expert expert = new Expert(id);
        expert.setName(name);
        expert.setWeighting(expertsWeighting);
        return expert;
    }

    private void createModelRun(String name, int diseaseGroupId, ModelRunStatus status, String requestDate,
                                           String responseDate) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, new DateTime(requestDate));
        modelRun.setStatus(status);
        modelRun.setResponseDate(new DateTime(responseDate));
        modelRunService.saveModelRun(modelRun);
    }
}
