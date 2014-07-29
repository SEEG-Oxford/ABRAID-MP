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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandlerTest {
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;
    private DiseaseService diseaseService;
    private DiseaseOccurrenceHandlerHelper diseaseOccurrenceHandlerHelper;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        diseaseOccurrenceHandlerHelper = mock(DiseaseOccurrenceHandlerHelper.class);
        diseaseOccurrenceHandler = new DiseaseOccurrenceHandler(diseaseService, diseaseOccurrenceHandlerHelper);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void handleValidationParametersHasNoEffectIfModelIncomplete() {
        // Arrange
        ModelRun modelRun = createModelRun(87, ModelRunStatus.FAILED);

        // Act
        //diseaseOccurrenceHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, never()).getDiseaseGroupById(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesForModelRunRequest(anyInt());
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseGroup(any(DiseaseGroup.class));
    }

    @Test
    public void handleValidationParametersHasNoEffectIfValidationProcessAlreadyStarted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = createDiseaseGroupAndMockLoad(diseaseGroupId);

        // Act
        //diseaseOccurrenceHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, times(1)).getDiseaseGroupById(eq(diseaseGroupId));
        verify(diseaseService, never()).getDiseaseOccurrencesForModelRunRequest(anyInt());
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseGroup(any(DiseaseGroup.class));
    }

    @Test
    public void handleValidationParametersAddsAndSavesDiseaseOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = createDiseaseGroupAndMockLoad(diseaseGroupId);
        List<DiseaseOccurrence> occurrences = createDiseaseOccurrences(5);

        when(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)).thenReturn(occurrences);

        // Act
        //diseaseOccurrenceHandler.handleValidationParameters(modelRun);

        // Assert
        verify(diseaseService, times(3)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(0)));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(3)));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrences.get(4)));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
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
