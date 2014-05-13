package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Requests a model run for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequester {
    private ModelWrapperWebService modelWrapperWebService;
    private DiseaseService diseaseService;

    private static final Logger LOGGER = Logger.getLogger(ModelRunRequester.class);
    private static final String WEB_SERVICE_ERROR_MESSAGE = "Error when requesting a model run: %s";

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService, DiseaseService diseaseService) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.diseaseService = diseaseService;
    }

    /**
     * Requests a model run for all relevant diseases.
     */
    public void requestModelRun() {
        // This is hardcoded to dengue for now
        DiseaseGroup dengue = diseaseService.getDiseaseGroupById(87);
        requestModelRun(dengue);
    }

    private void requestModelRun(DiseaseGroup diseaseGroup) {
        Integer diseaseGroupId = diseaseGroup.getId();
        List<DiseaseOccurrence> diseaseOccurrences = diseaseService.getDiseaseOccurrencesForModelRun(diseaseGroupId);
        List<Integer> diseaseExtent = getDiseaseExtent(diseaseGroupId);

        try {
            modelWrapperWebService.startRun(diseaseGroup, diseaseOccurrences, diseaseExtent);
        } catch (WebServiceClientException|JsonParserException e) {
            LOGGER.fatal(String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage()), e);
        }
    }

    private List<Integer> getDiseaseExtent(int diseaseGroupId) {
        List<AdminUnitDiseaseExtentClass> diseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        return extract(diseaseExtent,
                on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode());
    }
}
