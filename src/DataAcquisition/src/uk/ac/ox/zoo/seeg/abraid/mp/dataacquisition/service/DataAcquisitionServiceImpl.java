package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapDataAcquisition;

/**
 * Service class for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DataAcquisitionServiceImpl implements DataAcquisitionService {
    private HealthMapDataAcquisition healthMapDataAcquisition;

    public DataAcquisitionServiceImpl(HealthMapDataAcquisition healthMapDataAcquisition) {
        this.healthMapDataAcquisition = healthMapDataAcquisition;
    }

    /**
     * Acquires HealthMap data from the HealthMap web service.
     */
    @Override
    public void acquireHealthMapDataFromWebService() {
        healthMapDataAcquisition.acquireDataFromWebService();
    }

    /**
     * Acquires HealthMap data from a file.
     * @param jsonFileName The name of a file that contains HealthMap JSON.
     */
    @Override
    public void acquireHealthMapDataFromFile(String jsonFileName) {
        healthMapDataAcquisition.acquireDataFromFile(jsonFileName);
    }
}
