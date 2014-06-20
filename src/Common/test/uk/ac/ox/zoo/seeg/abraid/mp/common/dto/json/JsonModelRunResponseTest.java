package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the JsonModelRunResponse class.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunResponseTest {
    @Test
    public void constructorForJsonModelRunBindsParametersCorrectly() {
        // Arrange
        String modelRunName = "Model run name";
        String errorText = "Error text";

        // Act
        JsonModelRunResponse response = new JsonModelRunResponse(modelRunName, errorText);

        // Assert
        assertThat(response.getModelRunName()).isEqualTo(modelRunName);
        assertThat(response.getErrorText()).isEqualTo(errorText);
    }
}
