package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

/**
 * Determines whether the model run should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeper {

    private DiseaseService diseaseService;

    ModelRunGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run preparation tasks should be carried out.
     * @param lastModelRunPrepDate The date on which the model preparation tasks were last executed.
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if there is no lastModelRunPrepDate for disease, or more than a week has passed since last run, or
     * there have been more new occurrences since the last run than the minimum required for the disease group.
     * False if the minimum number of new occurrences value is not specified for the disease group.
     */
    public boolean dueToRun(DateTime lastModelRunPrepDate, int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup.getModelRunMinNewOccurrences() == null) {
            return false;
        } else {
            return weekHasElapsed(lastModelRunPrepDate) || enoughNewOccurrences(diseaseGroup);
        }
    }

    private boolean weekHasElapsed(DateTime lastModelRunPrepDate) {
        if (lastModelRunPrepDate == null) {
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastModelRunPrepDate.toLocalDate().plusWeeks(1);
            return (comparisonDate.isEqual(today) || comparisonDate.isBefore(today));
        }
    }

    private boolean enoughNewOccurrences(DiseaseGroup diseaseGroup) {
        long count = diseaseService.getNewOccurrencesCountByDiseaseGroup(diseaseGroup.getId());
        int min = diseaseGroup.getModelRunMinNewOccurrences();
        return (count > min);
    }
}
