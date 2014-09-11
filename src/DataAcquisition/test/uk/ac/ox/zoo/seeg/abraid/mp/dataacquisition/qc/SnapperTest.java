package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the Snapper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class SnapperTest {
    @Test
    public void throwsExceptionIfLocationHasNoGeometry() {
        // Arrange
        Location location = new Location();
        Snapper snapper = new Snapper("land", 5);

        // Act
        catchException(snapper).ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void locationInsideFirstGeometry() {
        // Arrange
        Location location = new Location(20, 30);
        Snapper snapper = new Snapper("land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location already within land");
        assertThat(snapper.hasPassed()).isTrue();
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationInsideSecondGeometry() {
        // Arrange
        Location location = new Location(117, 115);
        Snapper snapper = new Snapper("country", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location already within country");
        assertThat(snapper.hasPassed()).isTrue();
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationJustOutsideGeometries() {
        // Arrange
        Location location = new Location(100, 100.04);
        Snapper snapper = new Snapper("land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location (100.00000,100.04000) snapped to land (distance 4.466km)");
        assertThat(snapper.hasPassed()).isTrue();
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().getX()).isEqualTo(100);
        assertThat(snapper.getClosestPoint().getY()).isEqualTo(100);
    }

    @Test
    public void locationJustOutsideGeometriesWithReducedMaximumDistance() {
        // Arrange
        Location location = new Location(100, 100.04);
        Snapper snapper = new Snapper("land", 2);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location too distant from land (closest point is " +
                "(100.00000,100.00000) at distance 4.466km)");
        assertThat(snapper.hasPassed()).isFalse();
        assertThat(snapper.getClosestPoint()).isNull();
    }

    @Test
    public void locationWellOutsideGeometries() {
        // Arrange
        Location location = new Location(150, 150);
        Snapper snapper = new Snapper("land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location too distant from land (closest point is " +
                "(120.00000,120.00000) at distance 4015.703km)");
        assertThat(snapper.hasPassed()).isFalse();
        assertThat(snapper.getClosestPoint()).isNull();
    }

    @Test
    public void locationCannotBeSnapped() {
        // Arrange
        Location location = new Location(9, 9);
        Snapper snapper = new Snapper("land", 5);

        Polygon polygon = GeometryUtils.createPolygon(false, 10.000003, 10.000003, 10.000003, 10.000006, 10.000006,
                10.000006, 10.000006, 10.000003, 10.000003, 10.000003);
        MultiPolygon multiPolygon = GeometryUtils.createMultiPolygon(polygon);

        // Act
        snapper.ensureWithinGeometry(location, multiPolygon);

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("location cannot be snapped");
        assertThat(snapper.hasPassed()).isFalse();
        assertThat(snapper.getClosestPoint()).isNull();
    }

    private MultiPolygon getGeometry() {
        Polygon bigSquare = GeometryUtils.createPolygon(5, 5, 100, 5, 100, 100, 5, 100, 5, 5);
        Polygon smallTriangle = GeometryUtils.createPolygon(110, 110, 120, 120, 120, 105, 110, 110);
        return GeometryUtils.createMultiPolygon(bigSquare, smallTriangle);
    }
}
