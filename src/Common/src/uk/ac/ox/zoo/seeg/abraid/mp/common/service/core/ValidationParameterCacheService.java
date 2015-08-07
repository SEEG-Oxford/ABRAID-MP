package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

/**
 * Service interface for cached validation parameter values.
 * Copyright (c) 2015 University of Oxford
 */
public interface ValidationParameterCacheService {
    /**
     * Gets the cached "distance to disease extent" value for a location and disease pair, or null if not in the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId The id of the location.
     * @return The cached value or null.
     */
    Double getDistanceToExtentFromCache(int diseaseGroupId, int locationId);

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the extent has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    void clearDistanceToExtentCacheForDisease(int diseaseGroupId);

    /**
     * Add a "distance to disease extent" value to the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId The id of the location.
     * @param distance The distance to cache.
     */
    void saveDistanceToExtentCacheEntry(int diseaseGroupId, int locationId, double distance);

    /**
     * Gets the cached "environmental suitability" value for a location and disease pair, or null if not in the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId The id of the location.
     * @return The cached value or null.
     */
    Double getEnvironmentalSuitabilityFromCache(int diseaseGroupId, int locationId);

    /**
     * Clears the "environmental suitability" cache of all values for a disease (i.e. the raster has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    void clearEnvironmentalSuitabilityCacheForDisease(int diseaseGroupId);

    /**
     * Add a "environmental suitability" value to the cache.
     * @param diseaseGroupId The id of the disease group.
     * @param locationId The id of the location.
     * @param suitability The suitability to cache.
     */
    void saveEnvironmentalSuitabilityCacheEntry(int diseaseGroupId, int locationId, double suitability);
}
