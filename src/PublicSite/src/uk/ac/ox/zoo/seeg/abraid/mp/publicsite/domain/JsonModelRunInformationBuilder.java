package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.DiseaseGroupForModelRunValidator;

/**
 * Builds a JsonModelRunInformation object.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunInformationBuilder {
    private static final String DATE_FORMAT = "d MMM yyyy";
    private static final String DATE_TIME_FORMAT = "d MMM yyyy HH:mm:ss";

    private JsonModelRunInformation information;

    public JsonModelRunInformationBuilder() {
        information = new JsonModelRunInformation();
    }

    /**
     * Populates text that describes the last model run. Examples:
     *     never
     *     completed on 10 Jul 2014 03:14:32 [uses the response date]
     *     failed on 10 Jul 2014 03:14:32 [uses the response date]
     *     requested on 10 Jul 2014 03:14:32 [uses the request date]
     * @param lastRequestedModelRun The most recently-requested model run.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateLastModelRunText(ModelRun lastRequestedModelRun) {
        String text = "never";
        if (lastRequestedModelRun != null) {
            String statusText = lastRequestedModelRun.getStatus().getDisplayText();
            DateTime date = lastRequestedModelRun.getResponseDate();
            if (date == null) {
                date = lastRequestedModelRun.getRequestDate();
            }
            text = String.format("%s on %s", statusText, date.toString(DATE_TIME_FORMAT));
        }
        information.setLastModelRunText(text);
        return this;
    }

    /**
     * Populates text that describes the disease occurrences. Examples:
     *      none
     *      total 3, occurring on 9 Feb 2008
     *      total 25319, occurring between 1 Jan 2006 and 10 Jul 2014
     * @param statistics Statistics that describe the disease occurrences.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateDiseaseOccurrencesText(DiseaseOccurrenceStatistics statistics) {
        String text = "none";
        if (statistics.getOccurrenceCount() > 0) {
            String dateText = getDateText(statistics.getMinimumOccurrenceDate(), statistics.getMaximumOccurrenceDate());
            text = String.format("total %d, occurring %s", statistics.getOccurrenceCount(), dateText);
        }
        information.setDiseaseOccurrencesText(text);
        return this;
    }

    /**
     * Populates whether or not the model has ever been successfully run for the disease group.
     * @param lastCompletedModelRun The last completed model run (or null if never completed).
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateHasModelBeenSuccessfullyRun(ModelRun lastCompletedModelRun) {
        information.setHasModelBeenSuccessfullyRun(lastCompletedModelRun != null);
        return this;
    }

    /**
     * Populates whether or not the model can be run for the specified disease group, and if not why not.
     * @param diseaseGroup The disease group.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateCanRunModelWithReason(DiseaseGroup diseaseGroup) {
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);
        String errorMessage = validator.validate();
        information.setCanRunModel(errorMessage == null);
        information.setCannotRunModelReason(errorMessage);
        return this;
    }

    /**
     * Returns the built JsonModelRunInformation object.
     * @return The built JsonModelRunInformation object.
     */
    public JsonModelRunInformation get() {
        return information;
    }

    private String getDateText(DateTime startDate, DateTime endDate) {
        String startDateText = startDate.toString(DATE_FORMAT);
        String endDateText = endDate.toString(DATE_FORMAT);
        if (startDateText.equals(endDateText)) {
            return String.format("on %s", startDateText);
        } else {
            return String.format("between %s and %s", startDateText, endDateText);
        }
    }
}
