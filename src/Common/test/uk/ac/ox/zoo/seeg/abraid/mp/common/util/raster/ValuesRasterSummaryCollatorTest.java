package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ValuesRasterSummaryCollator.
 * Copyright (c) 2015 University of Oxford
 */
public class ValuesRasterSummaryCollatorTest {
    @Test
    public void collatorGetsCorrectResult() throws IOException {
        // Arrange
        ValuesRasterSummaryCollator target = new ValuesRasterSummaryCollator();

        // Act
        target.addValue(7);
        target.addValue(7);
        target.addValue(5);
        target.addValue(9);
        target.addValue(2);
        target.addValue(3);
        Collection<Double> result = target.getSummary();

        // Assert
        assertThat(result).containsOnly(7d, 5d, 9d, 2d, 3d);
    }
}
