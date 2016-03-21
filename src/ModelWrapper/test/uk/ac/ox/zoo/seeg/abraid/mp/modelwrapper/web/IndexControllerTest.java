package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ModelWrapperConfigurationService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils.captorForClass;

/**
* Tests for IndexController.
* Copyright (c) 2014 University of Oxford
*/
public class IndexControllerTest {

    @Test
    public void showIndexPageReturnsCorrectFreemarkerTemplateName() {
        // Arrange
        IndexController target = new IndexController(mock(ModelWrapperConfigurationService.class));

        // Act
        String result = target.showIndexPage(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("index");
    }

    @Test
    public void showIndexPageSetsCorrectModelData() throws Exception {
        // Arrange
        int expectedDuration = 1234;
        String expectedRPath = "foo3";

        ModelWrapperConfigurationService configurationService = mock(ModelWrapperConfigurationService.class);

        when(configurationService.getMaxModelRunDuration()).thenReturn(expectedDuration);
        when(configurationService.getRExecutablePath()).thenReturn(expectedRPath);

        Model model = mock(Model.class);
        IndexController target = new IndexController(configurationService);

        // Act
        target.showIndexPage(model);

        // Assert
        verify(model).addAttribute("r_path", expectedRPath);
        verify(model).addAttribute("run_duration", expectedDuration);
    }

    @Test
    public void showIndexPageSetsEmptyRPathIfConfigurationCheckFails() throws Exception {
        // Arrange
        ModelWrapperConfigurationService configurationService = mock(ModelWrapperConfigurationService.class);
        when(configurationService.getRExecutablePath()).thenThrow(new ConfigurationException());
        Model model = mock(Model.class);
        IndexController target = new IndexController(configurationService);

        // Act
        target.showIndexPage(model);

        // Assert
        verify(model).addAttribute("r_path", "");
    }

    @Test
    public void updateAuthenticationDetailsCallConfigurationServiceWithCorrectParams() {
        // Arrange
        ModelWrapperConfigurationService mockConfService = mock(ModelWrapperConfigurationService.class);
        IndexController target = new IndexController(mockConfService);
        String expectedPassword = "PasswordOne1";
        String expectedUser = "user";

        // Act
        ResponseEntity result = target.updateAuthenticationDetails(expectedUser, expectedPassword, expectedPassword);

        // Assert
        ArgumentCaptor<String> usernameCaptor = captorForClass(String.class);
        ArgumentCaptor<String> passwordCaptor = captorForClass(String.class);
        verify(mockConfService).setAuthenticationDetails(usernameCaptor.capture(), passwordCaptor.capture());
        assertThat(usernameCaptor.getValue()).isEqualTo(expectedUser);
        assertThat(BCrypt.checkpw(expectedPassword, passwordCaptor.getValue())).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void updateAuthenticationDetailsRejectsInvalidUserNames() {
        // Arrange
        List<String> invalidUserNames = Arrays.asList("", null, "u", "^273", "user name");
        ModelWrapperConfigurationService mockConfService = mock(ModelWrapperConfigurationService.class);
        IndexController target = new IndexController(mockConfService);

        for (String username : invalidUserNames) {
            // Act
            ResponseEntity result = target.updateAuthenticationDetails(username, "PasswordOne1", "PasswordOne1");

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateAuthenticationDetailsRejectsInvalidPassword() {
        // Arrange
        List<String> invalidPasswords = Arrays.asList("", null, "u", "^273", "user name");
        ModelWrapperConfigurationService mockConfService = mock(ModelWrapperConfigurationService.class);
        IndexController target = new IndexController(mockConfService);

        for (String password : invalidPasswords) {
            // Act
            ResponseEntity result = target.updateAuthenticationDetails("username", password, password);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateAuthenticationDetailsRejectsInvalidPasswordConfirmation() {
        // Arrange
        List<String> invalidPasswordConfirmations = Arrays.asList(null, "not_a_match");
        ModelWrapperConfigurationService mockConfService = mock(ModelWrapperConfigurationService.class);
        IndexController target = new IndexController(mockConfService);

        for (String passwordConfirmation : invalidPasswordConfirmations) {
            // Act
            ResponseEntity result = target.updateAuthenticationDetails("username", "Password1", passwordConfirmation);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
