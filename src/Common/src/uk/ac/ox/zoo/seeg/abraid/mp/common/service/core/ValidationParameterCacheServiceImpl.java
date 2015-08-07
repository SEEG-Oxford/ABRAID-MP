package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DistanceToExtentCacheEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.EnvironmentalSuitabilityCacheEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EnvironmentalSuitabilityCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

/**
 * Service for cached validation parameter values.
 * Copyright (c) 2015 University of Oxford
 */
public class ValidationParameterCacheServiceImpl implements ValidationParameterCacheService {
    private DistanceToExtentCacheEntryDao distanceToExtentCacheDao;
    private EnvironmentalSuitabilityCacheEntryDao environmentalSuitabilityCacheDao;

    public ValidationParameterCacheServiceImpl(DistanceToExtentCacheEntryDao distanceToExtentCacheDao,
                                               EnvironmentalSuitabilityCacheEntryDao environmentalSuitabilityCacheDao) {
        this.distanceToExtentCacheDao = distanceToExtentCacheDao;
        this.environmentalSuitabilityCacheDao = environmentalSuitabilityCacheDao;
    }

    /**
     * Gets the cached "distance to disease extent" value for a location and disease pair, or null if not in the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId     The id of the location.
     * @return The cached value or null.
     */
    @Override
    public Double getDistanceToExtentFromCache(int diseaseGroupId, int locationId) {
        ValidationParameterCacheEntryId id = new ValidationParameterCacheEntryId(diseaseGroupId, locationId);
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
        distanceToExtentCacheDao.save(
                new DistanceToExtentCacheEntry(diseaseGroupId, locationId, distance));
    }

    @Override
    public Double getEnvironmentalSuitabilityFromCache(int diseaseGroupId, int locationId) {
        ValidationParameterCacheEntryId id = new ValidationParameterCacheEntryId(diseaseGroupId, locationId);
        EnvironmentalSuitabilityCacheEntry entry = environmentalSuitabilityCacheDao.getById(id);
        return (entry != null) ? entry.getEnvironmentalSuitability() : null;
    }

    @Override
    public void clearEnvironmentalSuitabilityCacheForDisease(int diseaseGroupId) {
        environmentalSuitabilityCacheDao.clearCacheForDisease(diseaseGroupId);
    }

    @Override
    public void saveEnvironmentalSuitabilityCacheEntry(int diseaseGroupId, int locationId, double suitability) {
        environmentalSuitabilityCacheDao.save(
                new EnvironmentalSuitabilityCacheEntry(diseaseGroupId, locationId, suitability));
    }
}
