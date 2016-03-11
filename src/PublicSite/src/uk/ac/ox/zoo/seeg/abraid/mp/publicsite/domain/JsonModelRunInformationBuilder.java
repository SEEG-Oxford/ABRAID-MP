package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.DiseaseGroupForModelRunValidator;

import java.util.List;

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
     *     completed on 10 Jul 2014 08:00:00 (including release of 500 occurrences for validation until 31/12/2006)
     *     completed on 10 Jul 2014 06:00:00
     *     failed on 10 Jul 2014 06:00:00
     *     requested on 09 Jul 2014 21:00:00
     * @param lastRequestedModelRun The most recently-requested model run.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateLastModelRunText(ModelRun lastRequestedModelRun) {
        String text = "never";
        if (lastRequestedModelRun != null) {
            String statusText = lastRequestedModelRun.getStatus().getDisplayText();
            String dateText = getLastModelRunDateText(lastRequestedModelRun);
            String batchingText = getLastModelRunBatchingText(lastRequestedModelRun);
            text = String.format("%s on %s%s", statusText, dateText, batchingText);
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
            String dateText = getDiseaseOccurrencesDateText(
                    statistics.getMinimumOccurrenceDate(), statistics.getMaximumOccurrenceDate());
            String modelEligibleOccurrenceCount = getModelEligibilityText(statistics.getModelEligibleOccurrenceCount());
            text = String.format("total %d (of which %s), occurring %s",
                    statistics.getOccurrenceCount(), modelEligibleOccurrenceCount, dateText);
        }
        information.setDiseaseOccurrencesText(text);
        return this;
    }

    private String getModelEligibilityText(long modelEligibleOccurrenceCount) {
        if (modelEligibleOccurrenceCount == 1) {
            return "1 is a model eligible occurrence";
        } else {
            return String.format("%d are model eligible occurrences", modelEligibleOccurrenceCount);
        }
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
     * Populates the parameters relating to batch date.
     * @param lastCompletedModelRun The last completed model run (or null if never completed).
     * @param statistics Statistics that describe the disease occurrences.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateBatchDateParameters(ModelRun lastCompletedModelRun,
                                                                      DiseaseOccurrenceStatistics statistics) {
        // The minimum and maximum batch dates are equal to the minimum and maximum occurrence date
        DateTime minimumDate = statistics.getMinimumOccurrenceDate();
        DateTime maximumDate = statistics.getMaximumOccurrenceDate();

        // The default value of "batch start date" is the minimum occurrence date if this is the first
        // batch, otherwise it is the day after the latest batch end date
        DateTime defaultStartDate = minimumDate;
        if (lastCompletedModelRun != null && lastCompletedModelRun.getBatchingCompletedDate() != null &&
                lastCompletedModelRun.getBatchEndDate() != null) {
            defaultStartDate = lastCompletedModelRun.getBatchEndDate().plusDays(1);
        }

        // The default value of "batch end date" is the last day of the default start date's year, limited to
        // the maximum occurrence date
        DateTime defaultEndDate = null;
        if (defaultStartDate != null && maximumDate != null) {
            defaultEndDate = defaultStartDate.plusYears(1).withDayOfYear(1).minusDays(1);
            if (defaultEndDate.isAfter(maximumDate)) {
                defaultEndDate = maximumDate;
            }
            if (defaultStartDate.isAfter(defaultEndDate)) {
                defaultStartDate = defaultEndDate;
            }
        }

        information.setBatchDateMinimum(getDateText(minimumDate));
        information.setBatchStartDateDefault(getDateText(defaultStartDate));
        information.setBatchEndDateDefault(getDateText(defaultEndDate));
        information.setBatchDateMaximum(getDateText(maximumDate));
        return this;
    }

    /**
     * Populates whether or not the disease group has "gold standard" disease occurrences.
     * @param goldStandardOccurrences A list of "gold standard" disease occurrences.
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateHasGoldStandardOccurrences(
            List<DiseaseOccurrence> goldStandardOccurrences) {
        information.setHasGoldStandardOccurrences(goldStandardOccurrences.size() > 0);
        return this;
    }

    /**
     * Populates the text that describes the sample bias disease occurrences.
     * @param useSampleBias
     * @param bespokeBiasCount
     * @param usableBiasEstimate
     * @return This builder.
     */
    public JsonModelRunInformationBuilder populateBiasMessage(
            boolean useSampleBias, long bespokeBiasCount, long usableBiasEstimate) {
        String message = "The current model mode for this disease group does not use sample bias data.";
        if (useSampleBias) {
            message = String.format(
                    "%s bespoke sample bias data points have been provided, approximately %s %s are suitable.",
                    bespokeBiasCount,
                    usableBiasEstimate,
                    (bespokeBiasCount == 0 ? "ABRAID occurrences" : "of which"));
        }
        information.setSampleBiasText(message);
        return this;
    }

    /**
     * Returns the built JsonModelRunInformation object.
     * @return The built JsonModelRunInformation object.
     */
    public JsonModelRunInformation get() {
        return information;
    }

    private String getLastModelRunDateText(ModelRun lastRequestedModelRun) {
        DateTime date = lastRequestedModelRun.getBatchingCompletedDate();
        if (date == null) {
            date = lastRequestedModelRun.getResponseDate();
            if (date == null) {
                date = lastRequestedModelRun.getRequestDate();
            }
        }
        return getDateTimeText(date);
    }

    private String getLastModelRunBatchingText(ModelRun lastRequestedModelRun) {
        String text = "";

        if (lastRequestedModelRun.getBatchStartDate() != null && lastRequestedModelRun.getBatchEndDate() != null) {
            // Batching has been requested
            String batchStartDateText = getDateText(lastRequestedModelRun.getBatchStartDate());
            String batchEndDateText = getDateText(lastRequestedModelRun.getBatchEndDate());
            String batchDateText = String.format("start date %s, end date %s", batchStartDateText, batchEndDateText);

            if (lastRequestedModelRun.getBatchingCompletedDate() != null) {
                // Batching is complete
                int batchOccurrenceCount = lastRequestedModelRun.getBatchOccurrenceCount();
                String pluralEnding = (batchOccurrenceCount == 1) ? "" : "s";
                text = String.format(" (including batching of %d occurrence%s for validation, %s)",
                        batchOccurrenceCount, pluralEnding, batchDateText);
            } else if (lastRequestedModelRun.getStatus().equals(ModelRunStatus.COMPLETED)) {
                // Model run is complete but batching is not complete
                text = String.format(" (but batching not yet completed, %s)", batchDateText);
            }
        }

        return text;
    }

    private String getDiseaseOccurrencesDateText(DateTime startDate, DateTime endDate) {
        String startDateText = getDateText(startDate);
        String endDateText = getDateText(endDate);
        if (startDateText.equals(endDateText)) {
            return String.format("on %s", startDateText);
        } else {
            return String.format("between %s and %s", startDateText, endDateText);
        }
    }

    private String getDateText(DateTime date) {
        return (date == null) ? "" : date.toString(DATE_FORMAT);
    }

    private String getDateTimeText(DateTime dateTime) {
        return (dateTime == null) ? "" : dateTime.toString(DATE_TIME_FORMAT);
    }
}
