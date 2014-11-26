package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.List;

/**
 * Handles disease occurrences. Specifically, if a batch end date is specified in the model run, it sets the
 * "validation parameters" (e.g. environmental suitability, distance from disease extent) for the disease occurrences up
 * until the end date.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandler {
    private DiseaseOccurrenceHandlerHelper helper;

    public DiseaseOccurrenceHandler(DiseaseOccurrenceHandlerHelper helper) {
        this.helper = helper;
    }

    /**
     * Handles disease occurrences for disease group setup (i.e. automatic model runs not yet enabled).
     * @param modelRun The model run.
     */
    public void handle(ModelRun modelRun) {
        DateTime batchingInitialisationDate = helper.handle(modelRun);
        helper.continueBatchingInitialisation(modelRun.getDiseaseGroupId(), batchingInitialisationDate);
    }
}
