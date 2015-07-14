package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.apache.commons.lang.math.DoubleRange;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for RangeRasterSummaryCollator.
 * Copyright (c) 2015 University of Oxford
 */
public class RangeRasterSummaryCollatorTest {
    @Test
    public void collatorGetsCorrectResult() throws IOException {
        // Arrange
        RangeRasterSummaryCollator target = new RangeRasterSummaryCollator();

        // Act
        target.addValue(7);
        target.addValue(7);
        target.addValue(5);
        target.addValue(9);
        target.addValue(2);
        target.addValue(3);
        DoubleRange result = target.getSummary();

        // Assert
        assertThat(result.getMaximumDouble()).isEqualTo(9);
        assertThat(result.getMinimumDouble()).isEqualTo(2);
    }
}
