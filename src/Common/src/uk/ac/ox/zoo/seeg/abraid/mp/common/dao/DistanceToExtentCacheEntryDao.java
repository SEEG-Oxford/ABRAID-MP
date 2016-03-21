package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

/**
 * Interface for the DistanceToExtentCacheEntry entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
public interface DistanceToExtentCacheEntryDao {
    /**
     * Gets the cached "distance to disease extent" entry for a location and disease pair, or null if not in the cache.
     * @param id The location and disease pair.
     * @return The cached entry or null.
     */
    DistanceToExtentCacheEntry getById(ValidationParameterCacheEntryId id);

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the extent has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    void clearCacheForDisease(int diseaseGroupId);

    /**
     * Add a "distance to disease extent" entry to the cache.
     * @param distanceToExtentCacheEntry The entry to save.
     */
    void save(DistanceToExtentCacheEntry distanceToExtentCacheEntry);
}
