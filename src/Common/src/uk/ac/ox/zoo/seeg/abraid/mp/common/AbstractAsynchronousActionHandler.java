package uk.ac.ox.zoo.seeg.abraid.mp.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A base class to encapsulate the thread pool behavior required for asynchronous task processing.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractAsynchronousActionHandler {
    private final ExecutorService pool;

    public AbstractAsynchronousActionHandler(int threadPoolSize) {
        pool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Initiates an orderly shutdown of the internal thread pool in which previously submitted tasks are executed,
     * but no new tasks will be accepted.
     */
    public void cleanup() {
        pool.shutdown();
    }

    /**
     * Submits a value-returning task for execution and returns a Future representing the pending results of the task.
     * @param task The task to submit.
     * @param <T> The return type of the task.
     * @return A Future representing pending completion of the task
     */
    protected <T> Future<T> submitAsynchronousTask(Callable<T> task) {
        return pool.submit(task);
    }
}
