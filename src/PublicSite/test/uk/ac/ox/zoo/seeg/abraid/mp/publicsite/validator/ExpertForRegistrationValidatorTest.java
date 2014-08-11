package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertBasic;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ExpertForRegistrationValidator.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertForRegistrationValidatorTest {
    private ExpertForRegistrationValidator target =
            new ExpertForRegistrationValidator(mock(ReCaptcha.class), mock(ExpertService.class));

    @Test
    public void createValidationCaptchaCallsReCaptchaMethodWithCorrectTheme() throws Exception {
        // Arrange
        ReCaptcha mockCaptcha = mock(ReCaptcha.class);
        when(mockCaptcha.createRecaptchaHtml(anyString(), anyString(), anyInt())).thenReturn("expected");
        ExpertForRegistrationValidator localTarget =
                new ExpertForRegistrationValidator(mockCaptcha, null);

        // Act
        String result = localTarget.createValidationCaptcha();

        // Assert
        assertThat(result).isEqualTo("expected");
        verify(mockCaptcha, times(1)).createRecaptchaHtml(eq((String) null), eq("clean"), eq((Integer) null));
    }

    @Test
    public void validateBasicFieldsRejectsNullEmails() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn(null);

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void validateBasicFieldsRejectsEmptyEmails() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn("");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Email address must be provided.");
    }

    @Test
    public void validateBasicFieldsRejectsTooLongEmails() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        char[] chars = new char[158];
        Arrays.fill(chars, 'a');
        when(expert.getEmail()).thenReturn(new String(chars) + "@" + new String(chars) + ".com");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Email address must less than 320 letters in length.");
    }

    @Test
    public void validateBasicFieldsRejectsInvalidEmails() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn("a_at_b.com");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Email address not valid.");
    }

    @Test
    public void validateBasicFieldsAcceptsValidEmails() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn("a@b.com");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void validateBasicFieldsRejectsPreexistingEmails() throws Exception {
        // Arrange
        ExpertService mockExpertService = mock(ExpertService.class);
        ExpertForRegistrationValidator localTarget =
                new ExpertForRegistrationValidator(mock(ReCaptcha.class), mockExpertService);
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn("already@exists.com");
        when(mockExpertService.getExpertByEmail("already@exists.com")).thenReturn(mock(Expert.class));

        // Act
        List<String> result = localTarget.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Email address already has an associated account.");
    }

    @Test
    public void validateBasicFieldsRejectsNullPassword() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getPassword()).thenReturn(null);

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void validateBasicFieldsRejectsEmptyPassword() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getPassword()).thenReturn("");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Password must be provided.");
    }

    @Test
    public void validateBasicFieldsRejectsInvalidPasswords() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getPassword()).thenReturn("abc");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).contains("Password not sufficiently complex.");
    }

    @Test
    public void validateBasicFieldsAcceptsValidPasswords() throws Exception {
        // Arrange
        Expert expert = mockExpertBasic();
        when(expert.getPassword()).thenReturn("qwe123Q");

        // Act
        List<String> result = target.validateBasicFields(expert);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void validateDetailsFieldsRejectsNullName() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getName()).thenReturn(null);

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsEmptyName() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getName()).thenReturn("");

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Name must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsTooLongName() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        char[] chars = new char[1001];
        Arrays.fill(chars, 'a');
        when(expert.getName()).thenReturn(new String(chars));

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Name must less than 1000 letters in length.");
    }

    @Test
    public void validateDetailsFieldsRejectsNullJobTitle() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getJobTitle()).thenReturn(null);

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsEmptyJobTitle() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getJobTitle()).thenReturn("");

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Job title must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsTooLongJobTitle() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getJobTitle()).thenReturn("Alice was beginning to get very tired of sitting by her sister on the" +
                "bank, and of having nothing to do.");

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Job title must less than 100 letters in length.");
    }

    @Test
    public void validateDetailsFieldsRejectsNullInstitution() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getInstitution()).thenReturn(null);

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsEmptyInstitution() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getInstitution()).thenReturn("");

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Institution must be provided.");
    }

    @Test
    public void validateDetailsFieldsRejectsTooLongInstitution() throws Exception {
        // Arrange
        Expert expert = mockExpertDetails();
        when(expert.getInstitution()).thenReturn("Alice was beginning to get very tired of sitting by her sister on " +
                "the bank, and of having nothing to do.");

        // Act
        List<String> result = target.validateDetailsFields(expert);

        // Assert
        assertThat(result).contains("Institution must less than 100 letters in length.");
    }

    @Test
    public void validateTransientFieldsRejectsMismatchedPasswords() throws Exception {
        // Arrange
        ReCaptcha mockCaptcha = mock(ReCaptcha.class);
        ServletRequest mockRequest = mock(ServletRequest.class);
        ReCaptchaResponse mockResponse = mock(ReCaptchaResponse.class);
        when(mockCaptcha.checkAnswer(anyString(), anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockRequest.getRemoteAddr()).thenReturn("");

        ExpertForRegistrationValidator localTarget =
                new ExpertForRegistrationValidator(mockCaptcha, mock(ExpertService.class));
        JsonExpertBasic expert = mockJsonExpertBasic();
        when(expert.getPasswordConfirmation()).thenReturn("abc123Q");

        // Act
        List<String> result = localTarget.validateTransientFields(expert, mockRequest);

        // Assert
        assertThat(result).contains("Password confirmation pair must match.");
    }

    @Test
    public void validateTransientFieldsVerifiesCaptchaCorrectly() throws Exception {
        // Arrange
        ReCaptcha mockCaptcha = mock(ReCaptcha.class);
        ServletRequest mockRequest = mock(ServletRequest.class);
        ReCaptchaResponse mockResponse = mock(ReCaptchaResponse.class);
        when(mockCaptcha.checkAnswer(anyString(), anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.isValid()).thenReturn(false);
        when(mockRequest.getRemoteAddr()).thenReturn("expected address");

        ExpertForRegistrationValidator localTarget =
                new ExpertForRegistrationValidator(mockCaptcha, mock(ExpertService.class));
        JsonExpertBasic expert = mockJsonExpertBasic();

        // Act
        List<String> result = localTarget.validateTransientFields(expert, mockRequest);

        // Assert
        assertThat(result).contains("Captcha incorrect.");
        verify(mockCaptcha, times(1))
                .checkAnswer("expected address", expert.getCaptchaChallenge(), expert.getCaptchaResponse());
    }

    private static Expert mockExpertBasic() {
        Expert result = mock(Expert.class);
        when(result.getEmail()).thenReturn("a@b.com");
        when(result.getPassword()).thenReturn("qwe123Q");
        return result;
    }

    private static Expert mockExpertDetails() {
        Expert result = mock(Expert.class);
        when(result.getName()).thenReturn("Louis Pasteur");
        when(result.getJobTitle()).thenReturn("Microbiologist");
        when(result.getInstitution()).thenReturn("Pasteur Institute");
        return result;
    }

    private static JsonExpertBasic mockJsonExpertBasic() {
        JsonExpertBasic result = mock(JsonExpertBasic.class);
        when(result.getEmail()).thenReturn("a@b.com");
        when(result.getPassword()).thenReturn("qwe123Q");
        when(result.getPasswordConfirmation()).thenReturn("qwe123Q");
        when(result.getCaptchaChallenge()).thenReturn("challenge");
        when(result.getCaptchaResponse()).thenReturn("response");
        return result;
    }

}
