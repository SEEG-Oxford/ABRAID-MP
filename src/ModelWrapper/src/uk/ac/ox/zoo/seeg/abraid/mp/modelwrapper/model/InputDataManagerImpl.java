package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.geometry.Envelope2D;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.AdminUnitRunConfiguration;

import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Provides a mechanism for writing model input data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class InputDataManagerImpl implements InputDataManager {
    private static final Logger LOGGER = Logger.getLogger(FreemarkerScriptGenerator.class);
    private static final String LOG_FEATURE_CRS_WARN = "Aborted writing occurrence data due to feature level CRS.";
    private static final String LOG_WRITING_OCCURRENCE_DATA = "Writing %d occurrence data points to workspace at %s";
    private static final String LOG_TOP_LEVEL_CRS_WARN = "Aborted writing occurrence data due to incorrect CRS.";

    private static final String UTF_8 = "UTF-8";
    private static final String OUTBREAK_CSV = "outbreak.csv";
    private static final String EXTENT_RASTER = "extent.asc";

    /**
     * Write the occurrence data to file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param dataDirectory The directory to create the data files in.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void writeOccurrenceData(GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData, File dataDirectory)
            throws IOException {
        LOGGER.info(String.format(
                LOG_WRITING_OCCURRENCE_DATA, occurrenceData.getFeatures().size(), dataDirectory.getParent()));
        if (!occurrenceData.getCrs().equals(GeoJsonNamedCrs.createEPSG4326())) {
            LOGGER.warn(LOG_TOP_LEVEL_CRS_WARN);
            throw new IllegalArgumentException("Only EPSG:4326 is supported.");
        }

        File outbreakFile = Paths.get(dataDirectory.toString(), OUTBREAK_CSV).toFile();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outbreakFile.getAbsoluteFile()), UTF_8));
            for (GeoJsonDiseaseOccurrenceFeature occurrence : occurrenceData.getFeatures()) {
                if (occurrence.getCrs() != null) {
                    LOGGER.warn(LOG_FEATURE_CRS_WARN);
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
                Double.toString(occurrence.getGeometry().getCoordinates().getLongitude()),
                Double.toString(occurrence.getGeometry().getCoordinates().getLatitude()),
                occurrence.getProperties().getWeighting().toString(),
                occurrence.getProperties().getLocationPrecision().getModelValue().toString(),
                extractGaulCode(occurrence)
        }, ',');
    }

    private String extractGaulCode(GeoJsonDiseaseOccurrenceFeature occurrence) {
        if (occurrence.getProperties().getLocationPrecision() == LocationPrecision.PRECISE) {
            return "NA";
        } else {
            return occurrence.getProperties().getGaulCode().toString();
        }
    }

    @Override
    public void writeExtentData(Map<Integer, Integer> extentData, AdminUnitRunConfiguration config, File dataDirectory)
            throws IOException {
        File loadLocation = extractRasterFilePath(config);
        GridCoverage2D sourceRaster = loadRaster(loadLocation);

        Envelope2D rasterExtent = sourceRaster.getGridGeometry().getEnvelope2D();
        WritableRaster rasterData = (WritableRaster) sourceRaster.getRenderedImage().getData();

        transformRaster(extentData, rasterData);

        File saveLocation = Paths.get(dataDirectory.toString(), EXTENT_RASTER).toFile();
        saveRaster(saveLocation, rasterData, rasterExtent);
    }

    private void transformRaster(Map<Integer, Integer> transform, WritableRaster data) {
        for (int i = 0; i < data.getWidth(); i++) {
            for (int j = 0; j < data.getHeight(); j++) {
                int gaul = data.getSample(i, j, 0);
                if (transform.containsKey(gaul)) {
                    data.setSample(i, j, 0, transform.get(gaul));
                } else {
                    data.setSample(i, j, 0, Double.NaN);
                }
            }
        }
    }

    private GridCoverage2D loadRaster(File location) throws IOException {
        ArcGridReader reader = new ArcGridReader(location.toURI().toURL());
        return reader.read(null);
    }

    private void saveRaster(File location, WritableRaster raster, Envelope2D extents) throws IOException {
        GridCoverageFactory factory = new GridCoverageFactory();
        GridCoverage2D targetRaster = factory.create(location.getName(), raster, extents);
        ArcGridWriter writer = new ArcGridWriter(location.toURI().toURL());
        writer.write(targetRaster, null);
    }

    private File extractRasterFilePath(AdminUnitRunConfiguration config) {
        String shapefilePath = config.getUseGlobalRasterFile() ?
                config.getGlobalRasterFile() :
                config.getTropicalRasterFile();
        return Paths.get(shapefilePath).toFile();
    }
}
