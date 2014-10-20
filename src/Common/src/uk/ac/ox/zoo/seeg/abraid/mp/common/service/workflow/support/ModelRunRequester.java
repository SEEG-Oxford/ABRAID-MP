package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

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
    private Collection<URI> modelWrapperUrlCollection;
    private URI modelWrapperUrl;

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService, DiseaseService diseaseService,
                             ModelRunService modelRunService, String[] modelWrapperUrlCollection) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
        this.modelWrapperUrlCollection = convert(modelWrapperUrlCollection, new Converter<String, URI>(){
            @Override
            public URI convert(String url) {
                return URI.create(url);
            }
        });
        if (this.modelWrapperUrlCollection.isEmpty()) {
            throw new IllegalArgumentException("At least 1 ModelWrapper URL must be provided.");
        }
        this.modelWrapperUrl = this.modelWrapperUrlCollection.iterator().next();
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param occurrencesForModelRun The disease occurrences to send to the model.
     * @param batchEndDate If validator parameter batching should happen after the model run is completed,
     * this is the end date for batching. Otherwise null.
     * @throws ModelRunRequesterException if the model run could not be requested.
     */
    public void requestModelRun(int diseaseGroupId, List<DiseaseOccurrence> occurrencesForModelRun,
                                DateTime batchEndDate) throws ModelRunRequesterException {
        if (occurrencesForModelRun != null && occurrencesForModelRun.size() > 0) {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            Map<Integer, Integer> diseaseExtent = getDiseaseExtent(diseaseGroupId);
            DateTime requestDate = DateTime.now();

            try {
                logRequest(diseaseGroup, occurrencesForModelRun);
                JsonModelRunResponse response =
                        modelWrapperWebService.startRun(modelWrapperUrl, diseaseGroup, occurrencesForModelRun, diseaseExtent);
                handleModelRunResponse(response, diseaseGroupId, requestDate, modelWrapperUrl.getHost(), batchEndDate);
            } catch (WebServiceClientException|JsonParserException e) {
                String message = String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage());
                LOGGER.error(message);
                throw new ModelRunRequesterException(message, e);
            }
        }
    }

    private void logRequest(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> diseaseOccurrences) {
        LOGGER.info(String.format(REQUEST_LOG_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                diseaseOccurrences.size()));
    }

    private void handleModelRunResponse(JsonModelRunResponse response, int diseaseGroupId, DateTime requestDate,
                                        String requestServer, DateTime batchEndDate) {
        if (StringUtils.hasText(response.getErrorText())) {
            String message = String.format(WEB_SERVICE_ERROR_MESSAGE, response.getErrorText());
            LOGGER.error(message);
            throw new ModelRunRequesterException(message);
        } else {
            ModelRun modelRun = new ModelRun(response.getModelRunName(), diseaseGroupId, requestServer, requestDate);
            modelRun.setBatchEndDate(batchEndDate);
            modelRunService.saveModelRun(modelRun);
        }
    }

    private Map<Integer, Integer> getDiseaseExtent(int diseaseGroupId) {
        List<AdminUnitDiseaseExtentClass> diseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Create a mapping of GAUL codes to extent class weightings
        Map<Integer, Integer> extentMapping = new HashMap<>();
        for (AdminUnitDiseaseExtentClass extentClass : diseaseExtent) {
            extentMapping.put(extentClass.getAdminUnitGlobalOrTropical().getGaulCode(),
                    extentClass.getDiseaseExtentClass().getWeighting());
        }

        return extentMapping;
    }
}
