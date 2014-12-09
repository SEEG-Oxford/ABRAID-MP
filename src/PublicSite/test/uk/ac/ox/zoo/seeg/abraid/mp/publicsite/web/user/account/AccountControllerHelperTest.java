package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.PasswordResetRequest;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for AccountControllerHelper.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerHelperTest {
    @Test
    public void processExpertAsTransactionUpdatesAndSavesCorrectSingleExpertCorrectly() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), null);
        JsonExpertDetails expertDto = mockExpert();
        Expert expert = mockExpertDomain();

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertProfileUpdateAsTransaction(321, expertDto);

        // Assert
        verify(expert).setName(expertDto.getName());
        verify(expert).setJobTitle(expertDto.getJobTitle());
        verify(expert).setInstitution(expertDto.getInstitution());
        verify(expert).setVisibilityRequested(expertDto.getVisibilityRequested());

        verify(expertService).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionResetsVisibilityAndUpdatesTheTimestampOnChangedExperts() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(12345);
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), null);
        JsonExpertDetails expertDto = mockExpert();
        Expert expert = mockExpertDomain();

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertProfileUpdateAsTransaction(321, expertDto);

        // Assert
        verify(expert).setVisibilityApproved(false);
        verify(expert).setUpdatedDate(DateTime.now());

        verify(expertService).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionDoesNotResetVisibilityOrUpdatesTheTimestampOnUnchangedExperts()
            throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), null);
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
        target.processExpertProfileUpdateAsTransaction(321, expertDto);

        // Assert
        verify(expert, times(0)).setVisibilityApproved(anyBoolean());
        verify(expert, times(0)).setUpdatedDate(any(DateTime.class));
        verify(expertService).saveExpert(expert);
    }

    @Test
    public void processExpertAsTransactionThrowsValidationExceptionIfNoMatchingExpert() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), null);
        JsonExpertDetails expert = mock(JsonExpertDetails.class);

        when(expertService.getExpertById(anyInt())).thenReturn(null);

        // Act
        catchException(target).processExpertProfileUpdateAsTransaction(-1, expert);

        // Assert
        assertThat(caughtException()).isInstanceOf(ValidationException.class);
    }

    @Test
    public void processExpertAsTransactionSendsEmailForUpdatedUserRequiringVisibilityApproval() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, emailService, null);
        JsonExpertDetails expertDto = mockExpert();
        when(expertDto.getName()).thenReturn("asdfas");
        Expert expert = mockExpertDomain();
        when(expert.getVisibilityRequested()).thenReturn(true);

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertProfileUpdateAsTransaction(321, expertDto);

        // Assert
        verify(expertService).saveExpert(expert);
        Map<String, Object> data = new HashMap<>();
        data.put("expert", expert);
        verify(emailService).sendEmailInBackground(
                "Updated user requiring visibility sign off",
                "account/updatedUserEmail.ftl",
                data);
    }

    @Test
    public void processExpertAsTransactionSkipsEmailForUpdatedUserNotRequiringVisibilityApproval() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        EmailService emailService = mock(EmailService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, emailService, null);
        JsonExpertDetails expertDto = mockExpert();
        when(expertDto.getName()).thenReturn("asdfas");
        Expert expert = mockExpertDomain();
        when(expert.getVisibilityRequested()).thenReturn(false);

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert

        // Act
        target.processExpertProfileUpdateAsTransaction(321, expertDto);

        // Assert
        verify(expertService).saveExpert(expert);
        verify(emailService, never()).sendEmailInBackground(anyString(), anyString(), anyMapOf(String.class, Object.class));
    }

    @Test
    public void processExpertPasswordChangeAsTransactionUpdatesAndSavesPasswordCorrectly() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), passwordEncoder);
        Expert expert = mockExpertDomain();

        when(expertService.getExpertById(321)).thenReturn(expert); // Gets the correct expert
        when(passwordEncoder.encode("password")).thenReturn("passwordHash");

        // Act
        target.processExpertPasswordChangeAsTransaction(321, "password");

        // Assert
        verify(expert).setPassword("passwordHash");
        verify(expertService).saveExpert(expert);
    }

    @Test
    public void processExpertPasswordChangeAsTransactionThrowsValidationExceptionIfNoMatchingExpert() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, diseaseService, mock(EmailService.class), mock(PasswordEncoder.class));

        when(expertService.getExpertById(anyInt())).thenReturn(null);

        // Act
        catchException(target).processExpertPasswordChangeAsTransaction(-1, "password");

        // Assert
        assertThat(caughtException()).isInstanceOf(ValidationException.class);
    }

    @Test
    public void processExpertPasswordResetRequestAsTransactionCreatesNewPasswordResetRequest() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, mock(EmailService.class), null);

        when(expertService.getExpertByEmail("email")).thenReturn(mock(Expert.class));


        // Act
        target.processExpertPasswordResetRequestAsTransaction("email", "url");

        // Assert
        verify(expertService).createAndSavePasswordResetRequest(eq("email"), anyString());
    }

    @Test
    public void processExpertPasswordResetRequestAsTransactionSendsResetEmail() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        EmailService emailService = mock(EmailService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, emailService, null);

        when(expertService.getExpertByEmail("email")).thenReturn(mock(Expert.class));
        when(expertService.createAndSavePasswordResetRequest(eq("email"), anyString())).thenReturn(4321);

        // Act
        target.processExpertPasswordResetRequestAsTransaction("email", "url");

        // Assert
        Class<Map<String, Object>> argType = (Class<Map<String, Object>>) (Class) Map.class;
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(argType);
        verify(emailService).sendEmailInBackground(eq("email"), eq("ABRAID-MP Password Reset"), eq("account/passwordResetEmail.ftl"), captor.capture());
        Map<String, Object> data = captor.getValue();
        assertThat(data.get("url")).isEqualTo("url");
        assertThat(data.get("id")).isEqualTo(4321);
        assertThat(data.get("key")).isNotNull();
    }

    @Test
    public void processExpertPasswordResetRequestAsTransactionThrowsValidationExceptionIfNoMatchingExpert() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, mock(EmailService.class), null);

        when(expertService.getExpertByEmail("email")).thenReturn(null);

        // Act
        catchException(target).processExpertPasswordResetRequestAsTransaction("email", "url");

        // Assert
        assertThat(caughtException()).isInstanceOf(ValidationException.class);
    }

    @Test
    public void processExpertPasswordResetAsTransactionUpdatesExpertsPassword() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, mock(EmailService.class), passwordEncoder);

        PasswordResetRequest resetRequest = mock(PasswordResetRequest.class);
        Expert expert = mock(Expert.class);
        when(expertService.getPasswordResetRequest(7)).thenReturn(resetRequest);
        when(resetRequest.getExpert()).thenReturn(expert);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Act
        target.processExpertPasswordResetAsTransaction("password", 7);

        // Assert
        InOrder inOrder = inOrder(expert, expertService);
        inOrder.verify(expert).setPassword("hashedPassword");
        inOrder.verify(expertService).saveExpert(expert);
    }

    @Test
    public void processExpertPasswordResetAsTransactionDeletesCompletedResetRequest() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, mock(EmailService.class), passwordEncoder);

        PasswordResetRequest resetRequest = mock(PasswordResetRequest.class);
        Expert expert = mock(Expert.class);
        when(expertService.getPasswordResetRequest(7)).thenReturn(resetRequest);
        when(resetRequest.getExpert()).thenReturn(expert);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Act
        target.processExpertPasswordResetAsTransaction("password", 7);

        // Assert
        verify(expertService).deletePasswordResetRequest(resetRequest);
    }

    @Test
    public void processExpertPasswordResetAsTransactionThrowsValidationExceptionIfNoMatchingResetRequest() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AccountControllerHelper target = new AccountControllerHelper(expertService, null, mock(EmailService.class), null);

        when(expertService.getPasswordResetRequest(7)).thenReturn(null);

        // Act
        catchException(target).processExpertPasswordResetAsTransaction("password", 7);

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
