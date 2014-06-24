package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for JsonModelDisease.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelDiseaseTest {
    @Test
    public void constructorForJsonModelDiseaseBindsParametersCorrectly() throws Exception {
        // Arrange
        int expectedId = 64;
        boolean expectedGlobal = true;
        String expectedName = "foo";
        String expectedAbbreviation = "f";

        // Act
        JsonModelDisease result = new JsonModelDisease(expectedId, expectedGlobal, expectedName, expectedAbbreviation);

        // Assert
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.isGlobal()).isEqualTo(expectedGlobal);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getAbbreviation()).isEqualTo(expectedAbbreviation);
    }

    @Test
    public void diseaseGroupConstructorForJsonModelDiseaseBindsParametersCorrectly() throws Exception {
        // Arrange
        int expectedId = 64;
        boolean expectedGlobal = true;
        String expectedName = "foo";
        String expectedAbbreviation = "f";
        DiseaseGroup diseaseGroup = new DiseaseGroup(expectedId);
        diseaseGroup.setGlobal(expectedGlobal);
        diseaseGroup.setName(expectedName);
        diseaseGroup.setAbbreviation(expectedAbbreviation);

        // Act
        JsonModelDisease result = new JsonModelDisease(diseaseGroup);

        // Assert
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.isGlobal()).isEqualTo(expectedGlobal);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getAbbreviation()).isEqualTo(expectedAbbreviation);
    }

    @Test
    public void isValidReturnsTrueForGoodArguments() throws Exception {
        // Arrange
        JsonModelDisease target = new JsonModelDisease(1, true, "e", "f");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void isValidReturnsFalseForMissingName() throws Exception {
        // Arrange
        JsonModelDisease target = new JsonModelDisease(1, true, "", "f");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isValidReturnsFalseForMissingAbbreviation() throws Exception {
        // Arrange
        JsonModelDisease target = new JsonModelDisease(1, true, "e", "");

        // Act
        boolean result = target.isValid();

        // Assert
        assertThat(result).isFalse();
    }
}
