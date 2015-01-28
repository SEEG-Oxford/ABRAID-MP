package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.function.matcher.LambdaJMatcher;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.net.URI;
import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Requests a model run for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequester {
    private ModelWrapperWebService modelWrapperWebService;
    private DiseaseService diseaseService;
    private ModelRunService modelRunService;

    private static final Logger LOGGER = Logger.getLogger(ModelRunRequester.class);
    private static final String WEB_SERVICE_ERROR_MESSAGE = "Error when requesting a model run: %s";
    private static final String REQUEST_LOG_MESSAGE =
            "Requesting a model run for disease group %d (%s) with %d disease occurrence(s)";
    private static final String NO_OCCURRENCES_MESSAGE = "Cannot request a model run because there are no occurrences";
    private List<URI> modelWrapperUrlCollection;

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService, DiseaseService diseaseService,
                             ModelRunService modelRunService, String[] modelWrapperUrlCollection) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
        this.modelWrapperUrlCollection = convert(modelWrapperUrlCollection, new Converter<String, URI>() {
            @Override
            public URI convert(String url) {
                return URI.create(url);
            }
        });
        if (this.modelWrapperUrlCollection.isEmpty()) {
            throw new IllegalArgumentException("At least 1 ModelWrapper URL must be provided.");
        }
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param occurrencesForModelRun The disease occurrences to send to the model.
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null.
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    public void requestModelRun(int diseaseGroupId, List<DiseaseOccurrence> occurrencesForModelRun,
                                DateTime batchStartDate, DateTime batchEndDate) throws ModelRunWorkflowException {
        if (occurrencesForModelRun != null && occurrencesForModelRun.size() > 0) {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            Collection<AdminUnitDiseaseExtentClass> diseaseExtent =
                    diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
            Map<Integer, Integer> diseaseExtentMap = extractDiseaseExtentMap(diseaseExtent);
            DateTime requestDate = DateTime.now();

            try {
                logRequest(diseaseGroup, occurrencesForModelRun);
                URI modelWrapperUrl = selectLeastBusyModelWrapperUrl();
                ModelRun modelRun = createPreliminaryModelRun(diseaseGroup, requestDate, modelWrapperUrl,
                        batchStartDate, batchEndDate, occurrencesForModelRun, diseaseExtent);
                JsonModelRunResponse response = modelWrapperWebService.startRun(modelWrapperUrl, diseaseGroup,
                        occurrencesForModelRun, diseaseExtentMap);
                handleModelRunResponse(response, modelRun);
            } catch (WebServiceClientException|JsonParserException e) {
                String message = String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage());
                LOGGER.error(message);
                throw new ModelRunWorkflowException(message, e);
            }
        } else {
            throw new ModelRunWorkflowException(NO_OCCURRENCES_MESSAGE);
        }
    }

    private ModelRun createPreliminaryModelRun(DiseaseGroup diseaseGroup, DateTime requestDate, URI modelWrapperUrl,
                                               DateTime batchStartDate, DateTime batchEndDate,
                                               List<DiseaseOccurrence> occurrencesForModelRun,
                                               Collection<AdminUnitDiseaseExtentClass> diseaseExtent) {
        final ModelRun modelRun = new ModelRun(null, diseaseGroup.getId(), modelWrapperUrl.getHost(), requestDate,
                min(occurrencesForModelRun, on(DiseaseOccurrence.class).getOccurrenceDate()),
                max(occurrencesForModelRun, on(DiseaseOccurrence.class).getOccurrenceDate()));
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setInputDiseaseExtent(convert(diseaseExtent,
            new Converter<AdminUnitDiseaseExtentClass, ModelRunAdminUnitDiseaseExtentClass>() {
                @Override
                public ModelRunAdminUnitDiseaseExtentClass convert(AdminUnitDiseaseExtentClass adminUnitExtentClass) {
                    return new ModelRunAdminUnitDiseaseExtentClass(adminUnitExtentClass, modelRun);
                }
            }
        ));
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            modelRun.setInputDiseaseOccurrences(occurrencesForModelRun);
        }
        return modelRun;
    }

    private URI selectLeastBusyModelWrapperUrl() {
        Stack<String> usedHostsWithBusiestAtTop = new Stack<>();
        usedHostsWithBusiestAtTop.addAll(modelRunService.getModelRunRequestServersByUsage());

        List<URI> availableHostsInPreferenceOrder = modelWrapperUrlCollection;

        // Until only 1 available host remains, or there are no more used hosts remaining
        while (availableHostsInPreferenceOrder.size() > 1 && usedHostsWithBusiestAtTop.size() > 0) {
            // Remove the busiest of the used hosts from the list of available hosts (assuming it's present)
            final String busiestHost = usedHostsWithBusiestAtTop.pop();
            availableHostsInPreferenceOrder = filter(new LambdaJMatcher<URI>() {
                @Override
                public boolean matches(Object host) {
                    return !((URI) host).getHost().equals(busiestHost);
                }
            }, availableHostsInPreferenceOrder);
        }

        // If only 1 host remains use that one, if multiple hosts remain just use the first (preferred) one.
        return availableHostsInPreferenceOrder.get(0);
    }

    private void logRequest(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> diseaseOccurrences) {
        LOGGER.info(String.format(REQUEST_LOG_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                diseaseOccurrences.size()));
    }

    private void handleModelRunResponse(JsonModelRunResponse response, ModelRun modelRun) {
        if (StringUtils.hasText(response.getErrorText())) {
            String message = String.format(WEB_SERVICE_ERROR_MESSAGE, response.getErrorText());
            LOGGER.error(message);
            throw new ModelRunWorkflowException(message);
        } else {
            modelRun.setName(response.getModelRunName());
            modelRunService.saveModelRun(modelRun);
        }
    }

    private Map<Integer, Integer> extractDiseaseExtentMap(Collection<AdminUnitDiseaseExtentClass> diseaseExtent) {
        // Create a mapping of GAUL codes to extent class weightings
        Map<Integer, Integer> extentMapping = new HashMap<>();
        for (AdminUnitDiseaseExtentClass extentClass : diseaseExtent) {
            extentMapping.put(extentClass.getAdminUnitGlobalOrTropical().getGaulCode(),
                    extentClass.getDiseaseExtentClass().getWeighting());
        }

        return extentMapping;
    }
}
