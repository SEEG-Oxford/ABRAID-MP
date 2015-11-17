package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for JsonHealthMapDisease.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonHealthMapDiseaseTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Integer id = 123;
        String name = "ABC";
        JsonNamedEntry abraidDisease = mock(JsonNamedEntry.class);

        // Act
        JsonHealthMapDisease json = new JsonHealthMapDisease(id, name, abraidDisease);

        // Assert
        assertThat(json.getId()).isEqualTo(id);
        assertThat(json.getName()).isEqualTo(name);
        assertThat(json.getAbraidDisease()).isEqualTo(abraidDisease);
    }
}
