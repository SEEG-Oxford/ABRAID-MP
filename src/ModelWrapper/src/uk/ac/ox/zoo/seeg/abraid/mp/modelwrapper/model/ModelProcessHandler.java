package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelOutputsMetadata;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.*;
import java.util.ArrayList;

/**
 * Provides callbacks for model completion and datastreams for model io.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandler implements ProcessHandler {
    private static Logger logger = Logger.getLogger(ModelProcessHandler.class);

    private static final String LOG_MODEL_RUN_FAILED = "Model run failed.";
    private static final String LOG_MODEL_RUN_COMPLETE = "Model run complete.";
    private static final String LOG_HANDLER_REQUESTING = "Sending model outputs to model output handler " +
            "(zip file size %s)...";
    private static final String LOG_HANDLER_REQUEST_ERROR = "Error sending model outputs for handling: %s";
    private static final String LOG_HANDLER_RESPONSE_ERROR = "Error received from model output handler: %s";
    private static final String LOG_HANDLER_SUCCESSFUL = "Successfully sent model outputs to model output handler.";
    private static final String LOG_COULD_NOT_DELETE_TEMP_FILE = "Could not delete temporary file \"%s\"";

    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final OutputStream errorStream = new ByteArrayOutputStream();
    private final PipedInputStream inputStream = new PipedInputStream();
    private ProcessWaiter processWaiter = null;

    private RunConfiguration runConfiguration;
    private ModelOutputHandlerWebService modelOutputHandlerWebService;

    public ModelProcessHandler(RunConfiguration runConfiguration,
                               ModelOutputHandlerWebService modelOutputHandlerWebService) {
        this.modelOutputHandlerWebService = modelOutputHandlerWebService;
        this.runConfiguration = runConfiguration;
    }

    /**
     * Called when asynchronous model execution completes.
     * @param exitValue The return code of the model.
     */
    @Override
    public void onProcessComplete(int exitValue) {
        logger.info(LOG_MODEL_RUN_COMPLETE);
        try {
            doesWorkingDirectoryExist();
            createMetadataAndSaveToFile();
            File zipFile = createZipFile();
            sendOutputsToModelOutputHandler(zipFile);
            deleteZipFile(zipFile);
        } catch (Exception e) {
            logger.fatal(String.format(LOG_HANDLER_REQUEST_ERROR, e.getMessage()), e);
        }
    }

    private void doesWorkingDirectoryExist() {
        File workingDirectory = runConfiguration.getWorkingDirectoryPath().toFile();
        if (!workingDirectory.exists()) {
            throw new IllegalArgumentException(String.format("working directory \"%s\" does not exist",
                    workingDirectory.getAbsolutePath()));
        }
    }

    /**
     * Called when asynchronous model execution fails.
     * @param e Cause of failure.
     */
    @Override
    public void onProcessFailed(ProcessException e) {
        logger.warn(LOG_MODEL_RUN_FAILED, e);
    }

    /**
     * Gets the data stream that should be used to capture "stdout" from the process.
     * @return The output stream
     */
    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Gets the data stream that should be used to provide "stdin" to the process.
     * @return The input stream
     */
    @Override
    public PipedInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the data stream that should be used to capture "stderr" from the process.
     * @return The error stream
     */
    @Override
    public OutputStream getErrorStream() {
        return errorStream;
    }

    /**
     * Block the current thread until the subprocess completes.
     * @return The exit code of the process.
     * @throws InterruptedException Thrown if the current thread is interrupted by another thread while it is waiting.
     */
    @Override
    public int waitForCompletion() throws InterruptedException {
        if (processWaiter == null) {
            throw new IllegalStateException("Process waiter not set");
        }
        return processWaiter.waitForProcess();
    }

    /**
     * Sets the processWaiter for the process. This should be called by ProcessRunner.run().
     * @param processWaiter The process waiter
     */
    @Override
    public void setProcessWaiter(ProcessWaiter processWaiter) {
        this.processWaiter = processWaiter;
    }

    private void createMetadataAndSaveToFile() throws IOException {
        // Create metadata and serialize as JSON
        JsonModelOutputsMetadata metadata = new JsonModelOutputsMetadata();
        metadata.setModelRunName(runConfiguration.getRunName());
        String metadataJson = new ObjectMapper().writeValueAsString(metadata);

        // Write metadata to a file
        File metadataFile = getFileInWorkingDirectory(ModelOutputConstants.METADATA_JSON_FILENAME);
        FileUtils.writeStringToFile(metadataFile, metadataJson);
    }

    private File createZipFile() throws IOException, ZipException {
        // We want a temporary filename but the file must not yet exist. So create a temporary file then delete it.
        File file = File.createTempFile("outputs", ".zip");
        deleteZipFile(file);

        ZipFile zipFile = new ZipFile(file);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        // Add files to zip file
        ArrayList filesToAdd = new ArrayList();
        filesToAdd.add(getFileInWorkingDirectory(ModelOutputConstants.METADATA_JSON_FILENAME));
        filesToAdd.add(getFileInWorkingDirectory(ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME));
        filesToAdd.add(getFileInWorkingDirectory(ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME));
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

    private File getFileInWorkingDirectory(String fileName) {
        return new File(runConfiguration.getWorkingDirectoryPath().toFile(), fileName);
    }
}
