package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.CountryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapCountryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.LocationDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;

import java.util.List;

/**
 * Service class for locations, including countries.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class LocationServiceImpl implements LocationService {
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private LocationDao locationDao;

    public LocationServiceImpl(CountryDao countryDao, HealthMapCountryDao healthMapCountryDao,
                               LocationDao locationDao) {
        this.countryDao = countryDao;
        this.healthMapCountryDao = healthMapCountryDao;
        this.locationDao = locationDao;
    }

    /**
     * Gets all countries.
     * @return All countries.
     */
    @Override
    public List<Country> getAllCountries() {
        return countryDao.getAll();
    }

    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    @Override
    public List<HealthMapCountry> getAllHealthMapCountries() {
        return healthMapCountryDao.getAll();
    }

    /**
     * Gets a location by GeoNames ID.
     * @param geoNamesId The GeoNames ID.
     * @return The location with the specified GeoNames ID, or null if not found.
     */
    @Override
    public Location getLocationByGeoNamesId(int geoNamesId) {
        return locationDao.getByGeoNamesId(geoNamesId);
    }

    /**
     * Gets a list of locations that have the specified point.
     * @param point The point.
     * @return The locations with the specified point.
     */
    @Override
    public List<Location> getLocationsByPoint(Point point) {
        return locationDao.getByPoint(point);
    }
}
