package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService, DiseaseService diseaseService,
                             ModelRunService modelRunService) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void requestModelRun(Integer diseaseGroupId) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

        if (occurrences.size() > 0) {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            Map<Integer, Integer> diseaseExtent = getDiseaseExtent(diseaseGroupId);
            DateTime requestDate = DateTime.now();

            try {
                logRequest(diseaseGroup, occurrences);
                JsonModelRunResponse response =
                        modelWrapperWebService.startRun(diseaseGroup, occurrences, diseaseExtent);
                handleModelRunResponse(response, diseaseGroupId, requestDate);
            } catch (WebServiceClientException|JsonParserException e) {
                String message = String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage());
                throw new ModelRunManagerException(message, e);
            }
        }
    }

    private void logRequest(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> diseaseOccurrences) {
        LOGGER.info(String.format(REQUEST_LOG_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                diseaseOccurrences.size()));
    }

    private void handleModelRunResponse(JsonModelRunResponse response, Integer diseaseGroupId, DateTime requestDate) {
        if (StringUtils.hasText(response.getErrorText())) {
            String message = String.format(WEB_SERVICE_ERROR_MESSAGE, response.getErrorText());
            throw new ModelRunManagerException(message);
        } else {
            ModelRun modelRun = new ModelRun(response.getModelRunName(), diseaseGroupId, requestDate);
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
