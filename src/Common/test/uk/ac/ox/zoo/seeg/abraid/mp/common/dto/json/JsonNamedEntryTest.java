package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonNamedEntry.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonNamedEntryTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Integer id = 123;
        String name = "ABC";

        // Act
        JsonNamedEntry json = new JsonNamedEntry(id, name);

        // Assert
        assertThat(json.getId()).isEqualTo(id);
        assertThat(json.getName()).isEqualTo(name);
    }
}
