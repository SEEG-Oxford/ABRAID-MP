package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonCovariateSubFile.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateSubFileTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        int id = 123;
        String path = "path";
        String qualifier = "qualifier";

        // Act
        JsonCovariateSubFile result = new JsonCovariateSubFile(id, path, qualifier);

        // Assert
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getPath()).isEqualTo(path);
        assertThat(result.getQualifier()).isEqualTo(qualifier);
    }
}
