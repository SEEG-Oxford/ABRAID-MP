package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the DistanceFromDiseaseExtentHelper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DistanceFromDiseaseExtentHelperTest {
    private NativeSQL nativeSQL;
    private DistanceFromDiseaseExtentHelper helper;

    @Before
    public void setUp() {
        nativeSQL = mock(NativeSQL.class);
        helper = new DistanceFromDiseaseExtentHelper(nativeSQL);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenLocationIsOutsideTheExtentReturnsDistanceOutside() {
        // Arrange
        int diseaseGroupId = 87;
        Point point = GeometryUtils.createPoint(10, 10);
        double expectedDistance = 25;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false, point);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(eq(diseaseGroupId), eqExact(point))).thenReturn(expectedDistance);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
        verifyFindDistanceOutsideDiseaseExtent(1);
        verifyFindDistanceWithinDiseaseExtent(0);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenLocationIsWithinTheExtentReturnsDistanceWithin() {
        // Arrange
        int diseaseGroupId = 64;
        Point point = GeometryUtils.createPoint(50, 50);
        double expectedDistance = 20;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, true, point);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(eq(diseaseGroupId), eqExact(point))).thenReturn(0.0);
        when(nativeSQL.findDistanceWithinDiseaseExtent(eq(diseaseGroupId), eq(true), eqExact(point))).thenReturn(expectedDistance);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
        verifyFindDistanceOutsideDiseaseExtent(1);
        verifyFindDistanceWithinDiseaseExtent(1);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenCalculationOutsideReturnsNull() {
        // Arrange
        int diseaseGroupId = 87;
        Point point = GeometryUtils.createPoint(10, 10);
        double expectedDistance = 10;

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false, point);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(eq(diseaseGroupId), eqExact(point))).thenReturn(null);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isNull();
        verifyFindDistanceOutsideDiseaseExtent(1);
        verifyFindDistanceWithinDiseaseExtent(0);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenCalculationInsideReturnsNull() {
        // Arrange
        int diseaseGroupId = 87;
        Point point = GeometryUtils.createPoint(10, 10);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, false, point);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(eq(diseaseGroupId), eqExact(point))).thenReturn(0.0);
        when(nativeSQL.findDistanceWithinDiseaseExtent(eq(diseaseGroupId), eq(false), eqExact(point))).thenReturn(null);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isNull();
        verifyFindDistanceOutsideDiseaseExtent(1);
        verifyFindDistanceWithinDiseaseExtent(1);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, boolean isGlobal, Point point) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(isGlobal);
        Location location = new Location();
        location.setGeom(point);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        return occurrence;
    }

    private void verifyFindDistanceOutsideDiseaseExtent(int times) {
        verify(nativeSQL, times(times)).findDistanceOutsideDiseaseExtent(anyInt(), any(Point.class));
    }

    private void verifyFindDistanceWithinDiseaseExtent(int times) {
        verify(nativeSQL, times(times)).findDistanceWithinDiseaseExtent(anyInt(), anyBoolean(), any(Point.class));
    }

    private Point eqExact(Point point) {
        return argThat(new PointMatcher(point));
    }

    /**
     * Uses Point.equalsExact() instead of Point.equals() (the latter seems unreliable).
     */
    static class PointMatcher extends ArgumentMatcher<Point> {
        private final Point expected;

        public PointMatcher(Point expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object actual) {
            return expected.equalsExact((Point) actual);
        }
    }
}
