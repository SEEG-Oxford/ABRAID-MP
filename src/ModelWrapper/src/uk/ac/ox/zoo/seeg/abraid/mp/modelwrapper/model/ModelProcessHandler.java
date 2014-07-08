package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

/**
 * Provides callbacks for model completion and datastreams for model io.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandler implements ProcessHandler {
    private static final Logger LOGGER = Logger.getLogger(ModelProcessHandler.class);

    private static final String LOG_MODEL_RUN_FAILED = "Model run failed.";
    private static final String LOG_MODEL_RUN_COMPLETE = "Model run complete.";

    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final OutputStream errorStream = new ByteArrayOutputStream();
    private final PipedInputStream inputStream = new PipedInputStream();
    private ProcessWaiter processWaiter = null;

    private ModelStatusReporter modelStatusReporter;

    public ModelProcessHandler(ModelStatusReporter modelStatusReporter) {
        this.modelStatusReporter = modelStatusReporter;
    }

    /**
     * Called when asynchronous model execution completes.
     */
    @Override
    public void onProcessComplete() {
        LOGGER.info(LOG_MODEL_RUN_COMPLETE);
        handleOutputs(ModelRunStatus.COMPLETED, null);
    }

    /**
     * Called when asynchronous model execution fails.
     * @param e Cause of failure.
     */
    @Override
    public void onProcessFailed(ProcessException e) {
        LOGGER.warn(LOG_MODEL_RUN_FAILED, e);
        handleOutputs(ModelRunStatus.FAILED, e.getMessage());
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

    private void handleOutputs(ModelRunStatus status, String errorMessage) {
        // Create error text
        String errorText = getErrorStream().toString();
        if (StringUtils.hasText(errorMessage)) {
            errorText = String.format("Error message: %s. Standard error: %s", errorMessage, errorText);
        }

        // Create output text
        String outputText = getOutputStream().toString();

        // Bundle outputs and send report
        modelStatusReporter.report(status, outputText, errorText);
    }
}
