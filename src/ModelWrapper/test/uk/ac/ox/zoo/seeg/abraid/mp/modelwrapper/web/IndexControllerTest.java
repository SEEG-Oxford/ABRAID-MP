package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for IndexController.
 * Copyright (c) 2014 University of Oxford
 */
public class IndexControllerTest {

    @Test
    public void showIndexPageReturnsCorrectFreemarkerTemplateName() {
        // Arrange
        IndexController target = new IndexController(mock(ConfigurationService.class), mock(SourceCodeManager.class));

        // Act
        String result = target.showIndexPage(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("index");
    }

    @Test
    public void showIndexPageSetsCorrectModelData() throws Exception {
        // Arrange
        String expectedUrl = "foo1";
        String expectedVersion = "foo2";
        List<String> expectedVersions = Arrays.asList("1", "2", "3");
        int expectedDuration = 1234;
        String expectedRPath = "foo3";
        String expectedCovariateDir = "man";

        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);

        when(configurationService.getModelRepositoryUrl()).thenReturn(expectedUrl);
        when(configurationService.getModelRepositoryVersion()).thenReturn(expectedVersion);
        when(sourceCodeManager.getAvailableVersions()).thenReturn(expectedVersions);
        when(configurationService.getMaxModelRunDuration()).thenReturn(expectedDuration);
        when(configurationService.getRExecutablePath()).thenReturn(expectedRPath);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);

        Model model = mock(Model.class);
        IndexController target = new IndexController(configurationService, sourceCodeManager);

        // Act
        target.showIndexPage(model);

        // Assert
        verify(model, times(1)).addAttribute("repository_url", expectedUrl);
        verify(model, times(1)).addAttribute("model_version", expectedVersion);
        verify(model, times(1)).addAttribute("available_versions", expectedVersions);
        verify(model, times(1)).addAttribute("r_path", expectedRPath);
        verify(model, times(1)).addAttribute("run_duration", expectedDuration);
        verify(model, times(1)).addAttribute("covariate_directory", expectedCovariateDir);
    }

    @Test
    public void showIndexPageSetsEmptyVersionListIfRepositoryCheckFails() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        when(sourceCodeManager.getAvailableVersions()).thenThrow(new IOException());
        Model model = mock(Model.class);
        IndexController target = new IndexController(mock(ConfigurationService.class), sourceCodeManager);

        // Act
        target.showIndexPage(model);

        // Assert
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model, times(1)).addAttribute(eq("available_versions"), captor.capture());
        assertThat(captor.getValue()).hasSize(0);
    }

    @Test
    public void showIndexPageSetsEmptyRPathIfConfigurationCheckFails() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getRExecutablePath()).thenThrow(new ConfigurationException());
        Model model = mock(Model.class);
        IndexController target = new IndexController(configurationService, mock(SourceCodeManager.class));

        // Act
        target.showIndexPage(model);

        // Assert
        verify(model, times(1)).addAttribute("r_path", "");
    }

    @Test
    public void updateAuthenticationDetailsCallConfigurationServiceWithCorrectParams() {
        // Arrange
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService, null);
        String expectedPassword = "PasswordOne1";
        String expectedUser = "user";

        // Act
        ResponseEntity result = target.updateAuthenticationDetails(expectedUser, expectedPassword, expectedPassword);

        // Assert
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConfService, times(1)).setAuthenticationDetails(usernameCaptor.capture(), passwordCaptor.capture());
        assertThat(usernameCaptor.getValue()).isEqualTo(expectedUser);
        assertThat(BCrypt.checkpw(expectedPassword, passwordCaptor.getValue())).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void updateAuthenticationDetailsRejectsInvalidUserNames() {
        // Arrange
        List<String> invalidUserNames = Arrays.asList("", null, "u", "^273", "user name");
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService, null);

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
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService, null);

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
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService, null);

        for (String passwordConfirmation : invalidPasswordConfirmations) {
            // Act
            ResponseEntity result = target.updateAuthenticationDetails("username", "Password1", passwordConfirmation);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
