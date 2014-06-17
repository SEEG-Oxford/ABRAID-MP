package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AlertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.FeedDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ProvenanceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

import java.util.List;

/**
 * Service class for disease alerts.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class AlertServiceImpl implements AlertService {
    private AlertDao alertDao;
    private FeedDao feedDao;
    private ProvenanceDao provenanceDao;

    public AlertServiceImpl(AlertDao alertDao, FeedDao feedDao, ProvenanceDao provenanceDao) {
        this.alertDao = alertDao;
        this.feedDao = feedDao;
        this.provenanceDao = provenanceDao;
    }

    /**
     * Gets an alert by HealthMap alert ID.
     * @param healthMapAlertId The HealthMap alert ID.
     * @return The alert with this HealthMap alert ID, or null if not found.
     */
    @Override
    public Alert getAlertByHealthMapAlertId(Integer healthMapAlertId) {
        return alertDao.getByHealthMapAlertId(healthMapAlertId);
    }

    /**
     * Gets a list of feeds with the specified provenance name.
     * @param provenanceName The provenance name.
     * @return A list of feeds with the specified provenance name.
     */
    @Override
    public List<Feed> getFeedsByProvenanceName(String provenanceName) {
        return feedDao.getByProvenanceName(provenanceName);
    }

    /**
     * Saves a feed.
     * @param feed The feed to save.
     */
    @Override
    public void saveFeed(Feed feed) {
        feedDao.save(feed);
    }

    /**
     * Gets a provenance by name.
     * @param name The name.
     * @return The provenance with the specified name, or null if non-existent.
     */
    @Override
    public Provenance getProvenanceByName(String name) {
        return provenanceDao.getByName(name);
    }

    /**
     * Saves a provenance.
     * @param provenance The provenance to save.
     */
    @Override
    public void saveProvenance(Provenance provenance) {
        provenanceDao.save(provenance);
    }
}
