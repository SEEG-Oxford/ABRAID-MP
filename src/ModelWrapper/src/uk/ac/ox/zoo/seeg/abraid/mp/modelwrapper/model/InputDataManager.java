package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface InputDataManager {
    /**
     * Write the occurrence data to file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    void writeOccurrenceData(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File dataDirectory) throws IOException;
}
