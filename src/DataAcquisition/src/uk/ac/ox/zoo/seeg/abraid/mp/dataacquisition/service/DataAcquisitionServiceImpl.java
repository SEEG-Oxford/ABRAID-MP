package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.HealthMapDataAcquirer;

/**
 * Service class for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DataAcquisitionServiceImpl implements DataAcquisitionService {
    private HealthMapDataAcquirer healthMapDataAcquirer;

    public DataAcquisitionServiceImpl(HealthMapDataAcquirer healthMapDataAcquirer) {
        this.healthMapDataAcquirer = healthMapDataAcquirer;
    }

    /**
     * Acquires HealthMap data from the HealthMap web service.
     */
    @Override
    public void acquireHealthMapDataFromWebService() {
        healthMapDataAcquirer.acquireDataFromWebService();
    }

    /**
     * Acquires HealthMap data from a file.
     * @param jsonFileName The name of a file that contains HealthMap JSON.
     */
    @Override
    public void acquireHealthMapDataFromFile(String jsonFileName) {
        healthMapDataAcquirer.acquireDataFromFile(jsonFileName);
    }
}
