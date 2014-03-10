package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for IndexController.
 * Copyright (c) 2014 University of Oxford
 */
public class IndexControllerTest {
    @Test
    public void showIndexPageReturnsCorrectFreemarkerTemplateName() {
        // Arrange
        IndexController target = new IndexController();

        // Act
        String result = target.showIndexPage();

        // Assert
        assertThat(result).isEqualTo("index");
    }
}
