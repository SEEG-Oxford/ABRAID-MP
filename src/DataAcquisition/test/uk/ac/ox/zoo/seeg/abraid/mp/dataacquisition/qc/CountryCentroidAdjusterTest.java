package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CountryCentroidAdjuster class.
 * Copyright (c) 2014 University of Oxford
 */
public class CountryCentroidAdjusterTest {
    private Map<Integer, HealthMapCountry> countryMap;

    @Before
    public void setUp() {
        countryMap = new HashMap<>();
        countryMap.put(1, new HealthMapCountry(1, "Country 1"));
        countryMap.put(2, new HealthMapCountry(2, "Country 2", 60, 70));
    }

    @Test
    public void adjustCountryCentroidDoesNotAdjustNonCountryLocations() {
        // Arrange
        Location location = new Location(10, 20, LocationPrecision.ADMIN1, 2);
        actAndAssertThatPointUnchanged(location);
    }

    @Test
    public void adjustCountryCentroidDoesNotAdjustLocationWithoutHealthMapCountryId() {
        // Arrange
        Location location = new Location(10, 20, LocationPrecision.COUNTRY);
        actAndAssertThatPointUnchanged(location);
    }

    @Test
    public void adjustCountryCentroidDoesNotAdjustLocationWithMissingHealthMapCountry() {
        // Arrange
        Location location = new Location(10, 20, LocationPrecision.COUNTRY, 3);
        actAndAssertThatPointUnchanged(location);
    }

    @Test
    public void adjustCountryCentroidDoesNotAdjustLocationWithNoCentroidOverride() {
        // Arrange
        Location location = new Location(10, 20, LocationPrecision.COUNTRY, 1);
        actAndAssertThatPointUnchanged(location);
    }

    @Test
    public void adjustCountryCentroidAdjustsCountryLocationWithCentroidOverride() {
        // Arrange
        Location location = new Location(10, 20, LocationPrecision.COUNTRY, 2);

        // Act
        CountryCentroidAdjuster adjuster = new CountryCentroidAdjuster();
        boolean hasBeenAdjusted = adjuster.adjustCountryCentroid(location, countryMap);

        // Assert
        assertThat(location.getGeom().getX()).isEqualTo(60);
        assertThat(location.getGeom().getY()).isEqualTo(70);
        assertThat(adjuster.getMessage()).isEqualTo("location (10.00000,20.00000) replaced with fixed country " +
                "centroid (60.00000,70.00000)");
        assertThat(hasBeenAdjusted).isTrue();
    }

    private void actAndAssertThatPointUnchanged(Location location) {
        Point originalPoint = location.getGeom();

        // Act
        CountryCentroidAdjuster adjuster = new CountryCentroidAdjuster();
        boolean hasBeenAdjusted = adjuster.adjustCountryCentroid(location, countryMap);

        // Assert
        assertThat(location.getGeom()).isSameAs(originalPoint);
        assertThat(adjuster.getMessage()).isNull();
        assertThat(hasBeenAdjusted).isFalse();
    }
}
