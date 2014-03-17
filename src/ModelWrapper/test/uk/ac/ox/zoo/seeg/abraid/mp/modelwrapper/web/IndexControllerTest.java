package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for IndexController.
 * Copyright (c) 2014 University of Oxford
 */
public class IndexControllerTest {
    @Test
    public void showIndexPageReturnsCorrectFreemarkerTemplateName() {
        // Arrange
        IndexController target = new IndexController(null);

        // Act
        String result = target.showIndexPage();

        // Assert
        assertThat(result).isEqualTo("index");
    }

    @Test
    public void updateAuthenticationDetailsCallConfigurationServiceWithCorrectParams() {
        // Arrange
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService);
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
        ConfigurationService mockConfService = mock(ConfigurationService.class);
        IndexController target = new IndexController(mockConfService);

        for (String password : invalidPasswords) {
            // Act
            ResponseEntity result = target.updateAuthenticationDetails("username", password, password);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
