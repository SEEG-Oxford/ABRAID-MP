package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.AdminUnitRunConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Interface to provide a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface InputDataManager {
    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    void writeOccurrenceData(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File dataDirectory)
            throws IOException;

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param config A configuration object holding the path of the shapefile to use in rasterisation.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    void writeExtentData(Map<Integer, Integer> extentData, AdminUnitRunConfiguration config, File dataDirectory)
            throws IOException;
}
