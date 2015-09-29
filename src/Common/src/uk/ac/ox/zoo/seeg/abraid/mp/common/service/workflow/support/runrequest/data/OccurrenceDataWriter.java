package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface to provide a mechanism for writing model input occurrence data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface OccurrenceDataWriter {
    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param targetFile The file to be created.
     * @param includeWeight If the weight field should be included in the csv output.
     * @throws IOException If the data could not be written.
     */
    void write(List<DiseaseOccurrence> occurrenceData, File targetFile, boolean includeWeight)
            throws IOException;
}
