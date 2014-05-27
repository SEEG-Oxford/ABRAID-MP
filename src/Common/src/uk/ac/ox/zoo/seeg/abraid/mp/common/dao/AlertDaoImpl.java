package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * The Alert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class AlertDaoImpl extends AbstractDao<Alert, Integer> implements AlertDao {
    public AlertDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets an alert by HealthMap alert ID.
     *
     * @param healthMapAlertId The HealthMap alert ID.
     * @return The alert, or null if not found.
     */
    @Override
    public Alert getByHealthMapAlertId(Integer healthMapAlertId) {
        return uniqueResultNamedQuery("getAlertByHealthMapAlertId", "healthMapAlertId", healthMapAlertId);
    }
}
