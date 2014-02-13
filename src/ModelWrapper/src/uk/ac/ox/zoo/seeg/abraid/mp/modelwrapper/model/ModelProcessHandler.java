package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Provides callbacks for model completion and datastreams for model io.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandler implements ProcessHandler {
    private final OutputStream outputStream = new PipedOutputStream();
    private final OutputStream errorStream = new PipedOutputStream();
    private final InputStream inputStream = new PipedInputStream();

    /**
     * Called when asynchronous model execution completes.
     * @param exitValue The return code of the model.
     */
    @Override
    public void onProcessComplete(int exitValue) {
    }

    /**
     * Called when asynchronous model execution fails.
     * @param e Cause of failure.
     */
    @Override
    public void onProcessFailed(ProcessException e) {
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
    public InputStream getInputStream() {
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
}
