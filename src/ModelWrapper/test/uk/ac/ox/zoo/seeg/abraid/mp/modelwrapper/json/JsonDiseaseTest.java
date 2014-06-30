package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonDisease.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        String name = "name";
        int id = 4;

        // Act
        JsonDisease result = new JsonDisease(id, name);

        // Assert
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    public void isValidForCorrectInputs() {
        // Arrange
        String name = "name";
        int id = 4;

        // Act
        JsonDisease result = new JsonDisease(id, name);

        // Assert
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void isNotValidForMissingName() {
        // Arrange
        String name = "";
        int id = 4;

        // Act
        JsonDisease result = new JsonDisease(id, name);

        // Assert
        assertThat(result.isValid()).isFalse();
    }

}
