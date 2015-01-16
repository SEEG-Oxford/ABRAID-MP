package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the WaterBodiesMaskRasterFileLocator class.
 * Copyright (c) 2014 University of Oxford
 */
public class WaterBodiesMaskRasterFileLocatorTest {
    @Test
    public void getFileReturnsTheCorrectFile() throws Exception {
        // Arrange
        File expectation = mock(File.class);
        WaterBodiesMaskRasterFileLocator target = new WaterBodiesMaskRasterFileLocator(expectation);

        // Act
        File result = target.getFile();

        // Assert
        assertThat(result).isEqualTo(expectation);
    }
}
