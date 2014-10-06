package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the Atlas controller.
 * Copyright (c) 2014 University of Oxford
 */
public class AtlasControllerTest {

    @Test
    public void showPageReturnsAtlasPage() {
        // Arrange
        AtlasController target = new AtlasController(null, null, null);

        // Act
        String result = target.showPage();

        // Assert
        assertThat(result).isEqualTo("atlas");
    }

}
