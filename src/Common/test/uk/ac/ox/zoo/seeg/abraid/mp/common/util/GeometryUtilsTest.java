package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;

/**
 * Tests the GeometryUtils class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeometryUtilsTest {
    private static final double MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET = 0.000005;
    private static final double MAXIMUM_SPHERICAL_DISTANCE_OFFSET = 0.05;

    @Test
    public void createPointHasCorrectPrecisionAndSRID() {
        Point point = GeometryUtils.createPoint(-1.2824895, 51.6743376);
        assertThat(point.getX()).isEqualTo(-1.28249);
        assertThat(point.getY()).isEqualTo(51.67434);
        assertThat(point.getSRID()).isEqualTo(4326);
    }

    @Test
    public void findOrthodromicDistanceForTypicalParameters() {
        // Arrange
        // We compare the results with PostGIS's function ST_DISTANCE applied to geography objects, i.e.
        // ST_DISTANCE(GEOGRAPHY(ST_SetSRID(ST_Point(-1.28383, 51.06529), 4326)),
        //             GEOGRAPHY(ST_SetSRID(ST_Point(-1.25, 51), 4326)))
        Point point1 = GeometryUtils.createPoint(-1.28383, 51.06529);
        Point point2 = GeometryUtils.createPoint(-1.25, 51);
        double expectedDistance = 7.641287209854;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);
        double distanceReversed = GeometryUtils.findOrthodromicDistance(point2, point1);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET));
        assertThat(distanceReversed).isEqualTo(expectedDistance, offset(MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET));
    }

    @Test
    public void findOrthodromicDistanceForZeroParameters() {
        Point point = GeometryUtils.createPoint(0, 0);
        double distance = GeometryUtils.findOrthodromicDistance(point, point);
        assertThat(distance).isEqualTo(0);
    }

    @Test
    public void findOrthodromicDistanceBetweenPoles() {
        // Arrange
        Point point1 = GeometryUtils.createPoint(0, 90);
        Point point2 = GeometryUtils.createPoint(0, -90);
        double expectedDistance = 20003.9314586236;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET));
    }

    @Test
    public void findOrthodromicDistanceAtEquator() {
        // Arrange
        Point point1 = GeometryUtils.createPoint(0, 0);
        Point point2 = GeometryUtils.createPoint(100, 0);
        double expectedDistance = 11131.9490779206;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET));
    }

    @Test
    public void findOrthodromicDistanceBetweenIdenticalPoints() {
        Point point = GeometryUtils.createPoint(15, 15);
        double distance = GeometryUtils.findOrthodromicDistance(point, point);
        assertThat(distance).isEqualTo(0);
    }

    @Test
    public void findOrthodromicDistanceWithoutGeodeticCalculatorConvergence() {
        // This test fails when GeometryUtils.createPoint uses GeodeticCalculator.getOrthodromicDistance()
        // (rather than the currently-used DefaultEllipsoid.WGS84.orthodromicDistance()).

        // Arrange
        Point point1 = GeometryUtils.createPoint(120.566670, 22.733330);
        Point point2 = GeometryUtils.createPoint(-59.935130, -20);
        double expectedDistance = 19697.5564126086;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_ORTHODROMIC_DISTANCE_OFFSET));
    }

    @Test
    public void findOrthodromicDistanceWithoutConvergence1() {
        // This test fails to converge using Vincenty's method so falls back to the Haversine method.
        //
        // To compare it with PostGIS's ST_DISTANCE function, we have to set use_spheroid to FALSE i.e.
        // ST_DISTANCE(GEOGRAPHY(ST_SetSRID(ST_Point(101.686530, 3.143090), 4326)),
        //             GEOGRAPHY(ST_SetSRID(ST_Point(-78.10016, -2.7031), 4326)), FALSE)
        //
        // This is despite PostGIS also apparently using Vincenty's method and falling back to Haversine
        // (see lwspheroid.c's function "spheroid_distance" in the PostGIS source code).
        //
        // Spherical distance is more subject to error, so the maximum offset used below is much higher than for
        // ellipsoid distance. Also note that the difference in X points is close to 180 degrees where error
        // is more likely.

        // Arrange
        Point point1 = GeometryUtils.createPoint(101.686530, 3.143090);
        Point point2 = GeometryUtils.createPoint(-78.10016, -2.7031);
        double expectedDistance = 19960.7566965142;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_SPHERICAL_DISTANCE_OFFSET));
    }

    @Test
    public void findOrthodromicDistanceWithoutConvergence2() {
        // See comment for findOrthodromicDistanceWithoutConvergence1

        // Arrange
        Point point1 = GeometryUtils.createPoint(-53.1, -26.3);
        Point point2 = GeometryUtils.createPoint(126.953860, 26.566770);
        double expectedDistance = 19984.9699692572;

        // Act
        double distance = GeometryUtils.findOrthodromicDistance(point1, point2);

        // Assert
        assertThat(distance).isEqualTo(expectedDistance, offset(MAXIMUM_SPHERICAL_DISTANCE_OFFSET));
    }
}
