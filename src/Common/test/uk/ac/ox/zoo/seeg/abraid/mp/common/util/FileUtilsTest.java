package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the FileUtils class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class FileUtilsTest {
    @Test
    public void loadFileIntoStringLoadsFileCorrectly() throws IOException {
        // Arrange
        String expectedText = "This is a UTF-8 test file for use with FileUtilsTest. Some special characters: 华夏经纬";
        String fileName = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/util/file_utils_test.txt";

        // Act
        String actualText = FileUtils.loadFileIntoString(fileName, StandardCharsets.UTF_8);

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test(expected = IOException.class)
    public void loadFileIntoStringThrowsExceptionIfFileNotFound() throws IOException {
        // Arrange
        String fileName = "does_not_exist.txt";

        // Act
        FileUtils.loadFileIntoString(fileName, StandardCharsets.UTF_8);

        // Asserted exception is in the @Test annotation - cannot use catchException() for static methods
    }
}
