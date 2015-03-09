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

    @Autowired
    private QCLookupData qcLookupData;

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
    public void setsCorrectResolutionWeightingOnFailedQC() {
        Location location = new Location(176, -39, LocationPrecision.PRECISE);
        location.setHasPassedQc(false);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isNull();
    }

    @Test
    public void setsCorrectResolutionWeightingOnPrecise() {
        Location location = new Location(176, -39, LocationPrecision.PRECISE);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(1);
    }

    @Test
    public void setsCorrectResolutionWeightingOnAdmin2() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN2);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(16707);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(0.5195439219762943);
    }

    @Test
    public void setsCorrectResolutionWeightingOnAdmin1() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN1);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(1340);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(0.463076531714097);
    }

    @Test
    public void setsCorrectResolutionWeightingOnCountry() {
        qcLookupData.getCountryMap().get(179).setArea(100000);
        Location location = new Location(176, -39, LocationPrecision.COUNTRY);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(0.016571622570456777);
    }

    @Test
    public void setsCorrectResolutionWeightingOnSmallArea() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN2);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(1);
    }

    @Test
    public void setsCorrectResolutionWeightingOnLargeArea() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN1);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(16728);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getResolutionWeighting()).isEqualTo(0);
    }

    @Test
    public void setsCorrectModelEligibilityOnFailedQC() {
        Location location = new Location(176, -39, LocationPrecision.PRECISE);
        location.setHasPassedQc(false);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isFalse();
    }

    @Test
    public void setsCorrectModelEligibilityOnPrecise() {
        Location location = new Location(176, -39, LocationPrecision.PRECISE);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isTrue();
    }

    @Test
    public void setsCorrectModelEligibilityOnAdmin2() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN2);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(16707);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isTrue();
    }

    @Test
    public void setsCorrectModelEligibilityOnAdmin1() {
        Location location = new Location(176, -39, LocationPrecision.ADMIN1);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(1340);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isTrue();
    }

    @Test
    public void setsCorrectModelEligibilityOnSmallCountry() {
        qcLookupData.getCountryMap().get(179).setArea(114000);
        Location location = new Location(176, -39, LocationPrecision.COUNTRY);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isTrue();
    }

    @Test
    public void setsCorrectModelEligibilityOnLargeCountry() {
        qcLookupData.getCountryMap().get(179).setArea(1156000);
        Location location = new Location(176, -39, LocationPrecision.COUNTRY);
        location.setHasPassedQc(true);
        location.setAdminUnitQCGaulCode(61186);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.isModelEligible()).isFalse();
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
