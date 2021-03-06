package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Interface to provide a mechanism for writing model input extent data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public interface ExtentDataWriter {
    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param sourceRasterFile The path of the gaul code raster to reclassify.
     * @param targetFile The file to be created.
     * @throws IOException If the data could not be written.
     */
    void write(Collection<AdminUnitDiseaseExtentClass> extentData, File sourceRasterFile, File targetFile)
            throws IOException;
}
