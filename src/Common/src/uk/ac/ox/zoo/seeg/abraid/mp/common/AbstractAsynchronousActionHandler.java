package uk.ac.ox.zoo.seeg.abraid.mp.common;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.convert;

/**
 * A base class to encapsulate the thread pool behaviour required for asynchronous task processing.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractAsynchronousActionHandler {
    private static final String ERROR_BACKGROUND_PROCESS =
            "An error occurred while waiting for a background process to complete";

    private final Logger logger;
    private final ExecutorService pool;

    public AbstractAsynchronousActionHandler(int threadPoolSize, Logger logger) {
        this.pool = Executors.newFixedThreadPool(threadPoolSize);
        this.logger = logger;
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
     * @return A Future representing pending completion of the task.
     */
    protected <T> Future<T> submitAsynchronousTask(Callable<T> task) {
        return pool.submit(task);
    }

    /**
     * Submits a collection of boolean-returning tasks and returns a single Future representing the pending results of
     * all of the task aggregated to a single result (and).
     * @param tasks The tasks to submit.
     * @return A Future representing pending completion of the tasks.
     */
    protected Future<Boolean> submitConcurrentAsynchronousTasksWithAggregateResult(
            final Collection<Callable<Boolean>> tasks) {
        return submitAsynchronousTask(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                // Start each task in parallel then wait for them to complete
                Collection<Boolean> results = submitConcurrentTasksAndCollateResults(tasks);

                // Ensure they are all true
                return !(results.contains(false) || results.contains(null));
            }
        });
    }

    /**
     * Submits a collection of value-returning tasks and returns a single Future representing the pending results of
     * all of the task.
     * @param tasks The tasks to submit.
     * @param <T> The type of value the tasks return.
     * @return A Future representing pending completion of the tasks.
     */
    protected <T> Future<Collection<T>> submitConcurrentAsynchronousTasks(
            final Collection<Callable<T>> tasks) {
        return submitAsynchronousTask(new Callable<Collection<T>>() {
            @Override
            public Collection<T> call() {
                return submitConcurrentTasksAndCollateResults(tasks);
            }
        });
    }

    private <T> Collection<T> submitConcurrentTasksAndCollateResults(final Collection<Callable<T>> tasks) {
        // Start each task
        Collection<Future<T>> futures = convert(tasks, new Converter<Callable<T>, Future<T>>() {
            @Override
            public Future<T> convert(Callable<T> task) {
                return submitAsynchronousTask(task);
            }
        });

        // Wait for them all to finish
        return convert(futures, new Converter<Future<T>, T>() {
            @Override
            public T convert(Future<T> future) {
                try {
                    return future.get();
                } catch (Exception e) {
                    logger.error(ERROR_BACKGROUND_PROCESS, e);
                    return null;
                }
            }
        });
    }
}
