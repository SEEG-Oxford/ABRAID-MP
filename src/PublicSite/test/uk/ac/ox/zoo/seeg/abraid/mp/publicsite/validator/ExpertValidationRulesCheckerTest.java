package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ExpertValidationRulesChecker.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertValidationRulesCheckerTest {

    @Test
    public void checkEmailRejectsNullEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail(null, result);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void checkEmailRejectsEmptyEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("", result);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void checkEmailRejectsTooLongEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();
        char[] chars = new char[158];
        Arrays.fill(chars, 'a');

        // Act
        target.checkEmail(new String(chars) + "@" + new String(chars) + ".com", result);

        // Assert
        assertThat(result).contains("Email address must less than 320 letters in length.");
    }

    @Test
    public void checkEmailRejectsInvalidEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("a_at_b.com", result);

        // Assert
        assertThat(result).contains("Email address not valid.");
    }

    @Test
    public void validateBasicFieldsRejectsPreexistingEmails() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getExpertByEmail("already@exists.com")).thenReturn(mock(Expert.class));
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(expertService);
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("already@exists.com", result);

        // Assert
        assertThat(result).contains("Email address already has an associated account.");
    }

    @Test
    public void checkEmailAcceptsValidEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("a@b.com", result);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void checkPasswordRejectsNullPassword() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword(null, result);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void checkPasswordRejectsEmptyPassword() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("", result);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void checkPasswordRejectsInvalidPasswords() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("abc", result);

        // Assert
        assertThat(result).contains("Password not sufficiently complex.");
    }

    @Test
    public void checkPasswordAcceptsValidPasswords() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("qwe123Q", result);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void checkNameRejectsNullName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkName(null, result);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void checkNameRejectsEmptyName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkName("", result);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void checkNameRejectsTooLongName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();
        char[] chars = new char[1001];
        Arrays.fill(chars, 'a');

        // Act
        target.checkName(new String(chars), result);

        // Assert
        assertThat(result).contains("Name must less than 1000 letters in length.");
    }

    @Test
    public void checkJobTitleRejectsNullJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle(null, result);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void checkJobTitleRejectsEmptyJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle("", result);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void checkJobTitleRejectsTooLongJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle(
            "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do.",
            result);

        // Assert
        assertThat(result).contains("Job title must less than 100 letters in length.");
    }

    @Test
    public void checkInstitutionRejectsNullInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution(null, result);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void checkInstitutionsRejectsEmptyInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution("", result);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void checkInstitutionRejectsTooLongInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution(
                "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do.",
                result);

        // Assert
        assertThat(result).contains("Institution must less than 100 letters in length.");
    }

    @Test
    public void checkVisibilityRequestedRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkVisibilityRequested(null, result);

        // Assert
        assertThat(result).contains("Visibility requested must be provided.");
    }

    @Test
    public void checkDiseaseInterestsRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class));
        List<String> result = new ArrayList<>();

        // Act
        target.checkDiseaseInterests(null, result);

        // Assert
        assertThat(result).contains("Disease interests must be provided.");
    }
}
