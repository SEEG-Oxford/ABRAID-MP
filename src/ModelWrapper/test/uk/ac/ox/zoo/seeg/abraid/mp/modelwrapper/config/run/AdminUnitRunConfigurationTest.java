package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitRunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitRunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        boolean expectedUseGlobal = true;
        String expectedAdmin1File = "foo";
        String expectedAdmin2File = "bar";
        String expectedGlobalShapeFile = "fooooo";
        String expectedTropicalShapeFile = "barrrr";

        // Act
        AdminUnitRunConfiguration result = new AdminUnitRunConfiguration(expectedUseGlobal, expectedAdmin1File, expectedAdmin2File, expectedTropicalShapeFile, expectedGlobalShapeFile);

        // Assert
        assertThat(result.getUseGlobalShapefile()).isEqualTo(expectedUseGlobal);
        assertThat(result.getAdmin1RasterFile()).isEqualTo(expectedAdmin1File);
        assertThat(result.getAdmin2RasterFile()).isEqualTo(expectedAdmin2File);
        assertThat(result.getGlobalShapeFile()).isEqualTo(expectedGlobalShapeFile);
        assertThat(result.getTropicalShapeFile()).isEqualTo(expectedTropicalShapeFile);
    }
}
