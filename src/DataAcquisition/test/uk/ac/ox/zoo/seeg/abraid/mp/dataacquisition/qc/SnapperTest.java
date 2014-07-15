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
        Snapper snapper = new Snapper(2, "land", 5);

        // Act
        catchException(snapper).ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void locationInsideFirstGeometry() {
        // Arrange
        Location location = new Location(20, 30);
        Snapper snapper = new Snapper(2, "land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 passed: location already within land.");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationInsideSecondGeometry() {
        // Arrange
        Location location = new Location(117, 115);
        Snapper snapper = new Snapper(3, "HealthMap country", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 3 passed: location already within HealthMap country.");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationJustOutsideGeometries() {
        // Arrange
        Location location = new Location(100, 100.04);
        Snapper snapper = new Snapper(2, "land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 passed: location (100.000000,100.040000) snapped to " +
                "land (distance 4.466km).");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().getX()).isEqualTo(100);
        assertThat(snapper.getClosestPoint().getY()).isEqualTo(100);
    }

    @Test
    public void locationJustOutsideGeometriesWithReducedMaximumDistance() {
        // Arrange
        Location location = new Location(100, 100.04);
        Snapper snapper = new Snapper(2, "land", 2);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 failed: location too distant from land (closest " +
                "point is (100.000000,100.000000) at distance 4.466km).");
        assertThat(snapper.getClosestPoint()).isNull();
    }

    @Test
    public void locationWellOutsideGeometries() {
        // Arrange
        Location location = new Location(150, 150);
        Snapper snapper = new Snapper(2, "land", 5);

        // Act
        snapper.ensureWithinGeometry(location, getGeometry());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 failed: location too distant from land (closest " +
                "point is (120.000000,120.000000) at distance 4015.703km).");
        assertThat(snapper.getClosestPoint()).isNull();
    }

    @Test
    public void locationCannotBeSnapped() {
        // Arrange
        Location location = new Location(9, 9);
        Snapper snapper = new Snapper(2, "land", 5);

        Polygon polygon = GeometryUtils.createPolygon(false, 10.000003, 10.000003, 10.000003, 10.000006, 10.000006,
                10.000006, 10.000006, 10.000003, 10.000003, 10.000003);
        MultiPolygon multiPolygon = GeometryUtils.createMultiPolygon(polygon);

        // Act
        snapper.ensureWithinGeometry(location, multiPolygon);

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 failed: location cannot be snapped.");
        assertThat(snapper.getClosestPoint()).isNull();
    }

    private MultiPolygon getGeometry() {
        Polygon bigSquare = GeometryUtils.createPolygon(5, 5, 100, 5, 100, 100, 5, 100, 5, 5);
        Polygon smallTriangle = GeometryUtils.createPolygon(110, 110, 120, 120, 120, 105, 110, 110);
        return GeometryUtils.createMultiPolygon(bigSquare, smallTriangle);
    }
}
