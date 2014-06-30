package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the GeoJsonCoordinate class.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonCoordinateTest {
    @Test
    public void setValuesAcceptsAListWithTwoValues() {
        // Arrange
        double expectedLongitude = 1.0;
        double expectedLatitude = 2.0;
        List<Double> values = Arrays.asList(expectedLongitude, expectedLatitude);

        // Act
        GeoJsonCoordinate coordinate = new GeoJsonCoordinate(values);

        // Assert
        assertThat(coordinate.getLongitude()).isEqualTo(expectedLongitude);
        assertThat(coordinate.getLatitude()).isEqualTo(expectedLatitude);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValuesThrowsExceptionForListWithMoreThanTwoValues() {
        // Arrange
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);

        // Act
        new GeoJsonCoordinate(values);

        // Asserted exception is in the @Test annotation - cannot use catchException() for static methods
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValuesThrowsExceptionForListWithFewerThanTwoValues() {
        // Arrange
        List<Double> values = Arrays.asList(1.0);

        // Act
        new GeoJsonCoordinate(values);

        // Asserted exception is in the @Test annotation - cannot use catchException() for static methods
    }
}
