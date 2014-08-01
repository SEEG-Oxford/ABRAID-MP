package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An asynchronous wrapper for the various handler classes. This allows the model output handler to respond
 * to the web service call more quickly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HandlersAsyncWrapper {
    private static final Logger LOGGER = Logger.getLogger(HandlersAsyncWrapper.class);
    private final ExecutorService pool = Executors.newFixedThreadPool(1);

    private DiseaseExtentGenerationHandler diseaseExtentGenerationHandler;
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;

    @Autowired
    public HandlersAsyncWrapper(DiseaseExtentGenerationHandler diseaseExtentGenerationHandler,
                                DiseaseOccurrenceHandler diseaseOccurrenceHandler) {
        this.diseaseExtentGenerationHandler = diseaseExtentGenerationHandler;
        this.diseaseOccurrenceHandler = diseaseOccurrenceHandler;
    }

    /**
     * Asynchronously calls the various handlers.
     * @param modelRun The model run.
     * @return A future with a dummy value, to facilitate testing of this class.
     */
    public Future<?> handle(final ModelRun modelRun) {
        return pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    diseaseExtentGenerationHandler.handle(modelRun);
                    diseaseOccurrenceHandler.handle(modelRun);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                return null;
            }
        });
    }
}
