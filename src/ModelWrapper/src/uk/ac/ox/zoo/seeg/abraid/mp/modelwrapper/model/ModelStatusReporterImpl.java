package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelOutputsMetadata;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Provides a mechanism for reporting model completion or failure (and results) to the model output handler.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelStatusReporterImpl implements ModelStatusReporter {
    private static Logger logger = Logger.getLogger(ModelStatusReporterImpl.class); // not final for test mocking

    private static final String LOG_HANDLER_REQUESTING = "Sending model outputs to model output handler " +
            "(zip file size %s)...";
    private static final String LOG_HANDLER_REQUEST_ERROR = "Error sending model outputs for handling: %s";
    private static final String LOG_HANDLER_RESPONSE_ERROR = "Error received from model output handler: %s";
    private static final String LOG_HANDLER_SUCCESSFUL = "Successfully sent model outputs to model output handler.";
    private static final String LOG_COULD_NOT_DELETE_TEMP_FILE = "Could not delete temporary file \"%s\"";
    private static final String LOG_COULD_NOT_DELETE_WORKSPACE_DIR = "Could not delete workspace directory \"%s\"";

    private final String runName;
    private final Path workingDirectoryPath;
    private ModelOutputHandlerWebService modelOutputHandlerWebService;
    private final AbraidJsonObjectMapper objectMapper;

    public ModelStatusReporterImpl(String runName, Path workingDirectory,
            ModelOutputHandlerWebService modelOutputHandlerWebService, AbraidJsonObjectMapper objectMapper) {
        this.runName = runName;
        this.workingDirectoryPath = workingDirectory;
        this.modelOutputHandlerWebService = modelOutputHandlerWebService;
        this.objectMapper = objectMapper;
    }

    /**
     * Report model completion or failure (and results) to the model output handler.
     * @param status The model status.
     * @param outputText The output text from the model.
     * @param errorText The error text (and/or exception text) from the model.
     */
    @Override
    public void report(ModelRunStatus status, String outputText, String errorText) {
        File zipFile = null;
        try {
            doesWorkingDirectoryExist();
            createMetadataAndSaveToFile(status, outputText, errorText);
            zipFile = createZipFile(pickOutputFiles(status));
            sendOutputsToModelOutputHandler(zipFile);
            deleteWorkingDirectory();
        } catch (Exception e) {
            logger.fatal(String.format(LOG_HANDLER_REQUEST_ERROR, e.getMessage()), e);
        } finally {
            if (zipFile != null) {
                deleteZipFile(zipFile);
            }
        }
    }

    private static String[] pickOutputFiles(ModelRunStatus status) {
        if (status == ModelRunStatus.COMPLETED) {
            return new String[] {
                    ModelOutputConstants.METADATA_JSON_FILENAME,
                    ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME,
                    ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME,
                    ModelOutputConstants.VALIDATION_STATISTICS_FILENAME,
                    ModelOutputConstants.RELATIVE_INFLUENCE_FILENAME,
                    ModelOutputConstants.EFFECT_CURVES_FILENAME
            };
        } else {
            return new String[] {
                    ModelOutputConstants.METADATA_JSON_FILENAME
            };
        }
    }

    private void doesWorkingDirectoryExist() {
        File workingDirectory = workingDirectoryPath.toFile();
        if (!workingDirectory.exists()) {
            throw new IllegalArgumentException(String.format("working directory \"%s\" does not exist",
                    workingDirectory.getAbsolutePath()));
        }
    }

    private void createMetadataAndSaveToFile(ModelRunStatus status, String outputText, String errorText)
            throws IOException {
        // Create metadata and serialize as JSON
        JsonModelOutputsMetadata metadata = new JsonModelOutputsMetadata(runName, status, outputText, errorText);

        String metadataJson = objectMapper.writeValueAsString(metadata);

        // Write metadata to a file
        File metadataFile = getFileInWorkingDirectory(ModelOutputConstants.METADATA_JSON_FILENAME);
        FileUtils.writeStringToFile(metadataFile, metadataJson);
    }

    private File createZipFile(String[] outputFilenames) throws IOException, ZipException {
        // We want a temporary filename but the file must not yet exist. So create a temporary file then delete it.
        File file = File.createTempFile("outputs", ".zip");
        deleteZipFile(file);

        ZipFile zipFile = new ZipFile(file);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        // Add files to zip file
        ArrayList filesToAdd = new ArrayList();
        for (String outputFilename : outputFilenames) {
            File fileToAdd = getFileInWorkingDirectory(outputFilename);
            if (fileToAdd.exists()) {
                filesToAdd.add(fileToAdd);
            } else {
                throw new IllegalArgumentException("File does not exist: " + fileToAdd.getAbsolutePath());
            }
        }

        zipFile.createZipFile(filesToAdd, zipParameters);

        return file;
    }

    private void sendOutputsToModelOutputHandler(File zipFile) throws IOException, WebServiceClientException {
        logger.info(String.format(LOG_HANDLER_REQUESTING, zipFile.length()));
        String responseText = modelOutputHandlerWebService.handleOutputs(zipFile);

        if (StringUtils.hasText(responseText)) {
            logger.error(String.format(LOG_HANDLER_RESPONSE_ERROR, responseText));
        } else {
            logger.info(LOG_HANDLER_SUCCESSFUL);
        }
    }

    private void deleteZipFile(File zipFile) {
        if (!zipFile.delete()) {
            logger.error(String.format(LOG_COULD_NOT_DELETE_TEMP_FILE, zipFile.getAbsolutePath()));
        }
    }

    private void deleteWorkingDirectory() {
        try {
            FileUtils.deleteDirectory(workingDirectoryPath.toFile());
        } catch (IOException e) {
            logger.error(String.format(LOG_COULD_NOT_DELETE_WORKSPACE_DIR, workingDirectoryPath.toAbsolutePath()));
        }
    }

    private File getFileInWorkingDirectory(String fileName) {
        return new File(workingDirectoryPath.toFile(), fileName);
    }
}
