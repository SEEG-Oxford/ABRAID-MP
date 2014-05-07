package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.GeoNamesWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.List;

/**
 * Converts a HealthMap location into an ABRAID location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationConverter {
    private static final Logger LOGGER = Logger.getLogger(HealthMapLocationConverter.class);
    private static final String MULTIPLE_LOCATIONS_MATCH_MESSAGE =
            "More than one location already exists at point (%f,%f) and with precision %s, and HealthMap location " +
            "has no GeoNames ID. Arbitrarily using location ID %d.";
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
            Point point = createPointFromLatLong(healthMapLocation);

            location = findExistingLocation(healthMapLocation, point);
            if (location == null) {
                location = createLocation(healthMapLocation, point);
            }
        }

        return location;
    }

    /**
     * Adds location precision to a location. This is split out from the rest of the convert method, so that we only
     * call GeoNames if necessary.
     * @param healthMapLocation The HealthMap location.
     * @param location The location.
     */
    public void addPrecision(HealthMapLocation healthMapLocation, Location location) {
        Integer geoNameId = healthMapLocation.getGeoNameId();

        if (geoNameId != null) {
            addPrecisionUsingGeoNames(location, geoNameId);
        }

        if (location.getPrecision() == null) {
            // The precision could not be added using GeoNames for whatever reason. So use the "rough" location
            // precision supplied in HealthMap's place_basic_type field.
            addPrecisionUsingHealthMapPlaceBasicType(location, healthMapLocation);
        }
    }

    private boolean validate(HealthMapLocation healthMapLocation) {
        String validationMessage =
                new HealthMapLocationValidator(healthMapLocation, lookupData.getCountryMap()).validate();
        if (validationMessage != null) {
            LOGGER.error(validationMessage);
            return false;
        }
        return true;
    }

    private Location findExistingLocation(HealthMapLocation healthMapLocation, Point point) {
        Location location;

        if (healthMapLocation.getGeoNameId() != null) {
            // Query for an existing location at the specified GeoNames ID
            location = locationService.getLocationByGeoNameId(healthMapLocation.getGeoNameId());
        } else {
            // Query for an existing location at the specified lat/long and location precision
            LocationPrecision precision = findLocationPrecision(healthMapLocation);
            location = findExistingLocation(point, precision);
        }

        return location;
    }

    /**
     * Finds an existing location at the specified point and precision.
     * @param point The point.
     * @param precision The precision.
     * @return The first found location, or null if none found.
     */
    public Location findExistingLocation(Point point, LocationPrecision precision) {
        Location location = null;
        List<Location> locations = locationService.getLocationsByPointAndPrecision(point, precision);

        if (locations.size() > 0) {
            location = locations.get(0);
            if (locations.size() > 1) {
                // There may be multiple locations at the specified lat/long and location precision. For example:
                // - Location 1 is created at point (x,y) with no GeoNames ID and place_basic_type 'p' (precise)
                // - Location 2 is created at the same point (x,y) with a specified GeoNames ID, whose feature code
                //   indicates a precise location
                // It is valid for these to co-exist, but which one wins in this case is arbitrary. So we just pick
                // the first one and log that fact.
                LOGGER.warn(String.format(MULTIPLE_LOCATIONS_MATCH_MESSAGE, point.getX(), point.getY(), precision,
                        location.getId()));
            }
        }

        return location;
    }

    private Location createLocation(HealthMapLocation healthMapLocation, Point point) {
        Location location = null;

        HealthMapCountry healthMapCountry = lookupData.getCountryMap().get(healthMapLocation.getCountryId());
        if (CollectionUtils.isEmpty(healthMapCountry.getCountries())) {
            LOGGER.warn(String.format(IGNORING_COUNTRY_MESSAGE, healthMapCountry.getName()));
        } else {
            location = new Location();
            location.setHealthMapCountryId(healthMapCountry.getId());
            location.setGeom(point);
            location.setName(healthMapLocation.getPlaceName());
        }

        return location;
    }

    private void addPrecisionUsingGeoNames(Location location, int geoNameId) {
        location.setGeoNameId(geoNameId);
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
            uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName geoNameDTO =
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
            uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName geoNameDTO) {
        GeoName geoName = new GeoName(geoNameDTO.getGeoNameId(), geoNameDTO.getFeatureCode());
        locationService.saveGeoName(geoName);
        return geoName;
    }

    private Point createPointFromLatLong(HealthMapLocation healthMapLocation) {
        return GeometryUtils.createPoint(healthMapLocation.getLongitude(), healthMapLocation.getLatitude());
    }
}
