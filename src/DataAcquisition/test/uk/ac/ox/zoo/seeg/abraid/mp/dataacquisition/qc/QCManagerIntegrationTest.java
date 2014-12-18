package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performs integration tests for the QCManager class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCManagerIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private QCManager qcManager;

    @Test
    public void stage1NotRunWhenLocationPrecisionIsCountryAndStages2And3Pass() {
        // Arrange
        int japanId = 156;
        Location location = new Location("Japan", 138.47861, 36.09854, LocationPrecision.COUNTRY, japanId);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isTrue();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC " +
                "stage 2 passed: location already within land. QC stage 3 passed: location already within " +
                "country.");
    }

    @Test
    public void stage1NotRunWhenLocationPrecisionIsPreciseAndStage2Fails() {
        // Arrange
        Location location = new Location("Somewhere in the North Sea", 3.524163, 56.051420, LocationPrecision.PRECISE);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC stage " +
                "2 failed: location too distant from land (closest point is (4.91660,53.29162) at distance " +
                "320.061km).");
    }

    @Test
    public void stage2OverridesCountryCentroidIfNecessary() {
        // Arrange
        int philippinesId = 158;
        Location location = new Location("Philippines", 122.86711, 11.73469, LocationPrecision.COUNTRY, philippinesId);

        // Act
        qcManager.performQC(location);

        // Assert
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).contains("QC stage 2 passed: location (122.86711,11.73469) replaced with " +
                "fixed country centroid (120.81897,15.37497).");
    }

    @Test
    public void passesStage1AndStage2AndStage3() {
        // Arrange
        int mexicoId = 14;
        Location location = new Location("Estado de México, Mexico", -99.4922, 19.3318, LocationPrecision.ADMIN1,
                mexicoId);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isTrue();
        assertThat(location.getAdminUnitQCGaulCode()).isEqualTo(1006355);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 10.92% of the square " +
                "root of the area. QC stage 2 passed: location already within land. QC stage 3 passed: location " +
                "already within country.");
    }

    @Test
    public void failsStage1() {
        // Arrange
        int vietnamId = 152;
        Location location = new Location("Huyện Cai Lậy, Tiền Giang, Vietnam", 108.69807, 7.90055,
                LocationPrecision.ADMIN2, vietnamId);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 failed: closest distance is 2841.01% of the square " +
                "root of the area (GAUL code 1002305: \"Con Dao\").");
    }

    @Test
    public void passesStage1ButFailsStage2() {
        // Arrange
        int indonesiaId = 184;
        Location location = new Location("Central Sulawesi, Indonesia", 121, -1, LocationPrecision.ADMIN1, indonesiaId);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isEqualTo(1013690);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 9.01% of the square " +
                "root of the area. QC stage 2 failed: location too distant from land (closest point is " +
                "(121.20822,-1.16670) at distance 29.611km).");
    }

    @Test
    public void passesStage2BySnappingAPointInALake() {
        // Arrange
        int indonesiaId = 184;
        Location location = new Location("Central Sulawesi, Indonesia", 116.367, -0.270, LocationPrecision.PRECISE,
                indonesiaId);

        // Act
        qcManager.performQC(location);

        // Assert
        assertThat(location.getQcMessage()).contains("QC stage 2 passed: " +
                "location (116.36700,-0.27000) snapped to land (distance 2.209km).");
    }

    @Test
    public void passesStage1AndStage2ButFailsStage3() {
        // Arrange
        int id = 106;
        Location location = new Location("Pointe-Noire, DR Congo", 11.86364, -4.77867,
                LocationPrecision.PRECISE, id);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).contains("QC stage 1 passed: location not an ADMIN1 or ADMIN2. " +
                "QC stage 2 passed: location already within land. QC stage 3 failed: location too distant from " +
                "country");
    }

    @Test
    public void failsStage3IfNoHealthMapCountryOrCountryGaulCodeSpecified() {
        // Arrange
        Location location = new Location("Door County, Wisconsin, United States", -87.3001, 44.91666,
                LocationPrecision.ADMIN2);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isEqualTo(31738);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 8.76% of the square " +
                "root of the area. QC stage 2 passed: location already within land. QC stage 3 failed: no country " +
                "geometries associated with this location.");
    }

    @Test
    public void passesStage3IfHealthMapCountryHasNoGeometries() {
        // Arrange
        int maldivesId = 143;
        Location location = new Location("Maldives", 73.46564, 5.84270, LocationPrecision.COUNTRY, maldivesId);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).contains("QC stage 3 failed: no country geometries associated with " +
                "this location.");
    }

    @Test
    public void passesStage3WithCountryGaulCodeIfPointIsWithinGeometry() {
        // Arrange
        Location location = new Location("Japan", 138.47861, 36.09854, LocationPrecision.COUNTRY);
        location.setCountryGaulCode(126);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isTrue();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC " +
                "stage 2 passed: location already within land. QC stage 3 passed: location already within country.");
    }

    @Test
    public void failsStage3WithCountryGaulCodeIfPointIsOutsideGeometry() {
        // Arrange
        Location location = new Location("Pointe-Noire, DR Congo", 11.86364, -4.77867,
                LocationPrecision.PRECISE);
        location.setCountryGaulCode(259002);

        // Act
        boolean hasPassedQc = qcManager.performQC(location);

        // Assert
        assertThat(hasPassedQc).isFalse();
        assertThat(location.getAdminUnitQCGaulCode()).isNull();
        assertThat(location.getQcMessage()).contains("QC stage 1 passed: location not an ADMIN1 or ADMIN2. " +
                "QC stage 2 passed: location already within land. QC stage 3 failed: location too distant from " +
                "country");
    }
}
