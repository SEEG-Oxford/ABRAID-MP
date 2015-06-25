package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonCovariateFile.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateFileTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        String path = "path";
        String name = "name";
        String info = "info";
        boolean hide = true;
        List<Integer> enabled = new ArrayList<>();

        // Act
        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);

        // Assert
        assertThat(result.getPath()).isEqualTo(path);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getInfo()).isEqualTo(info);
        assertThat(result.getHide()).isEqualTo(hide);
        assertThat(result.getEnabled()).isEqualTo(enabled);
    }

//    @Test
//    public void isValidForCorrectInputs() {
//        // Arrange
//        String path = "path";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = new ArrayList<>();
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isTrue();
//    }
//
//    @Test
//    public void isNotValidForMissingPath() {
//        // Arrange
//        String path = "";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = new ArrayList<>();
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForDuplicateEnabledId() {
//        // Arrange
//        String path = "path";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = Arrays.asList(1, 1);
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
}
