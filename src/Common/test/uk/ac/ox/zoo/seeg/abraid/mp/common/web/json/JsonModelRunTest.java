package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for JsonModelRun.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunTest {
    @Test
    public void constructorForJsonModelRunBindsParametersCorrectly() throws Exception {
        // Arrange
        JsonModelDisease expectedDisease = new JsonModelDisease();
        GeoJsonDiseaseOccurrenceFeatureCollection expectedOccurrences = new GeoJsonDiseaseOccurrenceFeatureCollection();
        ArrayList<Integer> expectedExtents = new ArrayList<>();

        // Act
        JsonModelRun result = new JsonModelRun(
                expectedDisease,
                expectedOccurrences,
                expectedExtents);

        // Assert
        assertThat(result.getDisease()).isEqualTo(expectedDisease);
        assertThat(result.getOccurrences()).isEqualTo(expectedOccurrences);
        assertThat(result.getExtents()).isEqualTo(expectedExtents);
    }

    @Test
    public void isValidReturnsTrueForGoodArguments() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                new GeoJsonDiseaseOccurrenceFeatureCollection(),
                new ArrayList<Integer>());

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
                new GeoJsonDiseaseOccurrenceFeatureCollection(),
                new ArrayList<Integer>());

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isValidReturnsFalseForMissingOccurrences() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                null,
                new ArrayList<Integer>());

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isValidReturnsFalseForMissingExtents() throws Exception {
        // Arrange
        JsonModelRun target = new JsonModelRun(
                new JsonModelDisease(),
                new GeoJsonDiseaseOccurrenceFeatureCollection(),
                null);

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }
}
