package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

/**
 * Provides a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerImpl implements InputDataManager {
    private static final Logger LOGGER = Logger.getLogger(InputDataManagerImpl.class);

    private static final String OCCURRENCE_CSV = "occurrences.csv";
    private static final String SUPPLEMENTARY_OCCURRENCE_CSV = "supplementary_occurrences.csv";
    private static final String EXTENT_RASTER = "extent.tif";
    private final ExtentDataWriter extentDataWriter;
    private final OccurrenceDataWriter occurrenceDataWriter;

    public InputDataManagerImpl(ExtentDataWriter extentDataWriter, OccurrenceDataWriter occurrenceDataWriter) {
        this.extentDataWriter = extentDataWriter;
        this.occurrenceDataWriter = occurrenceDataWriter;
    }

    /**
     * Write the occurrence data to file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @param isSupplementary If this is supplementary occurrence file, instead of the main one.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeOccurrenceData(List<DiseaseOccurrence> occurrenceData, File dataDirectory, boolean isSupplementary)
            throws IOException {
        File outbreakFile = Paths.get(dataDirectory.toString(),
                isSupplementary ? SUPPLEMENTARY_OCCURRENCE_CSV : OCCURRENCE_CSV).toFile();
        occurrenceDataWriter.write(occurrenceData, outbreakFile, !isSupplementary);
    }

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param baseExtentRaster The base raster file containing the gaul codes to transform the extent values.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeExtentData(
            Collection<AdminUnitDiseaseExtentClass> extentData, File baseExtentRaster, File dataDirectory)
            throws IOException {
        File extentFile = Paths.get(dataDirectory.toString(), EXTENT_RASTER).toFile();
        extentDataWriter.write(extentData, baseExtentRaster, extentFile);
    }
}
