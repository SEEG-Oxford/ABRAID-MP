package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.Map;

import static java.lang.String.format;

/**
 * Validates a HealthMapLocation.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationValidator {
    private static final String LAT_LONG_MISSING = "Missing lat/long in HealthMap location (place name \"%s\")";
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
        String errorMessage = validateLatLong();
        errorMessage = (errorMessage != null) ? errorMessage : validateCountry();
        return errorMessage;
    }

    private String validateLatLong() {
        if (location.getLat() != null || location.getLng() != null) {
            return format(LAT_LONG_MISSING, location.getPlace_name());
        }
        return null;
    }

    private String validateCountry() {
        HealthMapCountry healthMapCountry = countryMap.get(location.getCountry());
        if (healthMapCountry == null) {
            return format(COUNTRY_DOES_NOT_EXIST, location.getCountry());
        }
        return null;
    }
}
