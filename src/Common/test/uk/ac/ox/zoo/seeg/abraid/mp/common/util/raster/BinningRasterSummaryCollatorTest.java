package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.apache.commons.lang.math.DoubleRange;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for BinningRasterSummaryCollator.
 * Copyright (c) 2015 University of Oxford
 */
public class BinningRasterSummaryCollatorTest {
    @Test
    public void collatorGetsCorrectResult() throws IOException {
        // Arrange
        DoubleRange lowBin = new DoubleRange(0, 3.5);
        DoubleRange middleBin = new DoubleRange(3.5, 7);
        DoubleRange highBin = new DoubleRange(7, 10);
        BinningRasterSummaryCollator target = new BinningRasterSummaryCollator(Arrays.asList(
                lowBin, middleBin, highBin
        ));

        // Act
        target.addValue(7);
        target.addValue(7);
        target.addValue(5);
        target.addValue(9);
        target.addValue(2);
        target.addValue(3);
        Map<DoubleRange, Integer> result = target.getSummary();

        // Assert
        assertThat(result.get(lowBin)).isEqualTo(2);
        assertThat(result.get(middleBin)).isEqualTo(3);
        assertThat(result.get(highBin)).isEqualTo(1);
        assertThat(result).hasSize(3);
    }

    @Test
    public void collatorGetsCorrectResultForZeroWidthBins() throws IOException {
        // Arrange
        DoubleRange bin7 = new DoubleRange(7, 7);
        DoubleRange bin5 = new DoubleRange(5, 5);
        DoubleRange bin9 = new DoubleRange(9, 9);
        DoubleRange bin3 = new DoubleRange(3, 3);
        DoubleRange bin2 = new DoubleRange(2, 2);
        BinningRasterSummaryCollator target = new BinningRasterSummaryCollator(Arrays.asList(
                bin7, bin5, bin9, bin3, bin2
        ));

        // Act
        target.addValue(7);
        target.addValue(7);
        target.addValue(5);
        target.addValue(9);
        target.addValue(2);
        target.addValue(3);
        Map<DoubleRange, Integer> result = target.getSummary();

        // Assert
        assertThat(result.get(bin7)).isEqualTo(2);
        assertThat(result.get(bin5)).isEqualTo(1);
        assertThat(result.get(bin9)).isEqualTo(1);
        assertThat(result.get(bin3)).isEqualTo(1);
        assertThat(result.get(bin2)).isEqualTo(1);
        assertThat(result).hasSize(5);
    }

    @Test
    public void collatorThrowsIfValueOutsideAllBins() throws IOException {
        // Arrange
        BinningRasterSummaryCollator target = new BinningRasterSummaryCollator(Arrays.asList(
                new DoubleRange(0, 10)
        ));

        // Act
        catchException(target).addValue(11);

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }
}
