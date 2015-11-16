package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for JsonHealthMapSubDisease.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonHealthMapSubDiseaseTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Integer id = 123;
        String name = "ABC";
        JsonNamedEntry abraidDisease = mock(JsonNamedEntry.class);
        JsonNamedEntry parent = mock(JsonNamedEntry.class);

        // Act
        JsonHealthMapSubDisease json = new JsonHealthMapSubDisease(id, name, abraidDisease, parent);

        // Assert
        assertThat(json.getId()).isEqualTo(id);
        assertThat(json.getName()).isEqualTo(name);
        assertThat(json.getAbraidDisease()).isEqualTo(abraidDisease);
        assertThat(json.getParent()).isEqualTo(parent);
    }
}
