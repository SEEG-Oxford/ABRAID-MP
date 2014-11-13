package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.registration;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertBasic;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import javax.servlet.ServletRequest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.*;

/**
 * Tests for RegistrationControllerValidator.
 * Copyright (c) 2014 University of Oxford
 */
public class RegistrationControllerValidatorTest {
    @Test
    public void createValidationCaptchaCallsReCaptchaMethodWithCorrectTheme() throws Exception {
        // Arrange
        ReCaptcha mockCaptcha = mock(ReCaptcha.class);
        when(mockCaptcha.createRecaptchaHtml(anyString(), anyString(), anyInt())).thenReturn("expected");
        RegistrationControllerValidator target =
                new RegistrationControllerValidator(mock(ExpertValidationRulesChecker.class), mockCaptcha);

        // Act
        String result = target.createValidationCaptcha();

        // Assert
        assertThat(result).isEqualTo("expected");
        verify(mockCaptcha).createRecaptchaHtml(eq((String) null), eq("clean"), eq((Integer) null));
    }

    @Test
    public void validateBasicFieldsChecksEmail() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target = new RegistrationControllerValidator(checker, mock(ReCaptcha.class));
        Expert expert = mockExpertBasic();
        when(expert.getEmail()).thenReturn("email");

        // Act
        target.validateBasicFields(expert);

        // Assert
        verify(checker).checkEmail(eq(expert.getEmail()), anyListOf(String.class));
    }

    @Test
    public void validateBasicFieldsChecksPassword() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target = new RegistrationControllerValidator(checker, mock(ReCaptcha.class));
        Expert expert = mockExpertBasic();
        when(expert.getPassword()).thenReturn("password");

        // Act
        target.validateBasicFields(expert);

        // Assert
        verify(checker).checkPassword(eq(expert.getPassword()), anyListOf(String.class));
    }

    @Test
    public void validateDetailsFieldsChecksName() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target = new RegistrationControllerValidator(checker, mock(ReCaptcha.class));
        Expert expert = mockExpertDetails();
        when(expert.getName()).thenReturn("name");

        // Act
        target.validateDetailsFields(expert);

        // Assert
        verify(checker).checkName(eq(expert.getName()), anyListOf(String.class));
    }

    @Test
    public void validateDetailsFieldsChecksJobTitle() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target =
                new RegistrationControllerValidator(checker, mock(ReCaptcha.class));
        Expert expert = mockExpertDetails();
        when(expert.getJobTitle()).thenReturn("job");

        // Act
        target.validateDetailsFields(expert);

        // Assert
        verify(checker).checkJobTitle(eq(expert.getJobTitle()), anyListOf(String.class));
    }

    @Test
    public void validateDetailsFieldsChecksInstitution() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target =
                new RegistrationControllerValidator(checker, mock(ReCaptcha.class));
        Expert expert = mockExpertDetails();
        when(expert.getInstitution()).thenReturn("institute");

        // Act
        target.validateDetailsFields(expert);

        // Assert
        verify(checker).checkInstitution(eq(expert.getInstitution()), anyListOf(String.class));
    }

    @Test
    public void validateTransientFieldsChecksPasswordsConfirmation() throws Exception {
        // Arrange
        ReCaptcha mockCaptcha = mock(ReCaptcha.class);
        ServletRequest mockRequest = mock(ServletRequest.class);
        ReCaptchaResponse mockResponse = mock(ReCaptchaResponse.class);
        when(mockCaptcha.checkAnswer(anyString(), anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockRequest.getRemoteAddr()).thenReturn("");

        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        RegistrationControllerValidator target =
                new RegistrationControllerValidator(checker, mockCaptcha);

        JsonExpertBasic expert = mockJsonExpertBasic();
        when(expert.getPassword()).thenReturn("abc123q");
        when(expert.getPasswordConfirmation()).thenReturn("abc123Q");

        // Act
        target.validateTransientFields(expert, mockRequest);

        // Assert
        verify(checker).checkPasswordConfirmation(eq("abc123q"), eq("abc123Q"), anyListOf(String.class));
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

        RegistrationControllerValidator target =
                new RegistrationControllerValidator(mock(ExpertValidationRulesChecker.class), mockCaptcha);
        JsonExpertBasic expert = mockJsonExpertBasic();

        // Act
        List<String> result = target.validateTransientFields(expert, mockRequest);

        // Assert
        assertThat(result).contains("Captcha incorrect.");
        verify(mockCaptcha)
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
