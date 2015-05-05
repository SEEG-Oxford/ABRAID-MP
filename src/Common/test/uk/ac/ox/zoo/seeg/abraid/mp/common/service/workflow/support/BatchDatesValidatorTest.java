package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the BatchDatesValidator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class BatchDatesValidatorTest {
    private ModelRunService modelRunService;
    private DiseaseService diseaseService;
    private BatchDatesValidator validator;

    @Before
    public void setUp() {
        modelRunService = mock(ModelRunService.class);
        diseaseService = mock(DiseaseService.class);
        validator = new BatchDatesValidator(modelRunService, diseaseService);
    }

    @Test
    public void validateReturnsIfBatchStartDateIsNull() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = DateTime.now();
        mockHasBatchingEverCompleted(diseaseGroupId, false);
        DiseaseGroup diseaseGroup = createDiseaseGroup(87);

        // Act
        catchException(validator).validate(diseaseGroup, null, batchEndDate);

        // Assert
        assertThat(caughtException()).isNull();
    }

    @Test
    public void validateReturnsIfBatchEndDateIsNull() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = DateTime.now();
        mockHasBatchingEverCompleted(diseaseGroupId, false);
        DiseaseGroup diseaseGroup = createDiseaseGroup(87);

        // Act
        catchException(validator).validate(diseaseGroup, batchStartDate, null);

        // Assert
        assertThat(caughtException()).isNull();
    }

    @Test
    public void validateReturnsIfBatchingHasCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = DateTime.now().minusDays(1);
        DateTime batchEndDate = DateTime.now();
        mockHasBatchingEverCompleted(diseaseGroupId, true);
        DiseaseGroup diseaseGroup = createDiseaseGroup(87);

        // Act
        catchException(validator).validate(diseaseGroup, batchStartDate, batchEndDate);

        // Assert
        assertThat(caughtException()).isNull();
    }

    @Test
    public void validateReturnsIfSufficientOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = DateTime.now().minusDays(1);
        DateTime batchEndDate = DateTime.now();
        int minimumDataVolume = 400;
        long occurrenceCount = 400;

        mockHasBatchingEverCompleted(diseaseGroupId, false);
        DiseaseGroup diseaseGroup = createDiseaseGroup(87);
        setMinDataVolume(diseaseGroup, minimumDataVolume);
        mockGetOccurrences(diseaseGroupId, batchStartDate, batchEndDate, occurrenceCount);

        // Act
        catchException(validator).validate(diseaseGroup, batchStartDate, batchEndDate);

        // Assert
        assertThat(caughtException()).isNull();
    }

    @Test
    public void validateThrowsExceptionIfInSufficientOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = DateTime.now().minusDays(1);
        DateTime batchEndDate = DateTime.now();
        int minimumDataVolume = 400;
        long occurrenceCount = 399;

        mockHasBatchingEverCompleted(diseaseGroupId, false);
        DiseaseGroup diseaseGroup = createDiseaseGroup(87);
        setMinDataVolume(diseaseGroup, minimumDataVolume);
        mockGetOccurrences(diseaseGroupId, batchStartDate, batchEndDate, occurrenceCount);

        // Act
        catchException(validator).validate(diseaseGroup, batchStartDate, batchEndDate);

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        assertThat(caughtException()).hasMessage("This batch contains 399 model eligible occurrence(s), which is below " +
                "the Minimum Data Volume (400) and therefore will be too few for the model run after this one. " +
                "Please increase the batch size, or reduce the value of Minimum Data Volume.");
    }

    private void mockHasBatchingEverCompleted(int diseaseGroupId, boolean result) {
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(result);
    }

    private void setMinDataVolume(DiseaseGroup diseaseGroup, int minDataVolume) {
        diseaseGroup.setMinDataVolume(minDataVolume);
    }

    private DiseaseGroup createDiseaseGroup(int diseaseGroupId) {
        return new DiseaseGroup(diseaseGroupId);
    }

    private void mockGetOccurrences(int diseaseGroupId, DateTime batchStartDate, DateTime batchEndDate,
                                    long occurrenceCount) {
        when(diseaseService.getNumberOfDiseaseOccurrencesEligibleForModelRun(diseaseGroupId,
                batchStartDate, batchEndDate)).thenReturn(occurrenceCount);
    }
}
