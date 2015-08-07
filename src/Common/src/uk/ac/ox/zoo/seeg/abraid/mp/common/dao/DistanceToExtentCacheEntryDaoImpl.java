package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

/**
 * The DistanceToExtentCacheEntry entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
public class DistanceToExtentCacheEntryDaoImpl
        extends AbstractDao<DistanceToExtentCacheEntry, ValidationParameterCacheEntryId>
        implements DistanceToExtentCacheEntryDao {

    public DistanceToExtentCacheEntryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the extent has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    @Override
    public void clearCacheForDisease(int diseaseGroupId) {
        noResultNamedQuery("clearDistanceToExtentCacheForDisease", "diseaseGroupId", diseaseGroupId);
    }
}
