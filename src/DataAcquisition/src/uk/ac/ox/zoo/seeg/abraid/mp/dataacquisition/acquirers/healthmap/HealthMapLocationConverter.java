package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.GeoNamesWebService;

/**
 * Converts a HealthMap location into an ABRAID location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationConverter {
    private static final Logger LOGGER = Logger.getLogger(HealthMapLocationConverter.class);
    private static final String IGNORING_COUNTRY_MESSAGE =
            "Ignoring HealthMap location in country \"%s\" as it is not of interest";
    private static final String GEONAMES_FCODE_NOT_IN_DATABASE_MESSAGE =
            "Feature code \"%s\" is not in the ABRAID database (GeoName ID %d) - attempting to use place_basic_type";
    private static final String GEONAMES_ID_NOT_FOUND_MESSAGE =
            "GeoNames ID %d was not found by the GeoNames web service - attempting to use place_basic_type";
    private static final String PLACE_BASIC_TYPE_NOT_FOUND_MESSAGE =
            "place_basic_type is missing - ignoring location (place name \"%s\")";
    private static final String GEONAMES_ID_HAS_NO_FEATURE_CODE_MESSAGE =
            "GeoNames ID %d was returned by the GeoNames web service but has no feature code";

    private LocationService locationService;
    private HealthMapLookupData lookupData;
    private GeoNamesWebService geoNamesWebService;

    public HealthMapLocationConverter(LocationService locationService, HealthMapLookupData lookupData,
                                      GeoNamesWebService geoNamesWebService) {
        this.locationService = locationService;
        this.lookupData = lookupData;
        this.geoNamesWebService = geoNamesWebService;
    }

    /**
     * Converts a HealthMap location into an ABRAID location.
     * @param healthMapLocation The HealthMap location.
     * @return An ABRAID location, or null if the HealthMap location could not be converted.
     */
    public Location convert(HealthMapLocation healthMapLocation) {
        Location location = null;

        if (validate(healthMapLocation)) {
            location = createLocation(healthMapLocation);
        }

        return location;
    }

    private boolean validate(HealthMapLocation healthMapLocation) {
        String validationMessage =
                new HealthMapLocationValidator(healthMapLocation, lookupData.getCountryMap()).validate();
        if (validationMessage != null) {
            LOGGER.warn(validationMessage);
            return false;
        }
        return true;
    }

    private Location createLocation(HealthMapLocation healthMapLocation) {
        Location location = null;

        HealthMapCountry healthMapCountry = lookupData.getCountryMap().get(healthMapLocation.getCountryId());
        if (CollectionUtils.isEmpty(healthMapCountry.getCountries())) {
            LOGGER.warn(String.format(IGNORING_COUNTRY_MESSAGE, healthMapCountry.getName()));
        } else {
            location = new Location();
            location.setHealthMapCountryId(healthMapCountry.getId());
            location.setGeom(healthMapLocation.getLongitude(), healthMapLocation.getLatitude());
            location.setName(healthMapLocation.getPlaceName());
            location.setGeoNameId(healthMapLocation.getGeoNameId());
            if (!addPrecision(healthMapLocation, location)) {
                // If precision could not be added, return null (i.e. location could not be converted)
                return null;
            }
        }

        return location;
    }

    private boolean addPrecision(HealthMapLocation healthMapLocation, Location location) {
        Integer geoNameId = healthMapLocation.getGeoNameId();

        if (geoNameId != null) {
            addPrecisionUsingGeoNames(location, geoNameId);
        }

        if (location.getPrecision() == null) {
            // The precision could not be added using GeoNames for whatever reason. So use the "rough" location
            // precision supplied in HealthMap's place_basic_type field.
            addPrecisionUsingHealthMapPlaceBasicType(location, healthMapLocation);
        }

        return (location.getPrecision() != null);
    }

    private void addPrecisionUsingGeoNames(Location location, int geoNameId) {
        GeoName geoName = getGeoName(geoNameId);

        if (geoName != null) {
            String featureCode = geoName.getFeatureCode();
            LocationPrecision precision = lookupData.getGeoNamesMap().get(featureCode);
            if (precision == null) {
                // We retrieved the GeoName, but the feature code is not in our mapping table
                LOGGER.warn(String.format(GEONAMES_FCODE_NOT_IN_DATABASE_MESSAGE, featureCode, geoNameId));
            }

            location.setPrecision(precision);
        }
    }

    private void addPrecisionUsingHealthMapPlaceBasicType(Location location, HealthMapLocation healthMapLocation) {
        LocationPrecision precision = findLocationPrecision(healthMapLocation);
        if (precision != null) {
            location.setPrecision(precision);
        } else {
            LOGGER.warn(String.format(PLACE_BASIC_TYPE_NOT_FOUND_MESSAGE, healthMapLocation.getPlaceName()));
        }
    }

    private LocationPrecision findLocationPrecision(HealthMapLocation healthMapLocation) {
        // HealthMap does not always set place_basic_type to "c" for countries. So firstly, return COUNTRY if the place
        // name is the name of a HealthMap country.
        if (isHealthMapCountryName(healthMapLocation.getPlaceName())) {
            return LocationPrecision.COUNTRY;
        }

        // Otherwise map the HealthMap place_basic_type field on to our LocationPrecision enumeration
        return LocationPrecision.findByHealthMapPlaceBasicType(healthMapLocation.getPlaceBasicType());
    }

    private boolean isHealthMapCountryName(String name) {
        if (StringUtils.hasText(name)) {
            for (HealthMapCountry country : lookupData.getCountryMap().values()) {
                if (name.equals(country.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    private GeoName getGeoName(int geoNameId) {
        // Determine whether we already have this GeoName in our database
        GeoName geoName = locationService.getGeoNameById(geoNameId);

        if (geoName == null) {
            // We do not, so look it up using the GeoNames web service
            uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.domain.GeoName geoNameDTO =
                    geoNamesWebService.getById(geoNameId);

            if (geoNameDTO != null) {
                if (StringUtils.hasText(geoNameDTO.getFeatureCode())) {
                    geoName = createAndSaveGeoName(geoNameDTO);
                } else {
                    LOGGER.warn(String.format(GEONAMES_ID_HAS_NO_FEATURE_CODE_MESSAGE, geoNameId));
                }
            } else {
                LOGGER.warn(String.format(GEONAMES_ID_NOT_FOUND_MESSAGE, geoNameId));
            }
        }

        return geoName;
    }

    private GeoName createAndSaveGeoName(
            uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.domain.GeoName geoNameDTO) {
        GeoName geoName = new GeoName(geoNameDTO.getGeoNameId(), geoNameDTO.getFeatureCode());
        locationService.saveGeoName(geoName);
        return geoName;
    }
}
