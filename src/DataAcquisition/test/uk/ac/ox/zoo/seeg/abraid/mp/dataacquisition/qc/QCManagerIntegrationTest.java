package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.Main;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Performs integration tests for the QCManager class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Main.APPLICATION_CONTEXT_LOCATION)
@Transactional
public class QCManagerIntegrationTest {
    @Autowired
    private QCManager qcManager;

    @Test
    public void stage1NotRunWhenLocationPrecisionIsCountry() {
        // Arrange
        Location location = new Location("Japan", 138.47861, 36.09854, LocationPrecision.COUNTRY);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(1);
        assertThat(location.getAdminUnit()).isNull();
        assertThat(location.getQcMessage()).isNull();
    }

    @Test
    public void stage1NotRunWhenLocationPrecisionIsPrecise() {
        // Arrange
        Location location = new Location("Los Angeles, California, United States", -118.2428, 34.0522,
                LocationPrecision.PRECISE);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(1);
        assertThat(location.getAdminUnit()).isNull();
        assertThat(location.getQcMessage()).isNull();
    }

    @Test
    public void passesStage1() {
        // Arrange
        Location location = new Location("Estado de México, Mexico", -99.4922, 19.3318, LocationPrecision.ADMIN1);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(1);
        assertThat(location.getAdminUnit()).isNotNull();
        assertThat(location.getAdminUnit().getGaulCode()).isEqualTo(1006355);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 10.92% of the square " +
                "root of the area.");
    }

    @Test
    public void failsStage1() {
        // Arrange
        Location location = new Location("Huyện Cai Lậy, Tiền Giang, Vietnam", 108.69807, 7.90055,
                LocationPrecision.ADMIN2);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(0);
        assertThat(location.getAdminUnit()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 failed: closest distance is 2841.01% of the square " +
                "root of the area (GAUL code 1002305: \"Con Dao\").");
    }
}
