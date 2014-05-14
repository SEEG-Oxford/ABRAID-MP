package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config;

import org.joda.time.LocalDateTime;

/**
 * Service interface for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ConfigurationService {
    /**
     * Updates the date on which DiseaseOccurrenceReviews were last retrieved.
     * @param lastRetrievalDate The date.
     */
    void setLastRetrievalDate(LocalDateTime lastRetrievalDate);

    /**
     * Gets the date on which DiseaseOccurrenceReviews were last retrieved.
     * @return The date.
     */
    LocalDateTime getLastRetrievalDate();
}
