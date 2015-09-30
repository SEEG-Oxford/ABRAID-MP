package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the JsonModelRunResponse class.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunResponseTest {
    @Test
    public void constructorForJsonModelRunBindsParametersCorrectly() {
        // Arrange
        String errorText = "Error text";

        // Act
        JsonModelRunResponse response = new JsonModelRunResponse(errorText);

        // Assert
        assertThat(response.getErrorText()).isEqualTo(errorText);
    }
}
