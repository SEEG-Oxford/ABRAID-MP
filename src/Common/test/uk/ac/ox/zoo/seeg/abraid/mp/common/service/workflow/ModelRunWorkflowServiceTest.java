package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunWorkflowServiceImpl class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunWorkflowServiceTest {
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;
    private DiseaseOccurrenceReviewManager reviewManager;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private ModelRunWorkflowServiceImpl modelRunWorkflowService;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;
    private MachineWeightingPredictor machineWeightingPredictor;
    private EmailService emailService;
    private BatchDatesValidator batchDatesValidator;

    @Before
    public void setUp() {
        weightingsCalculator = mock(WeightingsCalculator.class);
        modelRunRequester = mock(ModelRunRequester.class);
        reviewManager = mock(DiseaseOccurrenceReviewManager.class);
        diseaseService = mock(DiseaseService.class);
        GeometryService geometryService = mock(GeometryService.class);
        diseaseExtentGenerator = mock(DiseaseExtentGenerator.class);
        automaticModelRunsEnabler = mock(AutomaticModelRunsEnabler.class);
        machineWeightingPredictor = mock(MachineWeightingPredictor.class);
        emailService = mock(EmailService.class);
        batchDatesValidator = mock(BatchDatesValidator.class);
        modelRunWorkflowService = spy(new ModelRunWorkflowServiceImpl(weightingsCalculator, modelRunRequester,
                reviewManager, diseaseService, geometryService, diseaseExtentGenerator, automaticModelRunsEnabler,
                machineWeightingPredictor, emailService, batchDatesValidator));
    }

    @Test
    public void prepareForAndRequestManuallyTriggeredModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        DateTime batchStartDate = new DateTime("2012-11-13T15:16:17");
        DateTime batchStartDateWithMinimumTime = new DateTime("2012-11-13T00:00:00.000");
        DateTime batchEndDate = new DateTime("2012-11-14T15:16:17");
        DateTime batchEndDateWithMaximumTime = new DateTime("2012-11-14T23:59:59.999");
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestManuallyTriggeredModelRun(diseaseGroupId, batchStartDate, batchEndDate);

        // Assert
        verify(batchDatesValidator).validate(eq(diseaseGroupId), eq(batchStartDateWithMinimumTime), eq(batchEndDateWithMaximumTime));
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(diseaseGroupId));
        verify(weightingsCalculator).updateExpertsWeightings();
        verify(reviewManager).updateDiseaseOccurrenceStatus(eq(diseaseGroupId), eq(DateTime.now()));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(false));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences),
                eq(batchStartDateWithMinimumTime), eq(batchEndDateWithMaximumTime));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
        verify(machineWeightingPredictor).train(eq(diseaseGroupId), same(occurrencesForTrainingPredictor));
    }

    @Test(expected = ModelRunWorkflowException.class)
    public void prepareForAndRequestManuallyTriggeredModelRunWithInvalidBatchDates() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        DateTime batchStartDate = new DateTime("2012-11-13T15:16:17");
        DateTime batchStartDateWithMinimumTime = new DateTime("2012-11-13T00:00:00.000");
        DateTime batchEndDate = new DateTime("2012-11-14T15:16:17");
        DateTime batchEndDateWithMaximumTime = new DateTime("2012-11-14T23:59:59.999");
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();
        String exceptionMessage = "Invalid batch dates";

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);
        doThrow(new ModelRunWorkflowException(exceptionMessage)).when(batchDatesValidator).validate(
                diseaseGroupId, batchStartDateWithMinimumTime, batchEndDateWithMaximumTime);


        // Act
        modelRunWorkflowService.prepareForAndRequestManuallyTriggeredModelRun(diseaseGroupId, batchStartDate,
                batchEndDate);

        // Asserted exception is in the @Test annotation - cannot use catchException() on spies
    }

    @Test
    public void prepareForAndRequestAutomaticModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestAutomaticModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(diseaseGroupId));
        verify(reviewManager).updateDiseaseOccurrenceStatus(eq(diseaseGroupId), eq(DateTime.now()));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate),
                eq(false));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class),
                isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
        verify(machineWeightingPredictor).train(eq(diseaseGroupId), same(occurrencesForTrainingPredictor));
    }

    @Test
    public void prepareForAndRequestModelRunUsingGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, true);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRunUsingGoldStandardOccurrences(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, never()).updateDiseaseOccurrenceExpertWeightings(anyInt());
        verify(weightingsCalculator).updateExpertsWeightings();
        verify(reviewManager, never()).updateDiseaseOccurrenceStatus(anyInt(), any(DateTime.class));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(true));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class),
                isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
        verify(machineWeightingPredictor).train(eq(diseaseGroupId), same(occurrencesForTrainingPredictor));
    }

    @Test
    public void enableAutomaticModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(automaticModelRunsEnabler).enable(eq(diseaseGroupId));
    }

    @Test
    public void updateExpertsWeightings() {
        // Act
        modelRunWorkflowService.updateExpertsWeightings();
        // Assert
        verify(weightingsCalculator).updateExpertsWeightings();
    }

    @Test
    public void generateDiseaseExtentWithAutomaticModelRunsDisabled() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(false));
        verify(modelRunWorkflowService, never()).selectOccurrencesForModelRun(anyInt(), anyBoolean());
    }

    @Test
    public void generateDiseaseExtentWithAutomaticModelRunsEnabled() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithMultipleDates(minimumOccurrenceDate.plus(1), minimumOccurrenceDate, minimumOccurrenceDate.plusWeeks(1));
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate),
                eq(false));
    }

    @Test
    public void generateDiseaseExtentUsingGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        // Act
        modelRunWorkflowService.generateDiseaseExtentUsingGoldStandardOccurrences(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(true));
    }

    private List<DiseaseOccurrence> createListWithDate(DateTime minimumOccurrenceDate) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setOccurrenceDate(minimumOccurrenceDate);
        return Arrays.asList(occurrence);
    }

    private List<DiseaseOccurrence> createListWithMultipleDates(DateTime...  dates) {
        List<DiseaseOccurrence> diseaseOccurrences = new ArrayList<>();
        for (DateTime date : dates) {
            DiseaseOccurrence occurrence = new DiseaseOccurrence();
            occurrence.setOccurrenceDate(date);
            diseaseOccurrences.add(occurrence);
        }
        return diseaseOccurrences;
    }
}
