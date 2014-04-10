package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.io.*;
import java.nio.file.Paths;

/**
 * Provides a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerImpl implements InputDataManager {
    private static final Logger LOGGER = Logger.getLogger(FreemarkerScriptGenerator.class);

    private static final String UTF_8 = "UTF-8";
    private static final String OUTBREAK_CSV = "outbreak.csv";

    /**
     * Write the occurrence data to file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeData(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File dataDirectory)
            throws IOException {
        LOGGER.warn("Writing " + occurrenceData.getFeatures().size()
                + " occurrence data points to workspace at " + dataDirectory.getParent());
        if (!occurrenceData.getCrs().equals(GeoJsonNamedCrs.createEPSG4326())) {
            LOGGER.warn("Aborted writing occurrence data due to incorrect CRS.");
            throw new IllegalArgumentException("Only EPSG:4326 is supported.");
        }

        File outbreakFile = Paths.get(dataDirectory.toString(), OUTBREAK_CSV).toFile();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outbreakFile.getAbsoluteFile()), UTF_8));
            for (GeoJsonDiseaseOccurrenceFeature occurrence : occurrenceData.getFeatures()) {
                if (occurrence.getCrs() != null) {
                    LOGGER.warn("Aborted writing occurrence data due to feature level CRS.");
                    throw new IllegalArgumentException("Feature level CRS are not supported.");
                }

                writer.write(extractCsvLine(occurrence));
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String extractCsvLine(GeoJsonDiseaseOccurrenceFeature occurrence) {
        return StringUtils.join(new String[]{
                occurrence.getGeometry().getCoordinates().get(0).toString(),
                occurrence.getGeometry().getCoordinates().get(1).toString(),
                occurrence.getProperties().getLocationPrecision().toString(),
                occurrence.getProperties().getWeighting().toString()
        }, ',');
    }
}
