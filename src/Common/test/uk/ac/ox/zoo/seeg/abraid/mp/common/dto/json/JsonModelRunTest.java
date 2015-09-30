package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonModelRun.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunTest {
    @Test
    public void constructorForJsonModelRunBindsParametersCorrectly() throws Exception {
        // Arrange
        JsonModelDisease expectedDisease = new JsonModelDisease();
        String expectedName = "name123";

        // Act
        JsonModelRun result = new JsonModelRun(
                expectedDisease,
                expectedName
        );

        // Assert
        assertThat(result.getDisease()).isEqualTo(expectedDisease);
        assertThat(result.getRunName()).isEqualTo(expectedName);
    }

    @Test
    public void isValidReturnsTrueForGoodArguments() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                "name123");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void isValidReturnsFalseForMissingDisease() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                null,
                "name123");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isValidReturnsFalseForMissingName() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                "");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isValidReturnsFalseForNullName() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                null);

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }
}
