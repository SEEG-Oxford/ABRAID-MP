package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail(null, result);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void checkEmailRejectsEmptyEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("", result);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void checkEmailRejectsTooLongEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();
        char[] chars = new char[158];
        Arrays.fill(chars, 'a');

        // Act
        target.checkEmail(new String(chars) + "@" + new String(chars) + ".com", result);

        // Assert
        assertThat(result).contains("Email address must be fewer than 320 letters in length.");
    }

    @Test
    public void checkEmailRejectsInvalidEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
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
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(expertService, null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("already@exists.com", result);

        // Assert
        assertThat(result).contains("Email address already has an associated account.");
    }

    @Test
    public void checkEmailAcceptsValidEmails() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkEmail("a@b.com", result);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void checkPasswordRejectsNullPassword() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword(null, result);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void checkPasswordRejectsEmptyPassword() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("", result);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void checkCurrentPasswordRejectsIncorrectPasswords() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getExpertById(321)).thenReturn(mock(Expert.class));
        when(expertService.getExpertById(321).getPassword()).thenReturn("passwordHash");

        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.matches("password", "passwordHash")).thenReturn(false);

        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(expertService, passwordEncoder);
        List<String> result = new ArrayList<>();

        // Act
        target.checkCurrentPassword("password", 321, result);

        // Assert
        assertThat(result).contains("Current password incorrect.");
    }

    @Test
    public void checkPasswordConfirmationRejectsUnmatchedPair() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkPasswordConfirmation("password", "confirmation", result);

        // Assert
        assertThat(result).contains("Password confirmation pair must match.");
    }

    @Test
    public void checkPasswordRejectsInvalidPasswords() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("abc", result);

        // Assert
        assertThat(result).contains("Password not sufficiently complex.");
    }

    @Test
    public void checkPasswordAcceptsValidPasswords() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkPassword("qwe123Q", result);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void checkNameRejectsNullName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkName(null, result);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void checkNameRejectsEmptyName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkName("", result);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void checkNameRejectsTooLongName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();
        char[] chars = new char[1001];
        Arrays.fill(chars, 'a');

        // Act
        target.checkName(new String(chars), result);

        // Assert
        assertThat(result).contains("Name must be fewer than 1000 letters in length.");
    }

    @Test
    public void checkJobTitleRejectsNullJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle(null, result);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void checkJobTitleRejectsEmptyJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle("", result);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void checkJobTitleRejectsTooLongJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkJobTitle(
            "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do.",
            result);

        // Assert
        assertThat(result).contains("Job title must be fewer than 100 letters in length.");
    }

    @Test
    public void checkInstitutionRejectsNullInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution(null, result);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void checkInstitutionsRejectsEmptyInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution("", result);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void checkInstitutionRejectsTooLongInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkInstitution(
                "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do.",
                result);

        // Assert
        assertThat(result).contains("Institution must be fewer than 100 letters in length.");
    }

    @Test
    public void checkVisibilityRequestedRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkVisibilityRequested(null, result);

        // Assert
        assertThat(result).contains("Visibility requested must be provided.");
    }

    @Test
    public void checkDiseaseInterestsRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkDiseaseInterests(null, result);

        // Assert
        assertThat(result).contains("Disease interests must be provided.");
    }

    @Test
    public void checkIdRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkId(null, result);

        // Assert
        assertThat(result).contains("Id (id) must be provided.");
    }

    @Test
    public void checkVisibilityApprovedRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkVisibilityApproved(null, result);

        // Assert
        assertThat(result).contains("Is public visibility approved (visibilityApproved) must be provided.");
    }

    @Test
    public void checkWeightingRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkWeighting(null, result);

        // Assert
        assertThat(result).contains("Weighting (weighting) must be provided.");
    }

    @Test
    public void checkWeightingRejectsPositiveInf() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkWeighting(Double.POSITIVE_INFINITY, result);

        // Assert
        assertThat(result).contains("Weighting (weighting) not valid.");
    }

    @Test
    public void checkWeightingRejectsNegativeInf() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkWeighting(Double.NEGATIVE_INFINITY, result);

        // Assert
        assertThat(result).contains("Weighting (weighting) not valid.");
    }

    @Test
    public void checkWeightingRejectsNaN() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkWeighting(Double.NaN, result);

        // Assert
        assertThat(result).contains("Weighting (weighting) not valid.");
    }

    @Test
    public void checkIsAdministratorRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkIsAdministrator(null, result);

        // Assert
        assertThat(result).contains("Is administrator (administrator) must be provided.");
    }

    @Test
    public void checkIsSeegMemberRejectsNull() throws Exception {
        // Arrange
        ExpertValidationRulesChecker target = new ExpertValidationRulesChecker(mock(ExpertService.class), null);
        List<String> result = new ArrayList<>();

        // Act
        target.checkIsSeegMember(null, result);

        // Assert
        assertThat(result).contains("Is SEEG member (seegmember) must be provided.");
    }
}
