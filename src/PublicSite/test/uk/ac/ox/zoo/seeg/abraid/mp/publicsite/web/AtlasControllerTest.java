package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Test;
import org.springframework.ui.Model;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the Atlas controller.
 * Copyright (c) 2014 University of Oxford
 */
public class AtlasControllerTest {

    @Test
    public void showPageReturnsAtlasPage() {
        // Arrange
        Model model = mock(Model.class);
        AtlasController target = new AtlasController();

        // Act
        String result = target.showPage();

        // Assert
        assertThat(result).isEqualTo("atlas");
    }

}
