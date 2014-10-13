package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for JsonDiseaseModelRunLayerSetTest.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseModelRunLayerSetTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        JsonModelRunLayer[] runs = new JsonModelRunLayer[] {mock(JsonModelRunLayer.class), mock(JsonModelRunLayer.class)};
        String disease = "expectedDiseases";

        // Act
        JsonDiseaseModelRunLayerSet result = new JsonDiseaseModelRunLayerSet(disease, Arrays.asList(runs));

        // Assert
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getRuns()).containsOnly(runs);
    }
}
