package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
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
        validateOccurrenceCollection(occurrenceData);

        List<JsonDiseaseOccurrence> occurrences = new ArrayList<>();
        for (GeoJsonDiseaseOccurrenceFeature occurrence : occurrenceData.getFeatures()) {
            validateOccurrence(occurrence);
            occurrences.add(new JsonDiseaseOccurrence(occurrence));
        }

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(JsonDiseaseOccurrence.class).withHeader();
        try (FileOutputStream fileStream = new FileOutputStream(targetFile.getAbsoluteFile())) {
            csvMapper.writer(schema).writeValue(fileStream, occurrences);
            fileStream.flush();
        }
    }

    private void validateOccurrenceCollection(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData) {
        if (!occurrenceData.getCrs().equals(GeoJsonNamedCrs.createEPSG4326())) {
            LOGGER.warn(LOG_TOP_LEVEL_CRS_WARN);
            throw new IllegalArgumentException("Only EPSG:4326 is supported.");
        }
    }

    private void validateOccurrence(GeoJsonDiseaseOccurrenceFeature occurrence) {
        if (occurrence.getCrs() != null) {
            LOGGER.warn(LOG_FEATURE_CRS_WARN);
            throw new IllegalArgumentException("Feature level CRS are not supported.");
        }

        if (occurrence.getProperties().getLocationPrecision() == LocationPrecision.COUNTRY) {
            throw new IllegalArgumentException("Country location occurrences are not supported.");
        }

        if (occurrence.getProperties().getLocationPrecision() == LocationPrecision.PRECISE
                && occurrence.getProperties().getGaulCode() != null) {
            throw new IllegalArgumentException("Precise location occurrences with GAUL codes are not supported.");
        }
    }
}
