package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.GeoNamesWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.List;

import static java.lang.String.format;

/**
 * Converts a HealthMap location into an ABRAID location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationConverter {
    private static final Logger LOGGER = Logger.getLogger(HealthMapLocationConverter.class);
    private static final String MULTIPLE_LOCATIONS_MATCH_MESSAGE =
            "More than one location already exists at point (%f,%f) and with precision %s";
    private static final String IGNORING_COUNTRY_MESSAGE =
            "Ignoring HealthMap location in country \"%s\" as it is not of interest";
    private static final String GEONAMES_FCODE_NOT_IN_DATABASE =
            "Feature code \"%s\" is not in the ABRAID database (GeoName ID %d) - assuming precise location";

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
     * @return An ABRAID location, or null if the HealthMap location is invalid.
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
     * Adds location precision to a location. This is split out from the rest of the convert method, so that
     * we only call GeoNames if necessary.
     * @param location The location.
     * @param healthMapLocation The HealthMap location.
     */
    public void addPrecision(Location location, HealthMapLocation healthMapLocation) {
        if (healthMapLocation.getGeoNameId() != null) {
            LocationPrecision precision = getGeoNamesLocationPrecision(healthMapLocation.getGeoNameId());
            if (precision != null) {
                // We obtained a feature code from GeoNames and mapped it to our location precision
                location.setGeoNamesId(healthMapLocation.getGeoNameId());
                location.setPrecision(precision);
            }
        }

        if (location.getGeoNamesId() == null) {
            // Either the HealthMap location does not have a GeoNames ID, or the GeoNames web service couldn't
            // find it. So use the "rough" location precision supplied in HealthMap's place_basic_type field.
            location.setPrecision(findLocationPrecision(healthMapLocation));
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
        Location location = null;

        if (healthMapLocation.getGeoNameId() != null) {
            // Query for an existing location at the specified GeoNames ID
            location = locationService.getLocationByGeoNamesId(healthMapLocation.getGeoNameId());
        } else {
            // Query for an existing location at the specified lat/long and location precision
            LocationPrecision precision = findLocationPrecision(healthMapLocation);
            List<Location> locations = locationService.getLocationsByPointAndPrecision(point, precision);
            if (locations.size() > 0) {
                location = locations.get(0);
                if (locations.size() > 1) {
                    LOGGER.warn(format(MULTIPLE_LOCATIONS_MATCH_MESSAGE, point.getX(), point.getY(), precision));
                }
            }
        }

        return location;
    }

    private Location createLocation(HealthMapLocation healthMapLocation, Point point) {
        Location location = new Location();

        HealthMapCountry healthMapCountry = lookupData.getCountryMap().get(healthMapLocation.getCountry());
        if (healthMapCountry.getCountry() == null) {
            LOGGER.warn(format(IGNORING_COUNTRY_MESSAGE, healthMapLocation.getCountry()));
        } else {
            location.setCountry(healthMapCountry.getCountry());
            location.setGeom(point);
            location.setName(healthMapLocation.getPlaceName());
        }

        return location;
    }

    private LocationPrecision getGeoNamesLocationPrecision(int geoNamesId) {
        LocationPrecision precision = null;
        GeoName geoName = geoNamesWebService.getById(geoNamesId);

        if (geoName != null) {
            // If the geonames feature code is not found, log for info i.e. "feature code XYZ not found,
            // assuming a precise location"
            precision = lookupData.getGeoNamesMap().get(geoName.getFcode());
            if (precision == null) {
                // We retrieved the GeoNames feature code from the web service, but the feature code is not in
                // our mapping table. So assume that it's a precise location.
                LOGGER.warn(format(GEONAMES_FCODE_NOT_IN_DATABASE, geoName.getFcode(), geoNamesId));
                precision = LocationPrecision.PRECISE;
            }
        }

        return precision;
    }

    private Point createPointFromLatLong(HealthMapLocation healthMapLocation) {
        return GeometryUtils.createPoint(healthMapLocation.getLat(), healthMapLocation.getLng());
    }

    private LocationPrecision findLocationPrecision(HealthMapLocation healthMapLocation) {
        return LocationPrecision.findByHealthMapPlaceBasicType(healthMapLocation.getPlaceBasicType());
    }
}
