package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonCovariateConfiguration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfigurationTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.getDiseases()).isEqualTo(expectedDiseases);
        assertThat(result.getFiles()).isEqualTo(expectedFiles);
    }

    @Test
    public void isValidForCorrectInputs() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void isNotValidForMissingDiseases() {
        // Arrange
        List<JsonDisease> expectedDiseases = null;
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForMissingFiles() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        List<JsonCovariateFile> expectedFiles = null;

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForInvalidDiseases() {
        // Arrange
        JsonDisease mockDisease = mock(JsonDisease.class);
        when(mockDisease.isValid()).thenReturn(false);
        List<JsonDisease> expectedDiseases = Arrays.asList(mockDisease);
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForInvalidFiles() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
        when(mockFile.isValid()).thenReturn(false);
        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile);

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForDuplicateDiseases() {
        // Arrange
        JsonDisease mockDisease = mock(JsonDisease.class);
        when(mockDisease.getId()).thenReturn(1);
        List<JsonDisease> expectedDiseases = Arrays.asList(mockDisease, mockDisease);
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForDuplicateFiles() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
        when(mockFile.getPath()).thenReturn("foo");
        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile, mockFile);

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void isNotValidForBrokenDiseaseIdReferences() {
        // Arrange
        List<JsonDisease> expectedDiseases = new ArrayList<>();
        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
        when(mockFile.getEnabled()).thenReturn(Arrays.asList(1));
        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile);

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.isValid()).isFalse();
    }
}
