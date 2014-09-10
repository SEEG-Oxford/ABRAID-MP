package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.util.Map;

/**
 * Validates a HealthMapLocation.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationValidator {
    private static final String LAT_LONG_MISSING = "Missing lat/long in HealthMap location (place name \"%s\")";
    private static final String COUNTRY_MISSING = "Missing country ID in HealthMap location (place name \"%s\")";
    private static final String COUNTRY_DOES_NOT_EXIST =
            "HealthMap country \"%s\" (ID %d) does not exist in ABRAID database (place name \"%s\")";

    private HealthMapLocation location;
    private Map<Integer, HealthMapCountry> countryMap;

    public HealthMapLocationValidator(HealthMapLocation location, Map<Integer, HealthMapCountry> countryMap) {
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
        if (location.getLatitude() == null || location.getLongitude() == null) {
            return String.format(LAT_LONG_MISSING, location.getPlaceName());
        }
        return null;
    }

    private String validateCountryMissing() {
        if (location.getCountryId() == null || location.getCountryId() == 0) {
            return String.format(COUNTRY_MISSING, location.getPlaceName());
        }
        return null;
    }

    private String validateCountryDoesNotExist() {
        HealthMapCountry healthMapCountry = countryMap.get(location.getCountryId());
        if (healthMapCountry == null) {
            return String.format(COUNTRY_DOES_NOT_EXIST, location.getCountry(), location.getCountryId(),
                    location.getPlaceName());
        }
        return null;
    }
}
