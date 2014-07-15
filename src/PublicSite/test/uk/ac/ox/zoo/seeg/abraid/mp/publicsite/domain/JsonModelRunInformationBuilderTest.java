package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
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
    public void populateLastModelRunTextWhenCompleted() {
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

    private DiseaseGroup createValidDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);
        diseaseGroup.setName("Test name");
        diseaseGroup.setPublicName("Test public name");
        diseaseGroup.setShortName("Test short name");
        diseaseGroup.setAbbreviation("Test abbreviation");
        diseaseGroup.setGlobal(false);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup());
        return diseaseGroup;
    }
}
