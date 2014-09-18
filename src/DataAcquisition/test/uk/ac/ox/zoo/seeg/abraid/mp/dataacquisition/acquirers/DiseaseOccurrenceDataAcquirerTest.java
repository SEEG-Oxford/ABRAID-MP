package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.PostQCManager;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.QCManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceDataAcquirer class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceDataAcquirerTest {
    private LocationService locationService;
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private QCManager qcManager;
    private PostQCManager postQcManager;
    private DiseaseOccurrenceDataAcquirer acquirer;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        locationService = mock(LocationService.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        qcManager = mock(QCManager.class);
        postQcManager = mock(PostQCManager.class);
        acquirer = new DiseaseOccurrenceDataAcquirer(diseaseService, locationService,
                diseaseOccurrenceValidationService, qcManager, postQcManager);
    }

    @Test
    public void acquireDoesNotSaveIfOccurrenceAlreadyExists() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        locationIsKnownToAlreadyExist(occurrence);
        mockDiseaseOccurrenceAlreadyExists(occurrence, true);

        // Act
        boolean result = acquirer.acquire(occurrence);

        // Assert
        assertThat(result).isFalse();
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
    }

    @Test
    public void acquireSavesUsingExistingLocationIfLocationAlreadyExists() {
        // Arrange
        DiseaseOccurrence occurrence = createDefaultDiseaseOccurrence();
        Location existingLocation = new Location();
        mockGetLocationsByPointAndPrecision(occurrence.getLocation().getGeom(), occurrence.getLocation().getPrecision(),
                Arrays.asList(existingLocation));

        // Act
        boolean result = acquirer.acquire(occurrence);

        // Assert
        assertThat(occurrence.getLocation()).isSameAs(existingLocation);
        assertThat(occurrence.getLocation().hasPassedQc()).isFalse();
        verify(qcManager, never()).performQC(any(Location.class));
        verify(postQcManager, never()).runPostQCProcesses(any(Location.class));
        verifySuccessfulSave(occurrence, result);
    }

    @Test
    public void acquireSavesUsingExistingLocationIfLocationAlreadyExistsMultipleTimes() {
        // Arrange
        DiseaseOccurrence occurrence = createDefaultDiseaseOccurrence();
        Location existingLocation1 = new Location();
        Location existingLocation2 = new Location();
        mockGetLocationsByPointAndPrecision(occurrence.getLocation().getGeom(), occurrence.getLocation().getPrecision(),
                Arrays.asList(existingLocation1, existingLocation2));

        // Act
        boolean result = acquirer.acquire(occurrence);

        // Assert
        assertThat(occurrence.getLocation()).isSameAs(existingLocation1);
        assertThat(occurrence.getLocation().hasPassedQc()).isFalse();
        verify(qcManager, never()).performQC(any(Location.class));
        verify(postQcManager, never()).runPostQCProcesses(any(Location.class));
        verifySuccessfulSave(occurrence, result);
    }

    @Test
    public void acquireSavesUsingExistingLocationIfLocationAlreadyExistsAfterPerformingQC() {
        // Arrange
        DiseaseOccurrence occurrence = createDefaultDiseaseOccurrence();
        Location currentLocation = occurrence.getLocation();
        Location existingLocation = new Location();

        Point pointAfterQc = GeometryUtils.createPoint(50, 60);
        mockGetLocationsByPointAndPrecision(currentLocation.getGeom(), currentLocation.getPrecision(),
                new ArrayList<Location>());
        mockGetLocationsByPointAndPrecision(pointAfterQc, currentLocation.getPrecision(),
                Arrays.asList(existingLocation));
        mockRunQCWithPointChange(currentLocation, pointAfterQc, true);

        // Act
        boolean result = acquirer.acquire(occurrence);

        // Assert
        assertThat(occurrence.getLocation()).isSameAs(existingLocation);
        verify(qcManager, times(1)).performQC(same(currentLocation));
        verify(postQcManager, times(1)).runPostQCProcesses(same(currentLocation));
        verifySuccessfulSave(occurrence, result);
    }

    @Test
    public void acquireSavesIfLocationIsNew() {
        // Arrange
        DiseaseOccurrence occurrence = createDefaultDiseaseOccurrence();
        mockGetLocationsByPointAndPrecision(occurrence.getLocation().getGeom(), occurrence.getLocation().getPrecision(),
                new ArrayList<Location>());
        mockRunQC(occurrence.getLocation(), true);

        // Act
        boolean result = acquirer.acquire(occurrence);

        // Assert
        assertThat(occurrence.getLocation().hasPassedQc()).isTrue();
        verify(postQcManager, times(1)).runPostQCProcesses(same(occurrence.getLocation()));
        verifySuccessfulSave(occurrence, result);
    }

    private DiseaseOccurrence createDefaultDiseaseOccurrence() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        Location location = new Location(20, 10, LocationPrecision.ADMIN1);
        occurrence.setLocation(location);
        return occurrence;
    }

    private void locationIsKnownToAlreadyExist(DiseaseOccurrence occurrence) {
        occurrence.setLocation(new Location(1));
        verify(locationService, never()).getLocationsByPointAndPrecision(any(Point.class),
                any(LocationPrecision.class));
    }

    private void mockDiseaseOccurrenceAlreadyExists(DiseaseOccurrence occurrence, boolean response) {
        when(diseaseService.doesDiseaseOccurrenceExist(same(occurrence))).thenReturn(response);
    }

    private void mockGetLocationsByPointAndPrecision(Point point, LocationPrecision precision,
                                                     List<Location> existingLocations) {
        when(locationService.getLocationsByPointAndPrecision(argThat(new PointMatcher(point)),
                eq(precision))).thenReturn(existingLocations);
    }

    private void mockRunQC(Location location, boolean result) {
        when(qcManager.performQC(same(location))).thenReturn(result);
    }

    private void mockRunQCWithPointChange(final Location location, final Point pointAfterQc,
                                          final boolean result) {
        doAnswer(new Answer() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                location.setGeom(pointAfterQc);
                return result;
            }
        }).when(qcManager).performQC(same(location));
    }

    private void verifySuccessfulSave(DiseaseOccurrence occurrence, boolean result) {
        verify(diseaseOccurrenceValidationService, times(1)).addValidationParametersWithChecks(same(occurrence));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(same(occurrence));
        assertThat(result).isTrue();
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
            if (expected == actual) {
                return true;
            }
            if (expected == null || actual == null || actual.getClass() != Point.class) {
                return false;
            }
            return expected.equalsExact((Point) actual);
        }
    }
}
