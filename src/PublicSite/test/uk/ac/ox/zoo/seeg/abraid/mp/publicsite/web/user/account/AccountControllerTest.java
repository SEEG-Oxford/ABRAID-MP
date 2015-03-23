package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for AccountController.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerTest {

    private static AccountController createTarget(int userId, ExpertService expertServiceIn,
                                                  DiseaseService diseaseService,
                                                  AccountControllerValidator adminControllerValidator,
                                                  AccountControllerHelper accountControllerTransactionHelper,
                                                  CurrentUserService currentUserService) {
        CurrentUserService userService;
        if (currentUserService == null) {
            userService = getCurrentUserService();
            when(userService.getCurrentUserId()).thenReturn(userId);
        } else {
            userService = currentUserService;
        }

        ExpertService expertService = expertServiceIn;
        if (expertService == null) {
            expertService = mock(ExpertService.class);
            when(expertService.getExpertById(userId)).thenReturn(mock(Expert.class));
        }

        return new AccountController(
                userService,
                expertService,
                diseaseService == null ? mock(DiseaseService.class) : diseaseService,
                new AbraidJsonObjectMapper(),
                adminControllerValidator == null ? mock(AccountControllerValidator.class) : adminControllerValidator,
                accountControllerTransactionHelper == null ? mock(AccountControllerHelper.class) : accountControllerTransactionHelper);
    }

    @Test
    public void getAccountEditPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        AccountController target = createTarget(1, null, null, null, null, null);

        // Act
        String result = target.getAccountEditPage(mock(ModelMap.class));

        // Assert
        assertThat(result).isEqualTo("account/edit");
    }

    @Test
    public void getAccountEditPageAssignsCorrectModelData() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        Expert expert = mock(Expert.class);
        when(expertService.getExpertById(anyInt())).thenReturn(expert);
        when(expert.getName()).thenReturn("name");
        when(expert.getJobTitle()).thenReturn("job");
        when(expert.getInstitution()).thenReturn("institute");
        when(expert.getVisibilityRequested()).thenReturn(true);
        when(expert.getValidatorDiseaseGroups()).thenReturn(createMockValidatorDiseaseGroups());

        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(createMockValidatorDiseaseGroups());

        AccountController target = createTarget(1, expertService, diseaseService, null, null, null);

        // Act
        ModelMap model = mock(ModelMap.class);
        target.getAccountEditPage(model);

        // Assert
        verify(model).addAttribute("diseases", "[{\"id\":1,\"name\":\"1\"},{\"id\":2,\"name\":\"2\"},{\"id\":3,\"name\":\"3\"}]");
        verify(model).addAttribute("jsonExpert", "{\"name\":\"name\",\"visibilityRequested\":true,\"diseaseInterests\":[1,2,3],\"jobTitle\":\"job\",\"institution\":\"institute\"}");
    }

    @Test
    public void submitAccountEditPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountController target = createTarget(1, null, null, validator, null, null);
        when(validator.validate(any(JsonExpertDetails.class))).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void submitAccountEditPageReturnsBadRequestIfSaveFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertProfileUpdateAsTransaction(anyInt(), any(JsonExpertDetails.class));

        // Act
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitAccountEditPageSavesExpertAndReturnsNoContentForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        int userId = 99;
        AccountController target = createTarget(userId, null, null, validator, helper, null);

        // Act
        JsonExpertDetails expert = mock(JsonExpertDetails.class);
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(expert);

        // Assert
        verify(helper).processExpertProfileUpdateAsTransaction(userId, expert);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void getChangeEmailPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getExpertById(1)).thenReturn(mock(Expert.class));
        when(expertService.getExpertById(1).getEmail()).thenReturn("expectedEmail");
        AccountController target = createTarget(1, expertService, null, null, null, null);
        ModelMap model = mock(ModelMap.class);

        // Act
        String result = target.getChangeEmailPage(model);

        // Assert
        verify(model).addAttribute("email", "expectedEmail");
        assertThat(result).isEqualTo("account/email");
    }

    @Test
    public void submitChangeEmailPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        when(validator.validateEmailChange(anyString(), anyString(), anyInt())).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangeEmailPage("email", "password");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void submitChangeEmailPageReturnsBadRequestIfSaveFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertEmailChangeAsTransaction(anyInt(), anyString());

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangeEmailPage("email", "password");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitChangeEmailPageSavesPasswordAndReturnsNoContentForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        int userId = 99;
        AccountController target = createTarget(userId, null, null, validator, helper, null);

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangeEmailPage("email", "password");

        // Assert
        verify(helper).processExpertEmailChangeAsTransaction(userId, "email");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void getChangePasswordPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        AccountController target = createTarget(1, null, null, null, null, null);

        // Act
        String result = target.getChangePasswordPage();

        // Assert
        assertThat(result).isEqualTo("account/password");
    }

    @Test
    public void submitChangePasswordPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        when(validator.validatePasswordChange(anyString(), anyString(), anyString(), anyInt())).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangePasswordPage("oldPassword", "newPassword", "confirmPassword");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void submitChangePasswordPageReturnsBadRequestIfSaveFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertPasswordChangeAsTransaction(anyInt(), anyString());

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangePasswordPage("oldPassword", "newPassword", "confirmPassword");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitChangePasswordPageSavesPasswordAndReturnsNoContentForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        int userId = 99;
        AccountController target = createTarget(userId, null, null, validator, helper, null);

        // Act
        ResponseEntity<Collection<String>> result = target.submitChangePasswordPage("oldPassword", "newPassword", "confirmPassword");

        // Assert
        verify(helper).processExpertPasswordChangeAsTransaction(userId, "newPassword");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void getPasswordResetRequestPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        AccountController target = createTarget(1, null, null, null, null, getCurrentUserService());

        // Act
        String result = target.getPasswordResetRequestPage();

        // Assert
        assertThat(result).isEqualTo("account/reset/request");
    }

    @Test
    public void getPasswordResetRequestPageRedirectsLoggedInUsers() throws Exception {
        // Arrange
        CurrentUserService userService = getCurrentUserService();
        when(userService.getCurrentUserId()).thenReturn(1);
        AccountController target = createTarget(1, null, null, null, null, userService);

        // Act
        String result = target.getPasswordResetRequestPage();

        // Assert
        assertThat(result).isEqualTo("redirect:/");
    }

    @Test
    public void submitPasswordResetRequestPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        when(validator.validateNewPasswordResetRequest(anyString())).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitPasswordResetRequestPage("email", mock(HttpServletRequest.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void submitPasswordResetRequestPageReturnsBadRequestIfHelperFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertPasswordResetRequestAsTransaction(eq("email"), anyString());

        // Act
        HttpServletRequest httpServletRequest = createHttpServletRequest("https", "abraid.domain", 443, "/", true);
        ResponseEntity<Collection<String>> result = target.submitPasswordResetRequestPage("email", httpServletRequest);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitPasswordResetRequestPageIssuesNewResetKeyForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(99, null, null, validator, helper, null);
        HttpServletRequest httpServletRequest = createHttpServletRequest("https", "abraid.domain", 443, "/", true);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPasswordResetRequestPage("email", httpServletRequest);

        // Assert
        verify(helper).processExpertPasswordResetRequestAsTransaction("email", "https://abraid.domain/");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void getPasswordResetProcessingPageReturnsCorrectTemplateForValidParameters() throws Exception {
        // Arrange
        AccountControllerValidator adminControllerValidator = mock(AccountControllerValidator.class);
        AccountController target = createTarget(1, null, null, adminControllerValidator, null, getCurrentUserService());
        when(adminControllerValidator.validatePasswordResetRequest(7, "key")).thenReturn(new ArrayList<String>());
        Model model = mock(Model.class);

        // Act
        String result = target.getPasswordResetProcessingPage(7, "key", model);

        // Assert
        verify(model).addAttribute("id", 7);
        verify(model).addAttribute("key", "key");
        assertThat(result).isEqualTo("account/reset/process");
    }

    @Test
    public void getPasswordResetProcessingPageReturnsCorrectTemplateForInvalidParameters() throws Exception {
        // Arrange
        AccountControllerValidator adminControllerValidator = mock(AccountControllerValidator.class);
        AccountController target = createTarget(1, null, null, adminControllerValidator, null, getCurrentUserService());
        List<String> failures = Arrays.asList("Failure");
        when(adminControllerValidator.validatePasswordResetRequest(7, "key")).thenReturn(failures);
        Model model = mock(Model.class);

        // Act
        String result = target.getPasswordResetProcessingPage(7, "key", model);

        // Assert
        verify(model).addAttribute("failures", failures);
        assertThat(result).isEqualTo("account/reset/invalid");
    }

    @Test public void getPasswordResetProcessingPageRedirectsLoggedInUsers() throws Exception {
        // Arrange
        CurrentUserService userService = getCurrentUserService();
        when(userService.getCurrentUserId()).thenReturn(1);
        AccountController target = createTarget(1, null, null, null, null, userService);

        // Act
        String result = target.getPasswordResetProcessingPage(7, "key", mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("redirect:/");
    }

    @Test
    public void submitPasswordResetProcessingPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        when(validator.validatePasswordResetProcessing("password", "confirm", 7, "key")).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitPasswordResetProcessingPage(7, "password", "confirm", "key");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void submitPasswordResetProcessingPageReturnsBadRequestIfHelperFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper, null);
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertPasswordResetAsTransaction("password", 7);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPasswordResetProcessingPage(7, "password", "confirm", "key");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitPasswordResetProcessingPageUpdatesPasswordForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(99, null, null, validator, helper, null);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPasswordResetProcessingPage(7, "password", "confirm", "key");

        // Assert
        verify(helper).processExpertPasswordResetAsTransaction("password", 7);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private List<ValidatorDiseaseGroup> createMockValidatorDiseaseGroups() {
        ArrayList<ValidatorDiseaseGroup> groups = new ArrayList<>();
        groups.add(createMockValidatorDiseaseGroup(1, "1"));
        groups.add(createMockValidatorDiseaseGroup(2, "2"));
        groups.add(createMockValidatorDiseaseGroup(3, "3"));
        return groups;
    }

    private ValidatorDiseaseGroup createMockValidatorDiseaseGroup(int id, String name) {
        ValidatorDiseaseGroup mock = new ValidatorDiseaseGroup(id, name);
        return mock;
    }

    private HttpServletRequest createHttpServletRequest(String schema, String host, int port, String context, boolean useXFowarded) {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        if (useXFowarded) {
            when(httpServletRequest.getScheme()).thenReturn("http");
            when(httpServletRequest.getServerName()).thenReturn("localhost");
            when(httpServletRequest.getServerPort()).thenReturn(8080);
            when(httpServletRequest.getHeader("X-Forwarded-Host")).thenReturn(host + ":" + port);
            when(httpServletRequest.getHeader("X-Forwarded-Proto")).thenReturn(schema);
        } else {
            when(httpServletRequest.getScheme()).thenReturn(schema);
            when(httpServletRequest.getServerName()).thenReturn(host);
            when(httpServletRequest.getServerPort()).thenReturn(port);
        }
        when(httpServletRequest.getContextPath()).thenReturn(context);
        return httpServletRequest;
    }

    private static CurrentUserService getCurrentUserService() {
        CurrentUserService mock = mock(CurrentUserService.class);
        when(mock.getCurrentUserId()).thenReturn(null);
        return mock;
    }
}
