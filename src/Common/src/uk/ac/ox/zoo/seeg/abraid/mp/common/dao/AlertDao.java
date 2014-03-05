package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * Interface for the Alert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AlertDao {
    /**
     * Gets an alert by ID.
     * @param alertId The alert ID.
     * @return The alert, or null if not found.
     */
    Alert getById(Integer alertId);

    /**
     * Gets an alert by HealthMap alert ID.
     * @param healthMapAlertId The HealthMap alert ID.
     * @return The alert, or null if not found.
     */
    Alert getByHealthMapAlertId(Long healthMapAlertId);

    /**
     * Saves an alert.
     * @param alert The alert to save.
     */
    void save(Alert alert);
}
