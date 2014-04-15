package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

/**
 * Provides callbacks for model completion and datastreams for model io.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandler implements ProcessHandler {
    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final OutputStream errorStream = new ByteArrayOutputStream();
    private final PipedInputStream inputStream = new PipedInputStream();
    private ProcessWaiter processWaiter = null;

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
}
