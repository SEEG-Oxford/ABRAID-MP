package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EnvironmentalSuitabilityCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

/**
 * Interface for the EnvironmentalSuitabilityCacheEntry entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
public interface EnvironmentalSuitabilityCacheEntryDao {
    /**
     * Gets the cached "environmental suitability" entry for a location and disease pair, or null if not in the cache.
     * @param id The location and disease pair.
     * @return The cached entry or null.
     */
    EnvironmentalSuitabilityCacheEntry getById(ValidationParameterCacheEntryId id);

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the raster has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    void clearCacheForDisease(int diseaseGroupId);

    /**
     * Add a "distance to disease extent" entry to the cache.
     * @param environmentalSuitabilityCacheEntry The entry to save.
     */
    void save(EnvironmentalSuitabilityCacheEntry environmentalSuitabilityCacheEntry);
}
