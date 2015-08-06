package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for locations and associated geoname objects.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class LocationServiceImpl implements LocationService {

    private LocationDao locationDao;
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;
    private GeoNameDao geoNameDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;
    private DistanceToExtentCacheEntryDao distanceToExtentCacheDao;


    public LocationServiceImpl(LocationDao locationDao, GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao,
                               GeoNameDao geoNameDao, AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao,
                               DistanceToExtentCacheEntryDao distanceToExtentCacheDao) {
        this.locationDao = locationDao;
        this.geoNamesLocationPrecisionDao = geoNamesLocationPrecisionDao;
        this.geoNameDao = geoNameDao;
        this.adminUnitDiseaseExtentClassDao = adminUnitDiseaseExtentClassDao;
        this.distanceToExtentCacheDao = distanceToExtentCacheDao;
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

    /**
     * Gets one or more extent classes that corresponded to the specified location (multiple for countries that are
     * split in to admin units in the extent map).
     * @param diseaseId The disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param location The location
     * @return A list of extent classes.
     */
    @Override
    public List<AdminUnitDiseaseExtentClass> getAdminUnitDiseaseExtentClassesForLocation(
            int diseaseId, boolean isGlobal, Location location) {

        int gaulCode = isGlobal ? location.getAdminUnitGlobalGaulCode() : location.getAdminUnitTropicalGaulCode();
        Integer countryGaulCode = location.getCountryGaulCode();
        if (location.getPrecision().equals(LocationPrecision.COUNTRY) && countryGaulCode != gaulCode) {
            // Country is split, merged or has an alternative geom at this level
            List<AdminUnitDiseaseExtentClass> childAdminUnits = adminUnitDiseaseExtentClassDao
                            .getAllAdminUnitDiseaseExtentClassesByCountryGaulCode(diseaseId, isGlobal, countryGaulCode);

            if (!childAdminUnits.isEmpty()) {
                return childAdminUnits;
            }
            // If no child units are found, the shape is has either been subsumed into another extent shape (small
            // places, i.e. vatican), or an alternative extent shape is being used (russia in tropical).
            // In both cases they shape will be identified by the global/tropical gaul of the location (
            // same behavior as non-country)
        }

        return Arrays.asList(
                adminUnitDiseaseExtentClassDao.getDiseaseExtentClassByGaulCode(diseaseId, isGlobal, gaulCode));
    }

    /**
     * Saves the specified location.
     * @param location The location to save.
     */
    @Override
    public void saveLocation(Location location) {
        locationDao.save(location);
    }

    /**
     * Gets the cached "distance to disease extent" value for a location and disease pair, or null if not in the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId     The id of the location.
     * @return The cached value or null.
     */
    @Override
    public Double getDistanceToExtentFromCache(int diseaseGroupId, int locationId) {
        DistanceToExtentCacheEntryId id = new DistanceToExtentCacheEntryId(diseaseGroupId, locationId);
        DistanceToExtentCacheEntry entry = distanceToExtentCacheDao.getById(id);
        return (entry != null) ? entry.getDistance() : null;
    }

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the extent has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    @Override
    public void clearDistanceToExtentCacheForDisease(int diseaseGroupId) {
        distanceToExtentCacheDao.clearCacheForDisease(diseaseGroupId);
    }

    /**
     * Add a "distance to disease extent" value to the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId The id of the location.
     * @param distance The distance to cache.
     */
    @Override
    public void saveDistanceToExtentCacheEntry(int diseaseGroupId, int locationId, double distance) {
        distanceToExtentCacheDao.save(new DistanceToExtentCacheEntry(diseaseGroupId, locationId, distance));
    }
}
