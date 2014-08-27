package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for ExpertUpdateHelper.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertUpdateHelperTest {
    @Test
    public void processExpertAsTransactionUpdatesAndSavesCorrectSingleExpertCorrectly() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertUpdateHelper target = new ExpertUpdateHelper(expertService, diseaseService, mock(EmailService.class));
        JsonExpertDetails expertDto = mockExpert();
        Expert expert = mockExpertDomain();

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertAsTransaction(321, expertDto);

        // Assert
        verify(expert, times(1)).setName(expertDto.getName());
        verify(expert, times(1)).setJobTitle(expertDto.getJobTitle());
        verify(expert, times(1)).setInstitution(expertDto.getInstitution());
        verify(expert, times(1)).setVisibilityRequested(expertDto.getVisibilityRequested());

        verify(expertService, times(1)).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionResetsVisibilityAndUpdatesTheTimestampOnChangedExperts() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(12345);
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertUpdateHelper target = new ExpertUpdateHelper(expertService, diseaseService, mock(EmailService.class));
        JsonExpertDetails expertDto = mockExpert();
        Expert expert = mockExpertDomain();

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertAsTransaction(321, expertDto);

        // Assert
        verify(expert, times(1)).setVisibilityApproved(false);
        verify(expert, times(1)).setUpdatedDate(DateTime.now());

        verify(expertService, times(1)).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionDoesNotResetVisibilityOrUpdatesTheTimestampOnUnchangedExperts()
            throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertUpdateHelper target = new ExpertUpdateHelper(expertService, diseaseService, mock(EmailService.class));
        JsonExpertDetails expertDto = mockExpert();
        Expert expert = mock(Expert.class);

        // These field must appear unchanged
        when(expert.getName()).thenReturn("Hippocrates of Kos");
        when(expert.getJobTitle()).thenReturn("Physician");
        when(expert.getInstitution()).thenReturn("Classical Greece");
        when(expert.getVisibilityRequested()).thenReturn(false);

        // This field should be able to change without resetting visibility approval
        when(expertDto.getDiseaseInterests()).thenReturn(Arrays.asList(1, 2, 3, 4, 5));

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertAsTransaction(321, expertDto);

        // Assert
        verify(expert, times(0)).setVisibilityApproved(anyBoolean());
        verify(expert, times(0)).setUpdatedDate(any(DateTime.class));
        verify(expertService, times(1)).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionThrowsValidationExceptionIfNoMatchingExpert() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertUpdateHelper target = new ExpertUpdateHelper(expertService, diseaseService, mock(EmailService.class));
        JsonExpertDetails expert = mock(JsonExpertDetails.class);

        when(expertService.getExpertById(anyInt())).thenReturn(null);

        // Act
        catchException(target).processExpertAsTransaction(-1, expert);

        // Assert
        assertThat(caughtException()).isInstanceOf(ValidationException.class);
    }

    private static JsonExpertDetails mockExpert() {
        JsonExpertDetails result = mock(JsonExpertDetails.class);
        when(result.getName()).thenReturn("Hippocrates of Kos");
        when(result.getJobTitle()).thenReturn("Physician");
        when(result.getInstitution()).thenReturn("Classical Greece");
        when(result.getVisibilityRequested()).thenReturn(false);
        when(result.getDiseaseInterests()).thenReturn(Arrays.asList(1, 2, 3));
        return result;
    }

    private static Expert mockExpertDomain() {
        Expert result = mock(Expert.class);
        when(result.getName()).thenReturn("");
        when(result.getJobTitle()).thenReturn("");
        when(result.getInstitution()).thenReturn("");
        return result;
    }
}
