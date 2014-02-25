package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.Map;

/**
 * Validates a HealthMapLocation.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationValidator {
    private static final String LAT_LONG_MISSING = "Missing lat/long in HealthMap location (place name \"%s\")";
    private static final String COUNTRY_MISSING = "Missing country in HealthMap location (place name \"%s\")";
    private static final String COUNTRY_DOES_NOT_EXIST = "HealthMap country \"%s\" does not exist in ABRAID database";

    private HealthMapLocation location;
    private Map<String, HealthMapCountry> countryMap;

    public HealthMapLocationValidator(HealthMapLocation location, Map<String, HealthMapCountry> countryMap) {
        this.location = location;
        this.countryMap = countryMap;
    }

    /**
     * Validate the location.
     * @return An error message if invalid, or null if valid.
     */
    public String validate() {
        String errorMessage = validateLatLongMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateCountryMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateCountryDoesNotExist();
        return errorMessage;
    }

    private String validateLatLongMissing() {
        if (location.getLat() == null || location.getLng() == null) {
            return String.format(LAT_LONG_MISSING, location.getPlaceName());
        }
        return null;
    }

    private String validateCountryMissing() {
        if (!StringUtils.hasText(location.getCountry())) {
            return String.format(COUNTRY_MISSING, location.getPlaceName());
        }
        return null;
    }

    private String validateCountryDoesNotExist() {
        HealthMapCountry healthMapCountry = countryMap.get(location.getCountry());
        if (healthMapCountry == null) {
            return String.format(COUNTRY_DOES_NOT_EXIST, location.getCountry());
        }
        return null;
    }
}
