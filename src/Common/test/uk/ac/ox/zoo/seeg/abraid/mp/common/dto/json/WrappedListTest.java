package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for WrappedList.
 * Copyright (c) 2014 University of Oxford
 */
public class WrappedListTest {
    @Test
    public void constructorBindsFieldsCorrectly() throws Exception {
        // Arrange
        List<String> expectation = Arrays.asList("a", "b", "c");

        // Act
        WrappedList<String> result = new WrappedList<>(expectation);

        // Assert
        assertThat(result.getList()).isSameAs(expectation);
    }
}
