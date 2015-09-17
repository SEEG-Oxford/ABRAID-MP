package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * Represents the ModelWrapper's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebService {
    private WebServiceClient webServiceClient;
    private final AbraidJsonObjectMapper objectMapper;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelWrapper).
    private static final String MODEL_RUN_URL_PATH = "/model/run";

    public ModelWrapperWebService(WebServiceClient webServiceClient, AbraidJsonObjectMapper objectMapper) {
        this.webServiceClient = webServiceClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Starts a model run.
     * @param modelWrapperUrl The base url path for the model wrapper instance on which to start a run.
     * @param diseaseGroup The disease group for this model run.
     * @param occurrences The disease occurrences for this model run.
     * @param diseaseExtent Ths disease extent for this model run, expressed as a mapping between GAUL codes
     *                      and extent class weightings.
     * @param covariateFiles The covariate files for modelling the specified disease group.
     * @param covariateDirectory The directory where the covariate files are stored.
     * @return The model run name, or null if the run did not start successfully.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     * @throws IOException If the covariate file can not be found.
     * @throws ZipException If the model run zip can not be built.
     */
    public JsonModelRunResponse startRun(URI modelWrapperUrl, DiseaseGroup diseaseGroup,
                                         List<DiseaseOccurrence> occurrences, Map<Integer, Integer> diseaseExtent,
                                         Collection<CovariateFile> covariateFiles, String covariateDirectory)
            throws WebServiceClientException, JsonParserException, IOException, ZipException {
        String url = buildStartRunUrl(modelWrapperUrl);
        JsonModelRun metadata = createJsonModelRun(diseaseGroup, occurrences, diseaseExtent);
        byte[] data = createModelRunZip(metadata, covariateFiles, covariateDirectory);
        String response = webServiceClient.makePostRequestWithBinary(url, data);
        return parseResponseJson(response);
    }

    private String buildStartRunUrl(URI rootUrl) {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_RUN_URL_PATH)
                .build().toString();
    }

    private JsonModelRun createJsonModelRun(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> occurrences,
                                            Map<Integer, Integer> diseaseExtent) {
        JsonModelDisease jsonModelDisease = new JsonModelDisease(diseaseGroup);
        GeoJsonDiseaseOccurrenceFeatureCollection jsonOccurrences =
                new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences);
        return new JsonModelRun(jsonModelDisease, jsonOccurrences, diseaseExtent);
    }

    private JsonModelRunResponse parseResponseJson(String json) throws JsonParserException {
        return new JsonParser().parse(json, JsonModelRunResponse.class);
    }

    private String covertToJson(Object body, Class<?> view) {
        // only serialize properties that are annotated with ModellingJsonView or are not annotated at all
        ObjectWriter writer = objectMapper.writerWithView(view);
        try {
            return writer.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(e);
        }
    }

    private byte[] createModelRunZip(JsonModelRun metadata,
                                     Collection<CovariateFile> covariateFiles, String covariateDirectory)
            throws IOException, ZipException {
        Path tmpDir = null;
        Path tmpZip = null;
        try {
            // Create temporary paths
            tmpDir = Files.createTempDirectory("run");
            tmpZip = Files.createTempFile("run", ".zip");
            Files.delete(tmpZip);

            // Build directory with model run description
            String metadataAsJson = covertToJson(metadata, ModellingJsonView.class);
            FileUtils.writeStringToFile(Paths.get(tmpDir.toString(), "metadata.json").toFile(), metadataAsJson);

            for (CovariateFile file : covariateFiles) {
                FileUtils.copyFile(
                        Paths.get(covariateDirectory, file.getFile()).toFile(),
                        Paths.get(tmpDir.toString(), "covariates", file.getFile()).toFile()
                );
            }

            // Zip
            ZipFile zip = new ZipFile(tmpZip.toFile());
            ZipParameters parameters = new ZipParameters();
            parameters.setIncludeRootFolder(false);
            zip.createZipFileFromFolder(tmpDir.toFile(), parameters, false, 0);

            // Get bytes
            return FileUtils.readFileToByteArray(tmpZip.toFile());
        } finally {
            // Clean up
            if (tmpZip != null && tmpZip.toFile().exists()) {
                Files.delete(tmpZip);
            }

            if (tmpDir != null && tmpDir.toFile().exists()) {
                FileUtils.deleteDirectory(tmpDir.toFile());
            }
        }
    }
}
