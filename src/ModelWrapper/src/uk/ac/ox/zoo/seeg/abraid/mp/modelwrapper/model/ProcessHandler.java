package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to provide callbacks for process completion and datastreams for process io.
 * Copyright (c) 2014 University of Oxford
 */
public interface ProcessHandler {
    /**
     * Called when asynchronous execution completes.
     * @param exitValue The return code of the model.
     */
    void onProcessComplete(int exitValue);

    /**
     * Called when asynchronous execution fails.
     * @param e Cause of failure.
     */
    void onProcessFailed(ProcessException e);

    /**
     * Gets the data stream that should be used to capture "stdout" from the process.
     * @return The output stream
     */
    OutputStream getOutputStream();

    /**
     * Gets the data stream that should be used to provide "stdin" to the process.
     * @return The input stream
     */
    InputStream getInputStream();

    /**
     * Gets the data stream that should be used to capture "stderr" from the process.
     * @return The error stream
     */
    OutputStream getErrorStream();
}
