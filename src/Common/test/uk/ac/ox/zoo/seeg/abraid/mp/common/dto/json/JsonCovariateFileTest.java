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
        int id = 123;
        String name = "name";
        String info = "info";
        boolean hide = true;
        boolean discrete = false;
        List<Integer> enabled = new ArrayList<>();
        List<JsonCovariateSubFile> subFiles = new ArrayList<>();

        //Act
        JsonCovariateFile result = new JsonCovariateFile(id, subFiles, name, info, hide, discrete, enabled);

        // Assert
        assertThat(result.getSubFiles()).isEqualTo(subFiles);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getInfo()).isEqualTo(info);
        assertThat(result.getHide()).isEqualTo(hide);
        assertThat(result.getDiscrete()).isEqualTo(discrete);
        assertThat(result.getEnabled()).isEqualTo(enabled);
    }

}
