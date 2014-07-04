package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Provides a means for triggering model runs where the workspace setup is done asynchronously.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerAsyncWrapperImpl implements ModelRunnerAsyncWrapper {
    private static final Logger LOGGER = Logger.getLogger(ModelRunnerAsyncWrapperImpl.class);
    private static final String LOG_ERROR_DURING_THE_MODEL_SETUP =
            "An error occurred during the setup for model run: %s";

    private static final String SETUP_FAILED_MESSAGE = "Model setup failed: %s";

    private final ExecutorService pool = Executors.newFixedThreadPool(1);
    private final ModelRunner modelRunner;

    public ModelRunnerAsyncWrapperImpl(ModelRunner modelRunner) {
        this.modelRunner = modelRunner;
    }

    /**
     * Asynchronously starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @param occurrenceData The occurrence data to model with.
     * @param extentWeightings The mapping from GAUL code to disease extent class weighting.
     * @param modelStatusReporter The status reporter for to call with the results of the model or if the setup fails.
     * @return The process handler for the launched process.
     */
    @Override
    public Future<ModelProcessHandler> startModel(final RunConfiguration configuration,
                                                  final GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData,
                                                  final Map<Integer, Integer> extentWeightings,
                                                  final ModelStatusReporter modelStatusReporter) {
        return pool.submit(new Callable<ModelProcessHandler>() {
            @Override
            public ModelProcessHandler call() throws Exception {
                ModelProcessHandler handler = null;
                try {
                    handler = modelRunner.runModel(
                            configuration, occurrenceData, extentWeightings, modelStatusReporter);
                } catch (Exception e) {
                    LOGGER.error(String.format(LOG_ERROR_DURING_THE_MODEL_SETUP, configuration.getRunName()), e);
                    modelStatusReporter.report(ModelRunStatus.FAILED, "", String.format(SETUP_FAILED_MESSAGE, e));
                }
                return handler;
            }
        });
    }
}
