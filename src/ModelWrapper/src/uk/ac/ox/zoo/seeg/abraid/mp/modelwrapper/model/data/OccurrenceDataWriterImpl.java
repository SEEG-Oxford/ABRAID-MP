package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;

import java.io.*;

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
    private static final String R_CODE_NULL_IDENTIFIER = "NA";

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


        try (BufferedWriter writer = createBufferedWriter(targetFile)) {
            writer.write(extractCsvHeaderLine());
            writer.newLine();

            for (GeoJsonDiseaseOccurrenceFeature occurrence : occurrenceData.getFeatures()) {
                if (occurrence.getCrs() != null) {
                    LOGGER.warn(LOG_FEATURE_CRS_WARN);
                    throw new IllegalArgumentException("Feature level CRS are not supported.");
                }

                writer.write(extractCsvLine(occurrence));
                writer.newLine();
            }
        }
    }

    private BufferedWriter createBufferedWriter(File file) throws IOException {
        return new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), UTF_8));
    }

    private String extractCsvLine(GeoJsonDiseaseOccurrenceFeature occurrence) {
        return StringUtils.join(new String[]{
                Double.toString(occurrence.getGeometry().getCoordinates().getLongitude()),
                Double.toString(occurrence.getGeometry().getCoordinates().getLatitude()),
                ObjectUtils.toString(occurrence.getProperties().getWeighting()),
                ObjectUtils.toString(occurrence.getProperties().getLocationPrecision().getModelValue()),
                extractGaulCode(occurrence)
        }, ',');
    }

    private String extractCsvHeaderLine() {
        return StringUtils.join(new String[]{
                "Longitude",
                "Latitude",
                "Weight",
                "Admin",
                "GAUL"
        }, ',');
    }

    private String extractGaulCode(GeoJsonDiseaseOccurrenceFeature occurrence) {
        if (occurrence.getProperties().getLocationPrecision() == LocationPrecision.PRECISE) {
            return R_CODE_NULL_IDENTIFIER;
        } else {
            return ObjectUtils.toString(occurrence.getProperties().getGaulCode());
        }
    }
}
