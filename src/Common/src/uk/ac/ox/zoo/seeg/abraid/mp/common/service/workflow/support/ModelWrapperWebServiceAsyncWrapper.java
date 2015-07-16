package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractAsynchronousActionHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.net.URI;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.convert;

/**
 * Provides a means for interacting with ModelWrapper on all known instances, in a non-blocking fashion.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebServiceAsyncWrapper extends AbstractAsynchronousActionHandler {
    private static final Logger LOGGER = Logger.getLogger(ModelWrapperWebServiceAsyncWrapper.class);
    private static final int THREAD_POOL_SIZE = 5;
    private final ModelWrapperWebService modelWrapperWebService;
    private final Collection<URI> modelWrapperUrls;

    public ModelWrapperWebServiceAsyncWrapper(
            ModelWrapperWebService modelWrapperWebService, String[] modelWrapperUrlCollection) {
        super(THREAD_POOL_SIZE, LOGGER);
        this.modelWrapperWebService = modelWrapperWebService;
        this.modelWrapperUrls = convert(modelWrapperUrlCollection, new Converter<String, URI>() {
            @Override
            public URI convert(String url) {
                return URI.create(url);
            }
        });
    }

    /**
     * Publish a given disease group to all known model wrapper instances, as a background process.
     * @param diseaseGroup The disease group to publish.
     * @return A Future representing pending completion of the background process, with the value 'true' if the
     * background process succeed for all model wrapper instances.
     */
    public Future<Boolean> publishSingleDisease(final DiseaseGroup diseaseGroup) {
        return submitConcurrentAsynchronousTasksWithAggregateResult(
            convert(modelWrapperUrls, new Converter<URI, Callable<Boolean>>() {
                @Override
                public Callable<Boolean> convert(final URI modelWrapperUrl) {
                    return new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                modelWrapperWebService.publishSingleDisease(modelWrapperUrl, diseaseGroup);
                            } catch (WebServiceClientException e) {
                                LOGGER.error(e);
                                return false;
                            }
                            return true;
                        }
                    };
                }
            })
        );
    }

    /**
     * Publish a collection of disease groups to all known model wrapper instances, as a background process.
     * @param diseaseGroups The disease groups to publish.
     * @return A Future representing pending completion of the background process, with the value 'true' if the
     * background process succeed for all model wrapper instances.
     */
    public Future<Boolean> publishAllDiseases(final Collection<DiseaseGroup> diseaseGroups) {
        return submitConcurrentAsynchronousTasksWithAggregateResult(
            convert(modelWrapperUrls, new Converter<URI, Callable<Boolean>>() {
                @Override
                public Callable<Boolean> convert(final URI modelWrapperUrl) {
                    return new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                modelWrapperWebService.publishAllDiseases(modelWrapperUrl, diseaseGroups);
                            } catch (WebServiceClientException e) {
                                LOGGER.error(e);
                                return false;
                            }
                            return true;
                        }
                    };
                }
            })
        );
    }
}