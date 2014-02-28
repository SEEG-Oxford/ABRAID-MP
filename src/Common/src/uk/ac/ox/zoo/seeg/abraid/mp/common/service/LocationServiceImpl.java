package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.CountryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.GeoNamesLocationPrecisionDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapCountryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.LocationDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;

    public LocationServiceImpl(CountryDao countryDao, HealthMapCountryDao healthMapCountryDao,
                               LocationDao locationDao, GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao) {
        this.countryDao = countryDao;
        this.healthMapCountryDao = healthMapCountryDao;
        this.locationDao = locationDao;
        this.geoNamesLocationPrecisionDao = geoNamesLocationPrecisionDao;
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
     * Gets a list of locations that have the specified point and precision. This returns a list of locations as there
     * may be several at the same point with the same precision.
     * @param point The point.
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    @Override
    public List<Location> getLocationsByPointAndPrecision(Point point, LocationPrecision precision) {
        return locationDao.getByPointAndPrecision(point, precision);
    }

    /**
     * Gets mappings between GeoNames feature codes and location precision.
     * @return A set of mappings.
     */
    @Override
    public Map<String, LocationPrecision> getGeoNamesLocationPrecisionMappings() {
        List<GeoNamesLocationPrecision> list = geoNamesLocationPrecisionDao.getAll();
        Map<String, LocationPrecision> map = new HashMap<>();
        for (GeoNamesLocationPrecision item : list) {
            map.put(item.getGeoNamesFeatureCode(), item.getLocationPrecision());
        }
        return map;
    }
}
