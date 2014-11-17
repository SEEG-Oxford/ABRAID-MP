package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a mechanism for writing model input occurrence data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class OccurrenceDataWriterImpl implements OccurrenceDataWriter {
    private static final Logger LOGGER = Logger.getLogger(OccurrenceDataWriterImpl.class);
    private static final String LOG_FEATURE_CRS_WARN = "Aborted writing occurrence data due to feature level CRS.";
    private static final String LOG_WRITING_OCCURRENCE_DATA = "Writing %d occurrence data points to workspace at %s";
    private static final String LOG_TOP_LEVEL_CRS_WARN = "Aborted writing occurrence data due to incorrect CRS.";

    private static final String UTF_8 = "UTF-8";

    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param targetFile The file to be created.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void write(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File targetFile)
            throws IOException {
        LOGGER.info(String.format(
                LOG_WRITING_OCCURRENCE_DATA, occurrenceData.getFeatures().size(), targetFile.toString()));
        if (!occurrenceData.getCrs().equals(GeoJsonNamedCrs.createEPSG4326())) {
            LOGGER.warn(LOG_TOP_LEVEL_CRS_WARN);
            throw new IllegalArgumentException("Only EPSG:4326 is supported.");
        }

        List<JsonDiseaseOccurrence> occurrences = new ArrayList<>();
        for (GeoJsonDiseaseOccurrenceFeature occurrence : occurrenceData.getFeatures()) {
            if (occurrence.getCrs() != null) {
                LOGGER.warn(LOG_FEATURE_CRS_WARN);
                throw new IllegalArgumentException("Feature level CRS are not supported.");
            }

            occurrences.add(new JsonDiseaseOccurrence(occurrence));
        }

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(JsonDiseaseOccurrence.class).withHeader();
        csvMapper.writer(schema).writeValue(new FileOutputStream(targetFile.getAbsoluteFile()), occurrences);
    }
}
