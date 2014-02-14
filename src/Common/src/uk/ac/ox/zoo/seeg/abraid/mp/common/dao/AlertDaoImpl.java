package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class AlertDaoImpl extends AbstractDao<Alert, Long> implements AlertDao {
    /**
     * Gets an alert by HealthMap alert ID.
     *
     * @param healthMapAlertId The HealthMap alert ID.
     * @return The alert, or null if not found.
     */
    @Override
    public Alert getByHealthMapAlertId(Long healthMapAlertId) {
        return uniqueResultNamedQuery("getAlertByHealthMapAlertId", "healthMapAlertId", healthMapAlertId);
    }
}
