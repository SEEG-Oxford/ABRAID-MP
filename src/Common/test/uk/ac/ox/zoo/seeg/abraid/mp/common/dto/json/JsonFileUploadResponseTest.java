package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonFileUploadResponse.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonFileUploadResponseTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        String expectedStatus = "SUCCESS";
        List<String> expectedMessages = Arrays.asList("1", "2", "3");

        // Act
        JsonFileUploadResponse result = new JsonFileUploadResponse(expectedStatus.equals("SUCCESS"), expectedMessages);

        // Assert
        assertThat(result.getStatus()).isEqualTo(expectedStatus);
        assertThat(result.getMessages()).isEqualTo(expectedMessages);
    }
}