package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for locations, including countries.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class LocationServiceImpl implements LocationService {
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private LocationDao locationDao;
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;
    private GeoNameDao geoNameDao;
    private AdminUnitQCDao adminUnitQCDao;
    private NativeSQL nativeSQL;
    private LandSeaBorderDao landSeaBorderDao;

    public LocationServiceImpl(CountryDao countryDao, HealthMapCountryDao healthMapCountryDao,
                               LocationDao locationDao, GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao,
                               GeoNameDao geoNameDao, AdminUnitQCDao adminUnitQCDao, NativeSQL nativeSQL,
                               LandSeaBorderDao landSeaBorderDao) {
        this.countryDao = countryDao;
        this.healthMapCountryDao = healthMapCountryDao;
        this.locationDao = locationDao;
        this.geoNamesLocationPrecisionDao = geoNamesLocationPrecisionDao;
        this.geoNameDao = geoNameDao;
        this.adminUnitQCDao = adminUnitQCDao;
        this.nativeSQL = nativeSQL;
        this.landSeaBorderDao = landSeaBorderDao;
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
     * Gets all administrative units.
     * @return All administrative units.
     */
    @Override
    public List<AdminUnitQC> getAllAdminUnitQCs() {
        return adminUnitQCDao.getAll();
    }

    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    public Integer findAdminUnitGlobalThatContainsPoint(Point point, Character adminLevel) {
        return nativeSQL.findAdminUnitThatContainsPoint(point, true, adminLevel);
    }

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    public Integer findAdminUnitTropicalThatContainsPoint(Point point, Character adminLevel) {
        return nativeSQL.findAdminUnitThatContainsPoint(point, false, adminLevel);
    }

    /**
     * Gets all land-sea borders.
     * @return All land-sea borders.
     */
    @Override
    public List<LandSeaBorder> getAllLandSeaBorders() {
        return landSeaBorderDao.getAll();
    }

    /**
     * Gets a location by GeoNames ID.
     * @param geoNameId The GeoNames ID.
     * @return The location with the specified GeoNames ID, or null if not found.
     */
    @Override
    public Location getLocationByGeoNameId(int geoNameId) {
        return locationDao.getByGeoNameId(geoNameId);
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

    /**
     * Gets a GeoName by ID.
     * @param geoNameId The GeoNames ID.
     * @return The GeoName, or null if not found.
     */
    @Override
    public GeoName getGeoNameById(int geoNameId) {
        return geoNameDao.getById(geoNameId);
    }

    /**
     * Saves a GeoName.
     * @param geoName The GeoName to save.
     */
    @Override
    public void saveGeoName(GeoName geoName) {
        geoNameDao.save(geoName);
    }
}
