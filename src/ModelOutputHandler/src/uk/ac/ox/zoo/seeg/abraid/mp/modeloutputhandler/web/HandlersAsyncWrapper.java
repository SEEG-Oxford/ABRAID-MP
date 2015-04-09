package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractAsynchronousActionHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * An asynchronous wrapper for the various handler classes. This allows the model output handler to respond
 * to the web service call more quickly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HandlersAsyncWrapper extends AbstractAsynchronousActionHandler {
    private static Logger logger = Logger.getLogger(HandlersAsyncWrapper.class);
    private static final int THREAD_POOL_SIZE = 1;

    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;

    @Autowired
    public HandlersAsyncWrapper(DiseaseOccurrenceHandler diseaseOccurrenceHandler) {
        super(THREAD_POOL_SIZE, logger);
        this.diseaseOccurrenceHandler = diseaseOccurrenceHandler;
    }

    /**
     * Asynchronously calls the various handlers.
     * @param modelRun The model run.
     * @return A future with a dummy value, to facilitate testing of this class.
     */
    public Future<?> handle(final ModelRun modelRun) {
        return submitAsynchronousTask(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    diseaseOccurrenceHandler.handle(modelRun);
                } catch (Exception e) {
                    logger.error(e);
                }
                return null;
            }
        });
    }
}
