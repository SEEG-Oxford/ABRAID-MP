package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the JsonModelRunInformationBuilder class.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunInformationBuilderTest {
    @Test
    public void populateLastModelRunTextWhenModelNeverRun() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(null).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("never");
    }

    @Test
    public void populateLastModelRunTextWhenInProgress() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", 87, new DateTime("2014-07-01T08:07:06"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("requested on 1 Jul 2014 08:07:06");
    }

    @Test
    public void populateLastModelRunTextWhenCompletedWithNoBatching() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", 87, new DateTime("2014-07-01T08:07:06"));
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(new DateTime("2014-07-02T09:08:07"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("completed on 2 Jul 2014 09:08:07");
    }

    @Test
    public void populateLastModelRunTextWhenCompletedWithBatching() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", 87, new DateTime("2014-07-01T08:07:06"));
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(new DateTime("2014-07-02T09:08:07"));
        modelRun.setBatchEndDate(new DateTime("2006-12-31T04:05:06"));
        modelRun.setBatchingCompletedDate(new DateTime("2014-07-02T19:18:17"));
        modelRun.setBatchOccurrenceCount(1500);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo(
                "completed on 2 Jul 2014 19:18:17 (including batching of 1500 occurrences for validation, end date 31 Dec 2006)");
    }

    @Test
    public void populateLastModelRunTextWhenFailed() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", 87, new DateTime("2014-07-01T08:07:06"));
        modelRun.setStatus(ModelRunStatus.FAILED);
        modelRun.setResponseDate(new DateTime("2014-07-02T13:08:07"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("failed on 2 Jul 2014 13:08:07");
    }

    @Test
    public void populateDiseaseOccurrencesTextWhenNoOccurrences() {
        // Arrange
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, null, null);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateDiseaseOccurrencesText(statistics).get();

        // Assert
        assertThat(information.getDiseaseOccurrencesText()).isEqualTo("none");
    }

    @Test
    public void populateDiseaseOccurrencesTextWithOccurrencesWithSameStartAndEndDate() {
        // Arrange
        DateTime dateTime = new DateTime("2014-07-01T13:07:06");
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(3, dateTime, dateTime);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateDiseaseOccurrencesText(statistics).get();

        // Assert
        assertThat(information.getDiseaseOccurrencesText()).isEqualTo("total 3, occurring on 1 Jul 2014");
    }

    @Test
    public void populateDiseaseOccurrencesTextWithOccurrencesWithDifferentStartAndEndDate() {
        // Arrange
        DateTime startDate = new DateTime("2013-02-05T13:07:06");
        DateTime endDate = new DateTime("2014-06-01T09:15:00");
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(3, startDate, endDate);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateDiseaseOccurrencesText(statistics).get();

        // Assert
        assertThat(information.getDiseaseOccurrencesText()).isEqualTo(
                "total 3, occurring between 5 Feb 2013 and 1 Jun 2014");
    }

    @Test
    public void populateHasModelBeenSuccessfullyRunWhenModelHas() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateHasModelBeenSuccessfullyRun(new ModelRun()).get();

        // Assert
        assertThat(information.isHasModelBeenSuccessfullyRun()).isTrue();
    }

    @Test
    public void populateHasModelBeenSuccessfullyRunWhenModelHasNot() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateHasModelBeenSuccessfullyRun(null).get();

        // Assert
        assertThat(information.isHasModelBeenSuccessfullyRun()).isFalse();
    }

    @Test
    public void populateCanRunModelWithReasonWhenModelCanBeRun() {
        // Arrange
        DiseaseGroup diseaseGroup = createValidDiseaseGroup();
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateCanRunModelWithReason(diseaseGroup).get();

        // Assert
        assertThat(information.isCanRunModel()).isTrue();
        assertThat(information.getCannotRunModelReason()).isNull();
    }

    @Test
    public void populateCanRunModelWithReasonWhenModelCannotBeRun() {
        // Arrange
        DiseaseGroup diseaseGroup = createValidDiseaseGroup();
        diseaseGroup.setPublicName(null);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateCanRunModelWithReason(diseaseGroup).get();

        // Assert
        assertThat(information.isCanRunModel()).isFalse();
        assertThat(information.getCannotRunModelReason()).isEqualTo("the public name is missing");
    }

    @Test
    public void populateBatchEndDateParametersWithNullOccurrenceDates() {
        populateBatchEndDateParameters(null, null, null, "", "", "");
    }

    @Test
    public void populateBatchEndDateParametersForFirstBatch() {
        populateBatchEndDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", null,
                "5 Feb 2011", "31 Dec 2011", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForNextBatchWherePreviousBatchEndedAtYearEnd() {
        populateBatchEndDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2011-12-31",
                "1 Jan 2012", "31 Dec 2012", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForNextBatchWherePreviousBatchEndedWithinTheYear() {
        populateBatchEndDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2011-10-05",
                "6 Oct 2011", "31 Dec 2011", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForFinalBatchWhereLastOccurrenceEndsWellBeforeNow() {
        populateBatchEndDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2012-12-31",
                "1 Jan 2013", "1 Jun 2013", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForFinalBatchWhereLastOccurrenceEndsWithinAWeekBeforeNow() {
        populateBatchEndDateParameters("2011-02-05T13:07:06", "2014-07-25T00:00:00", "2013-12-31",
                "1 Jan 2014", "22 Jul 2014", "25 Jul 2014");
    }

    private DiseaseGroup createValidDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);
        diseaseGroup.setName("Test name");
        diseaseGroup.setPublicName("Test public name");
        diseaseGroup.setShortName("Test short name");
        diseaseGroup.setAbbreviation("Test abbreviation");
        diseaseGroup.setGlobal(false);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup());
        diseaseGroup.setDiseaseExtentParameters(new DiseaseExtent(new DiseaseGroup(), 60, 0.6, 3, 1, 36, 1, 2));
        return diseaseGroup;
    }

    private void populateBatchEndDateParameters(String occurrenceStartDate, String occurrenceEndDate,
                                                String previousBatchEndDate, String expectedBatchEndDateMinimum,
                                                String expectedBatchEndDateDefault, String expectedBatchEndDateMaximum) {
        // Arrange
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(100, getDate(occurrenceStartDate),
                getDate(occurrenceEndDate));

        DateTime now = new DateTime("2014-07-29T09:10:11");
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        ModelRun modelRun = null;
        if (previousBatchEndDate != null) {
            modelRun = new ModelRun("name", 87, new DateTime("2014-07-01T08:07:06"));
            modelRun.setBatchEndDate(getDate(previousBatchEndDate));
            modelRun.setBatchingCompletedDate(now.minusHours(2));
        }

        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateBatchEndDateParameters(modelRun, statistics).get();

        // Assert
        assertThat(information.getBatchEndDateMinimum()).isEqualTo(expectedBatchEndDateMinimum);
        assertThat(information.getBatchEndDateDefault()).isEqualTo(expectedBatchEndDateDefault);
        assertThat(information.getBatchEndDateMaximum()).isEqualTo(expectedBatchEndDateMaximum);
    }

    private DateTime getDate(String dateText) {
        return (dateText == null) ? null : new DateTime(dateText);
    }
}
