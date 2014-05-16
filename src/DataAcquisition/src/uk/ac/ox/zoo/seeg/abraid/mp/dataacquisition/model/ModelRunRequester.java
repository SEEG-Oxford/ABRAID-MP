package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRunResponse;

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

    private static Logger logger = Logger.getLogger(ModelRunRequester.class);
    private static final String WEB_SERVICE_ERROR_MESSAGE = "Error when requesting a model run: %s";

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService, DiseaseService diseaseService) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.diseaseService = diseaseService;
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void requestModelRun(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        List<DiseaseOccurrence> diseaseOccurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        Map<Integer, Integer> diseaseExtent = getDiseaseExtent(diseaseGroupId);
        DateTime requestDate = DateTime.now();

        try {
            JsonModelRunResponse response =
                    modelWrapperWebService.startRun(diseaseGroup, diseaseOccurrences, diseaseExtent);
            handleModelRunResponse(response, requestDate);
        } catch (WebServiceClientException|JsonParserException e) {
            logger.fatal(String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage()), e);
        }
    }

    private void handleModelRunResponse(JsonModelRunResponse response, DateTime requestDate) {
        if (StringUtils.hasText(response.getErrorText())) {
            logger.fatal(String.format(WEB_SERVICE_ERROR_MESSAGE, response.getErrorText()));
        } else {
            ModelRun modelRun = new ModelRun(response.getModelRunName(), requestDate);
            diseaseService.saveModelRun(modelRun);
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
