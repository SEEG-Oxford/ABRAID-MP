package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceName;

import java.util.List;

/**
 * Interface for the Feed entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface FeedDao {
    /**
     * Gets a feed by ID.
     *
     * @param id The ID.
     * @return The feed with the specified ID, or null if not found.
     */
    Feed getById(Integer id);

    /**
     * Gets a list of feeds by provenance name.
     * @param provenanceName The provenance name.
     * @return A list of feeds whose provenance has the given name.
     */
    List<Feed> getByProvenanceName(ProvenanceName provenanceName);

    /**
     * Saves a feed.
     * @param feed The feed to save.
     */
    void save(Feed feed);
}
