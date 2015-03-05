package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performs integration tests for the PostQCManager class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class PostQCManagerIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private PostQCManager postQCManager;

    @Test
    public void throwsExceptionIfLocationHasNoGeometry() {
        // Arrange
        Location location = new Location();
        location.setPrecision(LocationPrecision.PRECISE);

        // Act
        catchException(postQCManager).runPostQCProcesses(location);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void throwsExceptionIfLocationHasNoPrecision() {
        // Arrange
        Location location = new Location(10, 20);

        // Act
        catchException(postQCManager).runPostQCProcesses(location);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findsAdminUnitsIfExactlyOnGeometry() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN1);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(179);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(179);
        assertThat(location.getCountryGaulCode()).isEqualTo(179);
        assertThat(location.hasPassedQc()).isTrue();
    }

    @Test
    public void findsAdminUnitsIfWithinGeometry() {
        Location location = new Location(101.7, 3.16667, LocationPrecision.COUNTRY);
        location.setHasPassedQc(true);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(153);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(153);
        assertThat(location.getCountryGaulCode()).isEqualTo(153);
        assertThat(location.hasPassedQc()).isTrue();
    }

    @Test
    public void findsAdminUnitsIfWithinGeometryWithDifferentGaulCodesForGlobalAndTropical() {
        Location britishColumbiaCanadaCentroid = new Location(-124.76033, 54.75946, LocationPrecision.ADMIN1);
        britishColumbiaCanadaCentroid.setHasPassedQc(true);
        britishColumbiaCanadaCentroid.setAdminUnitQCGaulCode(826);
        postQCManager.runPostQCProcesses(britishColumbiaCanadaCentroid);
        assertThat(britishColumbiaCanadaCentroid.getAdminUnitGlobalGaulCode()).isEqualTo(826);
        assertThat(britishColumbiaCanadaCentroid.getAdminUnitTropicalGaulCode()).isEqualTo(825);
    }

    @Test
    public void doesNotFindAdminUnitsIfOutsideGeometry() {
        Location location = new Location(-40, 50, LocationPrecision.PRECISE);
        location.setHasPassedQc(true);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isNull();
        assertThat(location.getAdminUnitTropicalGaulCode()).isNull();
        assertThat(location.hasPassedQc()).isFalse();
    }

    @Test
    public void doesNotPassQCIfPointWithinAdminUnitsAndCountryButOutsideLandSeaBorder() {
        Location location = new Location(101.123, 3.456, LocationPrecision.PRECISE);
        location.setHasPassedQc(true);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(153);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(153);
        assertThat(location.getCountryGaulCode()).isEqualTo(153);
        assertThat(location.hasPassedQc()).isFalse();
    }
}
