package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.AdminUnitRunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Provides a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerImpl implements InputDataManager {
    private static final Logger LOGGER = Logger.getLogger(InputDataManagerImpl.class);

    private static final String OCCURRENCE_CSV = "occurrences.csv";
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
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeOccurrenceData(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File dataDirectory)
            throws IOException {
        File outbreakFile = Paths.get(dataDirectory.toString(), OCCURRENCE_CSV).toFile();

        occurrenceDataWriter.write(occurrenceData, outbreakFile);
    }

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param config A configuration object holding the path of the gaul code raster to reclassify.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeExtentData(Map<Integer, Integer> extentData, AdminUnitRunConfiguration config, File dataDirectory)
            throws IOException {
        File extentFile = Paths.get(dataDirectory.toString(), EXTENT_RASTER).toFile();

        String sourceRasterPath = config.getUseGlobalRasterFile() ?
                config.getGlobalRasterFile() :
                config.getTropicalRasterFile();

        File sourceRaster = Paths.get(sourceRasterPath).toFile();

        extentDataWriter.write(extentData, sourceRaster, extentFile);
    }
}
