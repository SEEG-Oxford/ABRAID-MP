package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the AdminUnitFinder class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitFinderTest {
    @Test
    public void findsNoAdminUnitIfNoneProvided() {
        // Arrange
        Location location = new Location("Hampshire", -1.25, 51, LocationPrecision.ADMIN2);
        List<AdminUnitQC> adminUnits = new ArrayList<>();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isNull();
        assertThat(adminUnitFinder.getMessage()).isNull();
    }

    @Test
    public void throwsExceptionIfLocationNotAnAdmin1OrAdmin2() {
        // Arrange
        Location location = new Location("United States", -97.5561, 39.96693, LocationPrecision.COUNTRY);
        List<AdminUnitQC> adminUnits = createAdminUnits();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        catchException(adminUnitFinder).findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void throwsExceptionIfLocationHasNoGeometry() {
        // Arrange
        Location location = new Location();
        location.setPrecision(LocationPrecision.ADMIN2);
        List<AdminUnitQC> adminUnits = createAdminUnits();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        catchException(adminUnitFinder).findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findsClosestAdminUnitIfSingleAdminUnitInList() {
        // Arrange
        Location location = new Location("Hampshire", -1.25, 51, LocationPrecision.ADMIN2);
        AdminUnitQC hampshire = new AdminUnitQC(40112, '2', "Hampshire", -1.28383, 51.06529, 3747.35320108);
        List<AdminUnitQC> adminUnits = Arrays.asList(hampshire);
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isSameAs(hampshire);
        assertThat(adminUnitFinder.getMessage()).isEqualTo("QC stage 1 passed: closest distance is 12.48% of the " +
                "square root of the area.");
    }

    @Test
    public void findsNoAdminUnitIfSingleAdminUnitInListIsNotCloseEnough() {
        // Arrange
        Location location = new Location("Hampshire", -1.25, 51, LocationPrecision.ADMIN2);
        AdminUnitQC berkshire = new AdminUnitQC(40096, '2', "Berkshire", -1.07313, 51.44574, 1222.48602795);
        List<AdminUnitQC> adminUnits = Arrays.asList(berkshire);
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isNull();
        assertThat(adminUnitFinder.getMessage()).isEqualTo("QC stage 1 failed: closest distance is 146.17% of the " +
                "square root of the area (GAUL code 40096: \"Berkshire\").");
    }

    @Test
    public void findsClosestAdminUnitForAnAdmin2() {
        // Arrange
        Location location = new Location("Hampshire", -1.25, 51, LocationPrecision.ADMIN2);
        List<AdminUnitQC> adminUnits = createAdminUnits();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isNotNull();
        assertThat(adminUnitFinder.getClosestAdminUnit().getName()).isEqualTo("Hampshire");
        assertThat(adminUnitFinder.getMessage()).isEqualTo("QC stage 1 passed: closest distance is 12.48% of the " +
                "square root of the area.");
    }

    @Test
    public void findsClosestAdminUnitForAnAdmin1() {
        // Arrange
        Location location = new Location("West Virginia, United States", -80.5079, 38.5137, LocationPrecision.ADMIN1);
        List<AdminUnitQC> adminUnits = createAdminUnits();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isNotNull();
        assertThat(adminUnitFinder.getClosestAdminUnit().getName()).isEqualTo("West Virginia");
        assertThat(adminUnitFinder.getMessage()).isEqualTo("QC stage 1 passed: closest distance is 6.79% of the " +
                "square root of the area.");
    }

    @Test
    public void findsNoAdminUnitIfNoneCloseEnough() {
        // Arrange
        Location location = new Location("Oxfordshire", -1.25, 51.833333, LocationPrecision.ADMIN2);
        List<AdminUnitQC> adminUnits = createAdminUnits();
        AdminUnitFinder adminUnitFinder = new AdminUnitFinder();

        // Act
        adminUnitFinder.findClosestAdminUnit(location, adminUnits);

        // Assert
        assertThat(adminUnitFinder.getClosestAdminUnit()).isNull();
        assertThat(adminUnitFinder.getMessage()).isEqualTo("QC stage 1 failed: closest distance is 128.21% of the " +
                "square root of the area (GAUL code 40096: \"Berkshire\").");
    }

    private List<AdminUnitQC> createAdminUnits() {
        // Five US states and five English counties
        return Arrays.asList(
                new AdminUnitQC(3227, '1', "Illinois", -89.19754, 40.06572, 146172.656559),
                new AdminUnitQC(3260, '1', "Virginia", -78.83583, 37.51577, 104188.680259),
                new AdminUnitQC(3261, '1', "Washington", -120.43996, 47.38034, 174921.174796),
                new AdminUnitQC(3262, '1', "West Virginia", -80.61382, 38.64252, 62845.4130703),
                new AdminUnitQC(3263, '1', "Wisconsin", -90.00856, 44.63645, 145538.410907),
                new AdminUnitQC(40096, '2', "Berkshire", -1.07313, 51.44574, 1222.48602795),
                new AdminUnitQC(40112, '2', "Hampshire", -1.28383, 51.06529, 3747.35320108),
                new AdminUnitQC(40116, '2', "Isle of Wight", -1.30334, 50.67887, 373.855675662),
                new AdminUnitQC(40137, '2', "West Sussex", -0.48444, 50.95776, 1945.02141568),
                new AdminUnitQC(40139, '2', "Wiltshire", -1.92657, 51.31608, 3428.74012362));
    }
}
