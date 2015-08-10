package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonApiDateRange.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiDateRangeTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        DateTime start = new DateTime("2015-01-01");
        DateTime end = new DateTime("2014-01-01");

        // Act
        JsonApiDateRange dto = new JsonApiDateRange(start, end);

        // Assert
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }
}
