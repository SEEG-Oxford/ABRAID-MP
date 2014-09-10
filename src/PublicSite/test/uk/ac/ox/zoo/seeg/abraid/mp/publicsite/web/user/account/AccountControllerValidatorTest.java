package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import java.util.Arrays;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for AccountControllerValidator.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerValidatorTest {
    @Test
    public void validateChecksName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);
        JsonExpertDetails expert = mockExpert();
        when(expert.getName()).thenReturn("name");

        // Act
        target.validate(expert);

        // Assert
        verify(checker, times(1)).checkName(eq(expert.getName()), anyListOf(String.class));
    }

    @Test
    public void validateChecksJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);
        JsonExpertDetails expert = mockExpert();
        when(expert.getJobTitle()).thenReturn("job");

        // Act
        target.validate(expert);

        // Assert
        verify(checker, times(1)).checkJobTitle(eq(expert.getJobTitle()), anyListOf(String.class));
    }

    @Test
    public void validateChecksInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);
        JsonExpertDetails expert = mockExpert();
        when(expert.getInstitution()).thenReturn("institute");

        // Act
        target.validate(expert);

        // Assert
        verify(checker, times(1)).checkInstitution(eq(expert.getInstitution()), anyListOf(String.class));
    }

    @Test
    public void validateChecksVisibilityRequested() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);
        JsonExpertDetails expert = mockExpert();
        when(expert.getVisibilityRequested()).thenReturn(true);

        // Act
        target.validate(expert);

        // Assert
        verify(checker, times(1)).checkVisibilityRequested(eq(expert.getVisibilityRequested()), anyListOf(String.class));
    }

    @Test
    public void validateChecksDiseaseInterests() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);
        JsonExpertDetails expert = mockExpert();
        when(expert.getDiseaseInterests()).thenReturn(Arrays.asList(1, 2));

        // Act
        target.validate(expert);

        // Assert
        verify(checker, times(1)).checkDiseaseInterests(eq(expert.getDiseaseInterests()), anyListOf(String.class));
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
}
