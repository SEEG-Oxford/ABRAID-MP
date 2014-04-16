package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for MiscController.
 * Copyright (c) 2014 University of Oxford
 */
public class MiscControllerTest {
    @Test
    public void updateRExecutablePathRejectsInvalidArguments() throws Exception {
        // Arrange
        List<String> invalidValues = Arrays.asList(null, "");
        MiscController target = new MiscController(mock(ConfigurationService.class));

        for (String value : invalidValues) {
            // Act
            ResponseEntity result = target.updateRExecutablePath(value);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateRExecutablePathSavesValueIfDifferentToExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenReturn("not"+value);

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setRExecutablePath(value);
    }

    @Test
    public void updateRExecutablePathDoesNotSaveValueIfSameAsExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenReturn(value);

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setRExecutablePath(value);
    }

    @Test
    public void updateRExecutablePathAlwaysSavesValueIfInvalidExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenThrow(new ConfigurationException());

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setRExecutablePath(value);
    }

    @Test
    public void updateMaxRunDurationSavesValueIfDifferentToExistingValue() throws Exception {
        // Arrange
        int value = 1;
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getMaxModelRunDuration()).thenReturn(-value);

        // Act
        ResponseEntity result = target.updateMaxRunDuration(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setMaxModelRunDuration(value);
    }

    @Test
    public void updateMaxRunDurationDoesNotSaveValueIfSameAsExistingValue() throws Exception {
        // Arrange
        int value = 1;
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getMaxModelRunDuration()).thenReturn(value);

        // Act
        ResponseEntity result = target.updateMaxRunDuration(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setMaxModelRunDuration(value);
    }
}
