package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the ValidationParametersHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationParametersHandlerTest {
    private ValidationParametersHandler validationParametersHandler;
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        validationParametersHandler = new ValidationParametersHandler(diseaseService, diseaseOccurrenceValidationService);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void handleValidationParametersHasNoEffectIfModelIncomplete() {
        // Arrange
        ModelRun modelRun = createModelRun(87, ModelRunStatus.FAILED);

        // Act
        validationParametersHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, never()).getDiseaseGroupById(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesForModelRunRequest(anyInt());
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseGroup(any(DiseaseGroup.class));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(any(DiseaseOccurrence.class));
    }

    @Test
    public void handleValidationParametersHasNoEffectIfValidationProcessAlreadyStarted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = createDiseaseGroupAndMockLoad(diseaseGroupId);
        diseaseGroup.setValidationProcessStartDate(DateTime.now());

        // Act
        validationParametersHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, times(1)).getDiseaseGroupById(eq(diseaseGroupId));
        verify(diseaseService, never()).getDiseaseOccurrencesForModelRunRequest(anyInt());
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseGroup(any(DiseaseGroup.class));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(any(DiseaseOccurrence.class));
    }

    @Test
    public void handleValidationParametersAddsAndSavesDiseaseOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = createDiseaseGroupAndMockLoad(diseaseGroupId);
        List<DiseaseOccurrence> occurrences = createDiseaseOccurrences(5);

        when(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)).thenReturn(occurrences);
        when(diseaseOccurrenceValidationService.addValidationParameters(same(occurrences.get(0)))).thenReturn(true);
        when(diseaseOccurrenceValidationService.addValidationParameters(same(occurrences.get(1)))).thenReturn(false);
        when(diseaseOccurrenceValidationService.addValidationParameters(same(occurrences.get(2)))).thenReturn(false);
        when(diseaseOccurrenceValidationService.addValidationParameters(same(occurrences.get(3)))).thenReturn(true);
        when(diseaseOccurrenceValidationService.addValidationParameters(same(occurrences.get(4)))).thenReturn(true);

        // Act
        validationParametersHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, times(3)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(0)));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(3)));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(4)));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
        assertThat(diseaseGroup.getValidationProcessStartDate()).isEqualTo(DateTime.now());
    }

    private ModelRun createModelRun(int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(1);
        modelRun.setName("test");
        modelRun.setDiseaseGroupId(diseaseGroupId);
        modelRun.setRequestDate(DateTime.now());
        modelRun.setStatus(status);
        return modelRun;
    }

    private DiseaseGroup createDiseaseGroupAndMockLoad(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        return diseaseGroup;
    }

    private List<DiseaseOccurrence> createDiseaseOccurrences(int occurrenceCount) {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        for (int i = 0; i < occurrenceCount; i++) {
            occurrences.add(new DiseaseOccurrence());
        }
        return occurrences;
    }
}
