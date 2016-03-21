package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.CsvDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.HealthMapDataAcquirer;

import java.util.List;

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
     * @param isBias Whether or not this is a "bias" data set.
     * @param isGoldStandard Whether or not this is a "gold standard" data set (only relevant for non-bias data sets).
     * @param biasDisease The ID of the disease for which this is a bias data set (only relevant for bias data sets).
     * @return A list of messages resulting from the data acquisition.
     */
    @Override
    public List<String> acquireCsvData(byte[] csv, boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease) {
        return csvDataAcquirer.acquireDataFromCsv(csv, isBias, isGoldStandard, biasDisease);
    }
}
