package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for AdminExpertsHelper.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminExpertsHelperTest {
    @Test
    public void processExpertsAsTransactionUpdatesAndSavesCorrectSingleExpertCorrectly() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper target = new AdminExpertsHelper(expertService);
        JsonExpertFull expertDto = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(expertDto);
        Expert expert = mock(Expert.class);

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert
        when(expertDto.getId()).thenReturn(321);
        when(expertDto.getVisibilityApproved()).thenReturn(true);
        when(expertDto.getWeighting()).thenReturn(123.456);
        when(expertDto.isAdministrator()).thenReturn(false);
        when(expertDto.isSEEGMember()).thenReturn(true);

        // Act
        target.processExpertsAsTransaction(experts);

        // Assert
        verify(expert, times(1)).setVisibilityApproved(true);
        verify(expert, times(1)).setWeighting(123.456);
        verify(expert, times(1)).setAdministrator(false);
        verify(expert, times(1)).setSeegMember(true);

        verify(expertService, times(1)).saveExpert(expert);
    }

    @Test
         public void processExpertsAsTransactionCanProcessMultipleExperts() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper target = new AdminExpertsHelper(expertService);
        List<JsonExpertFull> experts =
                Arrays.asList(mock(JsonExpertFull.class), mock(JsonExpertFull.class), mock(JsonExpertFull.class));

        when(expertService.getExpertById(anyInt())).thenReturn(mock(Expert.class));

        // Act
        target.processExpertsAsTransaction(experts);

        // Assert
        verify(expertService, times(3)).saveExpert(any(Expert.class));
    }

    @Test
    public void processExpertsAsTransactionThrowsValidationExceptionIfNoMatchingExpert() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper target = new AdminExpertsHelper(expertService);
        List<JsonExpertFull> experts =
                Arrays.asList(mock(JsonExpertFull.class), mock(JsonExpertFull.class), mock(JsonExpertFull.class));

        when(expertService.getExpertById(anyInt())).thenReturn(null);

        // Act
        catchException(target).processExpertsAsTransaction(experts);

        // Assert
        assertThat(caughtException()).isInstanceOf(ValidationException.class);
    }

}