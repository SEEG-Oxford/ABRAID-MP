package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EnvironmentalSuitabilityCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

/**
 * The EnvironmentalSuitabilityCacheEntry entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
public class EnvironmentalSuitabilityCacheEntryDaoImpl
        extends AbstractDao<EnvironmentalSuitabilityCacheEntry, ValidationParameterCacheEntryId>
        implements EnvironmentalSuitabilityCacheEntryDao {

    public EnvironmentalSuitabilityCacheEntryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Clears the "distance to disease extent" cache of all values for a disease (i.e. the raster has changed).
     * @param diseaseGroupId The id of the disease group.
     */
    @Override
    public void clearCacheForDisease(int diseaseGroupId) {
        noResultNamedQuery("clearEnvironmentalSuitabilityCacheForDisease", "diseaseGroupId", diseaseGroupId);
    }
}
