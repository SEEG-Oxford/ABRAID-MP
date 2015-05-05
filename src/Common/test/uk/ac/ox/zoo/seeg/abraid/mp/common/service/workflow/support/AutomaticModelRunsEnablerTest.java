package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the AutomaticModelRunsEnabler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AutomaticModelRunsEnablerTest {
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        when(diseaseService.subtractDaysBetweenModelRuns(any(DateTime.class))).thenAnswer(new Answer<LocalDate>() {
            @Override
            public LocalDate answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ((DateTime) invocationOnMock.getArguments()[0]).toLocalDate().minusDays(7);
            }
        });
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        automaticModelRunsEnabler = new AutomaticModelRunsEnabler(diseaseService, diseaseOccurrenceValidationService);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void enableAutomaticModelRunsSavesAutomaticModelRunsStartDateOnDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        // Act
        automaticModelRunsEnabler.enable(diseaseGroupId);

        // Assert
        verify(diseaseService).saveDiseaseGroup(diseaseGroup);
        assertThat(diseaseGroup.getAutomaticModelRunsStartDate()).isEqualTo(now);
    }

    @Test
    public void enableAutomaticModelRunsAddsValidationParametersToDiseaseOccurrence() throws Exception {
        // Arrange
        int diseaseGroupId = 87;

        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DiseaseOccurrence occurrence1 = new DiseaseOccurrence();
        DiseaseOccurrence occurrence2 = new DiseaseOccurrence();
        occurrence1.setOccurrenceDate(DateTime.now().minusDays(21));
        occurrence2.setOccurrenceDate(DateTime.now().minusDays(1));
        when(diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
                diseaseGroupId,
                DiseaseOccurrenceStatus.READY,
                DiseaseOccurrenceStatus.AWAITING_BATCHING
        )).thenReturn(
                Arrays.asList(occurrence1, occurrence2)
        );

        // Act
        automaticModelRunsEnabler.enable(diseaseGroupId);

        // Assert
        assertThat(occurrence1.getStatus()).isEqualTo(DiseaseOccurrenceStatus.DISCARDED_UNUSED);
        assertThat(occurrence1.getFinalWeighting()).isNull();
        assertThat(occurrence1.getFinalWeightingExcludingSpatial()).isNull();

        verify(diseaseOccurrenceValidationService).addValidationParameters(Arrays.asList(occurrence2));
        verify(diseaseService).saveDiseaseOccurrence(occurrence1);
        verify(diseaseService).saveDiseaseOccurrence(occurrence2);
    }
}
