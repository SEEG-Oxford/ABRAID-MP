package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config;

import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.DateTime;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;

/**
 * Service class for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceImpl implements ConfigurationService {

    private final FileConfiguration basicProperties;

    public ConfigurationServiceImpl(File basicProperties) throws ConfigurationException {
        this.basicProperties = new PropertiesConfiguration(basicProperties);
        this.basicProperties.setAutoSave(true);
    }
    /**
     * Updates the date at which DiseaseOccurrenceReviews were last retrieved.
     * @param lastRetrievalDate The date.
     */
    @Override
    public void setLastRetrievalDate(DateTime lastRetrievalDate) {
        basicProperties.setProperty("lastRetrievalDate", lastRetrievalDate);
    }

    /**
     * Gets the date on which DiseaseOccurrenceReviews were last retrieved.
     * @return The date.
     */
    @Override
    public DateTime getLastRetrievalDate() {
        return (DateTime) basicProperties.getProperty("lastRetrievalDate");
    }
}
