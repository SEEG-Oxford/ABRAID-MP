package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

/**
 * Handles the getting and setting of disease groups' lastModelRunPrepDate.
 * Copyright (c) 2014 University of Oxford
 */
public class LastModelRunPrepDateManager {

    private DiseaseService diseaseService;

    LastModelRunPrepDateManager(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Gets the date on which the model was last run for the specified disease group.
     * @param diseaseGroupId The ID of the disease group for which the model run is being prepared.
     * @return The date.
     */
    public DateTime getDate(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        return diseaseGroup.getLastModelRunPrepDate();
    }

    /**
     * Having finished necessary preparatory actions for the disease group, save the time at which preparation started.
     * @param modelRunPrepStartTime The time at which this week's prep process started.
     * @param diseaseGroupId The ID of the disease group for which the model run is being prepared.
     */
    public void saveDate(DateTime modelRunPrepStartTime, int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepStartTime);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }
}
