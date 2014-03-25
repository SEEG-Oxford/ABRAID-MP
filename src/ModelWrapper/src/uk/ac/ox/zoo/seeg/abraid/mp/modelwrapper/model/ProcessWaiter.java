package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

/**
 * Interface to define a mechanism for waiting for processes to complete.
 * Copyright (c) 2014 University of Oxford
 */
public interface ProcessWaiter {
    /**
     * Causes the current thread to wait, if necessary, until the owned process has terminated.
     * @return The exit code of the process.
     * @throws InterruptedException Thrown if the current thread is interrupted by another thread while it is waiting.
     */
    int waitForProcess() throws InterruptedException;
}
