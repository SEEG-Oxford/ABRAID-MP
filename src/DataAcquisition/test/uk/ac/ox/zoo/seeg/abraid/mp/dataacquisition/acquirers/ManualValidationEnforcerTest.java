package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers;

import ch.lambdaj.group.Group;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.*;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for ManualValidationEnforcer.
 * Copyright (c) 2015 University of Oxford
 */
public class ManualValidationEnforcerTest {
    private List<DiseaseOccurrence> toReview = new ArrayList<>();

    @Before
    public void resetCount() {
        toReview.clear();
    }

    private DiseaseService mockDiseaseService() {
        DiseaseService mock = mock(DiseaseService.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (((DiseaseOccurrence) invocationOnMock.getArguments()[0]).getStatus() == DiseaseOccurrenceStatus.IN_REVIEW) {
                    toReview.add((DiseaseOccurrence) invocationOnMock.getArguments()[0]);
                }
                return null;
            }
        }).when(mock).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        return mock;
    }

    private DiseaseGroup mockDisease(boolean isAuto) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.isAutomaticModelRunsEnabled()).thenReturn(isAuto);
        return mock;
    }

    private DiseaseOccurrence mockOccurrence(boolean isEligible, DiseaseGroup diseaseGroup, DiseaseOccurrenceStatus status) {
        final DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().isModelEligible()).thenReturn(isEligible);
        when(mock.getStatus()).thenReturn(status);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                when(mock.getStatus()).thenReturn((DiseaseOccurrenceStatus) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(mock).setStatus(any(DiseaseOccurrenceStatus.class));
        return mock;
    }

    @Test
    public void addRandomSubsetToManualValidationAdjustsTheCorrectNumberOfOccurrences() {
        // Arrange
        DiseaseGroup disease = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 0.666, 3, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(2);
    }

    @Test
    public void addRandomSubsetToManualValidationAdjustsTheCorrectNumberOfOccurrencesAtMax() {
        // Arrange
        DiseaseGroup disease = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 1, 2, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(2);
    }

    @Test
    public void addRandomSubsetToManualValidationAdjustsTheCorrectNumberOfOccurrencesAtMin() {
        // Arrange
        DiseaseGroup disease = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 0, 3, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(1);
    }

    @Test
    public void addRandomSubsetToManualValidationAdjustsTheCorrectNumberOfOccurrencesAtSmallList() {
        // Arrange
        DiseaseGroup disease = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(4, 0.666, 10, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(3);
    }

    @Test
    public void addRandomSubsetToManualValidationAdjustsPerDisease() {
        // Arrange
        DiseaseGroup disease1 = mockDisease(true);
        DiseaseGroup disease2 = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease1, DiseaseOccurrenceStatus.READY), //y
                mockOccurrence(true, disease1, DiseaseOccurrenceStatus.READY), //n
                mockOccurrence(true, disease2, DiseaseOccurrenceStatus.READY), //y
                mockOccurrence(true, disease2, DiseaseOccurrenceStatus.READY), //y
                mockOccurrence(true, disease2, DiseaseOccurrenceStatus.READY)  //n
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 0.51, 3, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(3);
        Group<DiseaseOccurrence> byDisease = group(toReview, by(on(DiseaseOccurrence.class).getDiseaseGroup()));
        assertThat(byDisease.find(disease1)).hasSize(1);
        assertThat(byDisease.find(disease2)).hasSize(2);
    }

    @Test
    public void addRandomSubsetToManualValidationIgnoresDiseasesWithoutOccurrencesToAdjust() {
        DiseaseGroup disease1 = mockDisease(false);
        DiseaseGroup disease2 = mockDisease(true);
        DiseaseGroup disease3 = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease1, DiseaseOccurrenceStatus.READY),
                mockOccurrence(false, disease2, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease3, DiseaseOccurrenceStatus.IN_REVIEW)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 1, 1, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(toReview).hasSize(0);
    }

    @Test
    public void addRandomSubsetToManualValidationDoesNotChangeInputList() {
        // Arrange
        DiseaseGroup disease = mockDisease(true);
        Set<DiseaseOccurrence> occurrences = new HashSet<>(Arrays.asList(
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY),
                mockOccurrence(true, disease, DiseaseOccurrenceStatus.READY)
        ));
        DiseaseService diseaseService = mockDiseaseService();
        ManualValidationEnforcer target = new ManualValidationEnforcer(1, 1, 2, diseaseService);

        // Act
        target.addRandomSubsetToManualValidation(occurrences);

        // Assert
        assertThat(occurrences).hasSize(3);
    }

}
