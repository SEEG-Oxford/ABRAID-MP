package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import java.util.Arrays;
import java.util.List;

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
        verify(checker).checkName(eq(expert.getName()), anyListOf(String.class));
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
        verify(checker).checkJobTitle(eq(expert.getJobTitle()), anyListOf(String.class));
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
        verify(checker).checkInstitution(eq(expert.getInstitution()), anyListOf(String.class));
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
        verify(checker).checkVisibilityRequested(eq(expert.getVisibilityRequested()), anyListOf(String.class));
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
        verify(checker).checkDiseaseInterests(eq(expert.getDiseaseInterests()), anyListOf(String.class));
    }

    @Test
    public void validateNewPasswordResetRequestChecksExpertExists() throws Exception {
        // Arrange
        String email = "email";
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);

        // Act
        List<String> result = target.validateNewPasswordResetRequest(email);

        // Assert
        verify(checker).checkExpertExists(email, result);
    }

    @Test
    public void validatePasswordResetRequestChecksRequestKey() throws Exception {
        // Arrange
        Integer id = 1;
        String key = "key";
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);

        // Act
        List<String> result = target.validatePasswordResetRequest(id, key);

        // Assert
        verify(checker).checkPasswordResetRequest(id, key, result);
    }

    @Test
    public void validatePasswordResetProcessingChecksRequestKey() throws Exception {
        // Arrange
        Integer id = 1;
        String key = "key";
        String newPassword = "newPassword";
        String confirmPassword = "confirmPassword";
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);

        // Act
        List<String> result = target.validatePasswordResetProcessing(newPassword, confirmPassword, id, key);

        // Assert
        verify(checker).checkPasswordResetRequest(id, key, result);
    }

    @Test
    public void validatePasswordResetProcessingChecksNewPassword() throws Exception {
        // Arrange
        Integer id = 1;
        String key = "key";
        String newPassword = "newPassword";
        String confirmPassword = "confirmPassword";
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);

        // Act
        List<String> result = target.validatePasswordResetProcessing(newPassword, confirmPassword, id, key);

        // Assert
        verify(checker).checkPassword(newPassword, result);
    }

    @Test
    public void validatePasswordResetProcessingChecksPasswordConfirmation() throws Exception {
        // Arrange
        Integer id = 1;
        String key = "key";
        String newPassword = "newPassword";
        String confirmPassword = "confirmPassword";
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AccountControllerValidator target = new AccountControllerValidator(checker);

        // Act
        List<String> result = target.validatePasswordResetProcessing(newPassword, confirmPassword, id, key);

        // Assert
        verify(checker).checkPasswordConfirmation(newPassword, confirmPassword, result);
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
