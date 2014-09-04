package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

/**
 * Service interface for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DataAcquisitionService {
    /**
     * Acquires HealthMap data from the HealthMap web service.
     */
    void acquireHealthMapDataFromWebService();

    /**
     * Acquires HealthMap data from a file.
     * @param jsonFileName The name of a file that contains HealthMap JSON.
     */
    void acquireHealthMapDataFromFile(String jsonFileName);
}
