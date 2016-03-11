package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("requested on 1 Jul 2014 08:07:06");
    }

    @Test
    public void populateLastModelRunTextWhenCompletedWithNoBatching() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(new DateTime("2014-07-02T09:08:07"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("completed on 2 Jul 2014 09:08:07");
    }

    @Test
    public void populateLastModelRunTextWhenCompletedWithBatchingCompleted() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(new DateTime("2014-07-02T09:08:07"));
        modelRun.setBatchStartDate(new DateTime("2006-12-30T04:05:06"));
        modelRun.setBatchEndDate(new DateTime("2006-12-31T04:05:06"));
        modelRun.setBatchingCompletedDate(new DateTime("2014-07-02T19:18:17"));
        modelRun.setBatchOccurrenceCount(1500);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo(
                "completed on 2 Jul 2014 19:18:17 (including batching of 1500 occurrences for validation, start date 30 Dec 2006, end date 31 Dec 2006)");
    }

    @Test
    public void populateLastModelRunTextWhenCompletedWithBatchingIncomplete() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(new DateTime("2014-07-02T09:08:07"));
        modelRun.setBatchStartDate(new DateTime("2006-12-30T00:00:00"));
        modelRun.setBatchEndDate(new DateTime("2006-12-31T23:59:59.999"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo(
                "completed on 2 Jul 2014 09:08:07 (but batching not yet completed, start date 30 Dec 2006, end date 31 Dec 2006)");
    }

    @Test
    public void populateLastModelRunTextWhenFailedAndBatchingRequested() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
        modelRun.setStatus(ModelRunStatus.FAILED);
        modelRun.setResponseDate(new DateTime("2014-07-02T13:08:07"));
        modelRun.setBatchStartDate(new DateTime("2006-12-30T00:00:00"));
        modelRun.setBatchEndDate(new DateTime("2006-12-31T23:59:59.999"));
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateLastModelRunText(modelRun).get();

        // Assert
        assertThat(information.getLastModelRunText()).isEqualTo("failed on 2 Jul 2014 13:08:07");
    }

    @Test
    public void populateLastModelRunTextWhenFailedAndBatchingNotRequested() {
        // Arrange
        ModelRun modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
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
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, 0, null, null);
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
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(3, 1, dateTime, dateTime);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateDiseaseOccurrencesText(statistics).get();

        // Assert
        assertThat(information.getDiseaseOccurrencesText()).isEqualTo("total 3 (of which 1 is a model eligible occurrence), occurring on 1 Jul 2014");
    }

    @Test
    public void populateDiseaseOccurrencesTextWithOccurrencesWithDifferentStartAndEndDate() {
        // Arrange
        DateTime startDate = new DateTime("2013-02-05T13:07:06");
        DateTime endDate = new DateTime("2014-06-01T09:15:00");
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(3, 2, startDate, endDate);
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateDiseaseOccurrencesText(statistics).get();

        // Assert
        assertThat(information.getDiseaseOccurrencesText()).isEqualTo(
                "total 3 (of which 2 are model eligible occurrences), occurring between 5 Feb 2013 and 1 Jun 2014");
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
        populateBatchDateParameters(null, null, null, "", "", "", "");
    }

    @Test
    public void populateBatchEndDateParametersForFirstBatch() {
        populateBatchDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", null,
                "5 Feb 2011", "5 Feb 2011", "31 Dec 2011", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForNextBatchWherePreviousBatchEndedAtYearEnd() {
        populateBatchDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2011-12-31",
                "5 Feb 2011", "1 Jan 2012", "31 Dec 2012", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForNextBatchWherePreviousBatchEndedWithinTheYear() {
        populateBatchDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2011-10-05",
                "5 Feb 2011", "6 Oct 2011", "31 Dec 2011", "1 Jun 2013");
    }

    @Test
    public void populateBatchEndDateParametersForFinalBatchWhereLastOccurrenceEndsWellBeforeNow() {
        populateBatchDateParameters("2011-02-05T13:07:06", "2013-06-01T09:15:00", "2012-12-31",
                "5 Feb 2011", "1 Jan 2013", "1 Jun 2013", "1 Jun 2013");
    }

    @Test
    public void populateHasGoldStandardOccurrencesWhenItDoesHave() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();
        List<DiseaseOccurrence> occurrences = Arrays.asList(new DiseaseOccurrence());

        // Act
        JsonModelRunInformation information = builder.populateHasGoldStandardOccurrences(occurrences).get();

        // Assert
        assertThat(information.isHasGoldStandardOccurrences()).isTrue();
    }

    @Test
    public void populateHasGoldStandardOccurrencesWhenItDoesNotHave() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();
        List<DiseaseOccurrence> occurrences = Arrays.asList();

        // Act
        JsonModelRunInformation information = builder.populateHasGoldStandardOccurrences(occurrences).get();

        // Assert
        assertThat(information.isHasGoldStandardOccurrences()).isFalse();
    }

    @Test
    public void populateBiasMessageWhenNotUsing() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateBiasMessage(false, 4, 4).get();

        // Assert
        assertThat(information.getSampleBiasText()).isEqualTo("The current model mode for this disease group does not use sample bias data.");
    }

    @Test
    public void populateBiasMessageWhenBespoke() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();
        // Act
        JsonModelRunInformation information = builder.populateBiasMessage(true, 4, 0).get();

        // Assert
        assertThat(information.getSampleBiasText()).isEqualTo("4 bespoke sample bias data points have been provided, approximately 0 of which are suitable.");
    }

    @Test
    public void populateBiasMessageWhenDefault() {
        // Arrange
        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateBiasMessage(true, 0, 4).get();

        // Assert
        assertThat(information.getSampleBiasText()).isEqualTo("0 bespoke sample bias data points have been provided, approximately 4 ABRAID occurrences are suitable.");
    }

    private DiseaseGroup createMockDiseaseGroup(int id) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }

    private DiseaseGroup createValidDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);
        diseaseGroup.setName("Test name");
        diseaseGroup.setPublicName("Test public name");
        diseaseGroup.setShortName("Test short name");
        diseaseGroup.setAbbreviation("Test abbreviation");
        diseaseGroup.setGlobal(false);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup());
        diseaseGroup.setDiseaseExtentParameters(new DiseaseExtent(new DiseaseGroup(), 0.6, 36, 1, 2));
        return diseaseGroup;
    }

    private void populateBatchDateParameters(String occurrenceStartDate, String occurrenceEndDate,
                                             String previousBatchEndDate, String expectedBatchDateMinimum,
                                             String expectedBatchStartDateDefault, String expectedBatchEndDateDefault,
                                             String expectedBatchDateMaximum) {
        // Arrange
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(100, 50, getDate(occurrenceStartDate),
                getDate(occurrenceEndDate));

        DateTime now = new DateTime("2014-07-29T09:10:11");
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        ModelRun modelRun = null;
        if (previousBatchEndDate != null) {
            modelRun = new ModelRun("name", createMockDiseaseGroup(87), "host", new DateTime("2014-07-01T08:07:06"), DateTime.now(), DateTime.now());
            modelRun.setBatchEndDate(getDate(previousBatchEndDate));
            modelRun.setBatchingCompletedDate(now.minusHours(2));
        }

        JsonModelRunInformationBuilder builder = new JsonModelRunInformationBuilder();

        // Act
        JsonModelRunInformation information = builder.populateBatchDateParameters(modelRun, statistics).get();

        // Assert
        assertThat(information.getBatchDateMinimum()).isEqualTo(expectedBatchDateMinimum);
        assertThat(information.getBatchStartDateDefault()).isEqualTo(expectedBatchStartDateDefault);
        assertThat(information.getBatchEndDateDefault()).isEqualTo(expectedBatchEndDateDefault);
        assertThat(information.getBatchDateMaximum()).isEqualTo(expectedBatchDateMaximum);
    }

    private DateTime getDate(String dateText) {
        return (dateText == null) ? null : new DateTime(dateText);
    }
}
