package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a mechanism for writing model input occurrence data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface OccurrenceDataWriter {
    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param targetFile The file to be created.
     * @throws IOException If the data could not be written.
     */
    void write(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File targetFile)
            throws IOException;
}
