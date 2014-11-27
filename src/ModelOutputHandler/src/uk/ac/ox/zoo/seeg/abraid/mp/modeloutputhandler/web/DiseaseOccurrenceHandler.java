package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

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
