package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service interface for locations, including countries.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationService {
    /**
     * Gets a location by GeoNames ID.
     * @param geoNamesId The GeoNames ID.
     * @return The location with the specified GeoNames ID, or null if not found.
     */
    Location getLocationByGeoNamesId(int geoNamesId);

    /**
     * Gets a list of locations that have the specified point.
     * @param point The point.
     * @return The locations with the specified point.
     */
    List<Location> getLocationsByPoint(Point point);

    /**
     * Gets all countries.
     * @return All countries.
     */
    List<Country> getAllCountries();

    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    List<HealthMapCountry> getAllHealthMapCountries();
}
