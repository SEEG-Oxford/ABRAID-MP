package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

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

    /**
     * Acquires data from a generic CSV file.
     * @param csv The content of the CSV file.
     * @param isBias Whether or not this is a "bias" data set.
     * @param isGoldStandard Whether or not this is a "gold standard" data set (only relevant for non-bias data sets).
     * @param biasDisease The ID of the disease for which this is a bias data set (only relevant for bias data sets).
     * @return A list of messages resulting from the data acquisition.
     */
    List<String> acquireCsvData(byte[] csv, boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease);
}
