package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Interface to provide a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface InputDataManager {
    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @param isBias If this is bias occurrence file, instead of the main one.
     * @throws IOException If the data could not be written.
     */
    void writeOccurrenceData(List<DiseaseOccurrence> occurrenceData, File dataDirectory, boolean isBias)
            throws IOException;

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param baseExtentRaster The base raster file containing the gaul codes to transform the extent values.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    void writeExtentData(Collection<AdminUnitDiseaseExtentClass> extentData, File baseExtentRaster, File dataDirectory)
            throws IOException;
}
