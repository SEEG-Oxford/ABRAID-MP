package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the LandSnapper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LandSnapperTest {
    @Test
    public void locationInsideFirstLandSeaBorder() {
        // Arrange
        Location location = new Location(20, 30);
        LandSnapper snapper = new LandSnapper();

        // Act
        snapper.ensureOnLand(location, getLandSeaBorders());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 passed: location already on land.");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationInsideSecondLandSeaBorder() {
        // Arrange
        Location location = new Location(117, 115);
        LandSnapper snapper = new LandSnapper();

        // Act
        snapper.ensureOnLand(location, getLandSeaBorders());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 passed: location already on land.");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().equalsExact(location.getGeom())).isTrue();
    }

    @Test
    public void locationJustOutsideLandSeaBorders() {
        // Arrange
        Location location = new Location(100, 100.04);
        LandSnapper snapper = new LandSnapper();

        // Act
        snapper.ensureOnLand(location, getLandSeaBorders());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 passed: location (100.000000,100.040000) snapped to " +
                "land (distance 4.466km).");
        assertThat(snapper.getClosestPoint()).isNotNull();
        assertThat(snapper.getClosestPoint().getX()).isEqualTo(100);
        assertThat(snapper.getClosestPoint().getY()).isEqualTo(100);
    }

    @Test
    public void locationWellOutsideLandSeaBorders() {
        // Arrange
        Location location = new Location(150, 150);
        LandSnapper snapper = new LandSnapper();

        // Act
        snapper.ensureOnLand(location, getLandSeaBorders());

        // Assert
        assertThat(snapper.getMessage()).isEqualTo("QC stage 2 failed: location too distant from land (closest " +
                "point is (120.000000,120.000000) at distance 4015.703km).");
        assertThat(snapper.getClosestPoint()).isNull();
    }

    private MultiPolygon getLandSeaBorders() {
        Polygon bigSquare = GeometryUtils.createPolygon(5, 5, 100, 5, 100, 100, 5, 100, 5, 5);
        Polygon smallTriangle = GeometryUtils.createPolygon(110, 110, 120, 120, 120, 105, 110, 110);
        return GeometryUtils.createMultiPolygon(new Polygon[] { bigSquare, smallTriangle });
    }
}
