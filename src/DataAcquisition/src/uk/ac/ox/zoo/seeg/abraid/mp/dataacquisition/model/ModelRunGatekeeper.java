package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Determines whether the model run should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeper {

    /**
     * Determines whether model run preparation tasks should be carried out.
     * @param lastModelRunPrepDate The date on which the model preparation tasks were last executed.
     * @return True if there is no lastModelRunPrepDate for disease, or more than a week has passed since last run.
     */
    public boolean dueToRun(DateTime lastModelRunPrepDate) {
        if (lastModelRunPrepDate == null) {
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastModelRunPrepDate.toLocalDate().plusWeeks(1);
            return (comparisonDate.isEqual(today) || comparisonDate.isBefore(today));
        }
    }
}
