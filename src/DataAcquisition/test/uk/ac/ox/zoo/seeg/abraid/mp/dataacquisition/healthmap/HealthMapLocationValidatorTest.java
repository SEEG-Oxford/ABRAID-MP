package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapLocationValidator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationValidatorTest {
    @Test
    public void locationIsValid() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude("10");
        location.setLongitude("20");
        location.setCountryId("1");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isNull();
    }

    @Test
    public void latitudeIsMissing() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLongitude("20");
        location.setCountryId("1");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("Missing lat/long in HealthMap location (place name \"Test Place Name\")");
    }

    @Test
    public void longitudeIsMissing() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude("20");
        location.setCountryId("1");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("Missing lat/long in HealthMap location (place name \"Test Place Name\")");
    }

    @Test
    public void latitudeAndLongitudeAreNull() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude(null);
        location.setLongitude("");
        location.setCountryId("1");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("Missing lat/long in HealthMap location (place name \"Test Place Name\")");
    }

    @Test
    public void countryIdIsNull() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude("10");
        location.setLongitude("20");
        location.setCountryId(null);
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("Missing country ID in HealthMap location (place name \"Test Place Name\")");
    }

    @Test
    public void countryIdIsZero() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude("10");
        location.setLongitude("20");
        location.setCountryId("0");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("Missing country ID in HealthMap location (place name \"Test Place Name\")");
    }

    @Test
    public void countryDoesNotExist() {
        // Arrange
        HealthMapLocation location = new HealthMapLocation();
        location.setLatitude("10");
        location.setLongitude("20");
        location.setCountryId("1000");
        location.setCountry("New Country");
        location.setPlaceName("Test Place Name");

        // Act
        HealthMapLocationValidator validator = new HealthMapLocationValidator(location, getCountryMap());
        String message = validator.validate();

        // Assert
        assertThat(message).isEqualTo("HealthMap country \"New Country\" (ID 1000) does not exist in ABRAID database" +
                " (place name \"Test Place Name\")");
    }

    private Map<Integer, HealthMapCountry> getCountryMap() {
        Country country1 = new Country(167, "Mongolia");
        Country country2 = new Country(212, "Samoa");
        HealthMapCountry healthMapCountry1 = new HealthMapCountry(1, "Mongolia", country1);
        HealthMapCountry healthMapCountry2 = new HealthMapCountry(2, "Samoa", country2);

        Map<Integer, HealthMapCountry> countryMap = new HashMap<>();
        countryMap.put(1, healthMapCountry1);
        countryMap.put(2, healthMapCountry2);

        return countryMap;
    }
}
