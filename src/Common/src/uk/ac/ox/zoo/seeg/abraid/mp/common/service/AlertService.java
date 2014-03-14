package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

import java.util.List;

/**
 * Service interface for disease alerts.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AlertService {
    /**
     * Gets an alert by HealthMap alert ID.
     * @param healthMapAlertId The HealthMap alert ID.
     * @return The alert with this HealthMap alert ID, or null if not found.
     */
    Alert getAlertByHealthMapAlertId(Long healthMapAlertId);

    /**
     * Gets a list of alert feeds with the specified provenance name.
     * @param provenanceName The provenance name.
     * @return A list of alert feeds with the specified provenance name.
     */
    List<Feed> getFeedsByProvenanceName(String provenanceName);

    /**
     * Saves a feed.
     * @param feed The feed to save.
     */
    void saveFeed(Feed feed);

    /**
     * Gets a provenance by name.
     * @param name The name.
     * @return The provenance with the specified name, or null if non-existent.
     */
    Provenance getProvenanceByName(String name);

    /**
     * Saves a provenance.
     * @param provenance The provenance to save.
     */
    void saveProvenance(Provenance provenance);
}
