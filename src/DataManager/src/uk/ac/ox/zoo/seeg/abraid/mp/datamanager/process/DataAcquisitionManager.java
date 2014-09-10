package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

/**
 * Runs data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DataAcquisitionManager {
    private DataAcquisitionService dataAcquisitionService;

    public DataAcquisitionManager(DataAcquisitionService dataAcquisitionService) {
        this.dataAcquisitionService = dataAcquisitionService;
    }

    /**
     * Acquires data from all sources.
     * @param fileNames A list of file names containing HealthMap JSON data to acquire. If no file names are specified
     * (or if null), the HealthMap web service will be called instead.
     */
    public void runDataAcquisition(String[] fileNames) {
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                dataAcquisitionService.acquireHealthMapDataFromFile(fileName);
            }
        } else {
            dataAcquisitionService.acquireHealthMapDataFromWebService();
        }
    }
}
