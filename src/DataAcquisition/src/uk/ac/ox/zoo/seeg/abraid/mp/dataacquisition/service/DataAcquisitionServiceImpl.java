package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.CsvDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.HealthMapDataAcquirer;

/**
 * Service class for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DataAcquisitionServiceImpl implements DataAcquisitionService {
    private HealthMapDataAcquirer healthMapDataAcquirer;
    private CsvDataAcquirer csvDataAcquirer;

    public DataAcquisitionServiceImpl(HealthMapDataAcquirer healthMapDataAcquirer, CsvDataAcquirer csvDataAcquirer) {
        this.healthMapDataAcquirer = healthMapDataAcquirer;
        this.csvDataAcquirer = csvDataAcquirer;
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

    /**
     * Acquires data from a generic CSV file.
     * @param csv The content of the CSV file.
     * @param isGoldStandard Whether or not this is a "gold standard" data set.
     * @return A message upon the success of the data acquisition.
     * @throws DataAcquisitionException Upon failure of the data acquisition.
     */
    @Override
    public String acquireCsvData(String csv, boolean isGoldStandard) throws DataAcquisitionException {
        return csvDataAcquirer.acquireDataFromCsv(csv, isGoldStandard);
    }
}
