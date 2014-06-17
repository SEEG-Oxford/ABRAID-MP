package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An asynchronous wrapper for the ValidationParametersHandler class. This allows the model output handler to respond
 * to the web service call more quickly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationParametersHandlerAsyncWrapper {
    private static final Logger LOGGER = Logger.getLogger(ValidationParametersHandlerAsyncWrapper.class);
    private static final String LOG_ERROR =
            "An error occurred while handling validation parameters for model run ID %d: %s";

    private final ExecutorService pool = Executors.newFixedThreadPool(1);
    private final ValidationParametersHandler handler;

    public ValidationParametersHandlerAsyncWrapper(ValidationParametersHandler handler) {
        this.handler = handler;
    }

    /**
     * Asynchronously handles validation parameters.
     * @param modelRun The model run.
     * @return A future with a dummy value, to facilitate testing of this class.
     */
    public Future<?> handleValidationParameters(final ModelRun modelRun) {
        return pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    handler.handleValidationParameters(modelRun);
                } catch (Exception e) {
                    LOGGER.error(String.format(LOG_ERROR, modelRun.getId(), e.getMessage()), e);
                }
                return null;
            }
        });
    }
}
