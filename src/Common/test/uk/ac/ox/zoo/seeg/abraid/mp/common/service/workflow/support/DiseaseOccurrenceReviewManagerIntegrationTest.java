package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the DiseaseOccurrenceReviewManager class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewManagerIntegrationTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsed() {
        executeTest(0, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAWeekHasElapsed() {
        executeTest(7, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanAWeekHasElapsed() {
        executeTest(8, true);
    }

    public void executeTest(int daysElapsed, boolean expectedValue) {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseOccurrence occurrence = createDiseaseOccurrence();
        DiseaseService diseaseService = mockDiseaseService(diseaseGroupId, occurrence);

        DiseaseOccurrenceReviewManager target = new DiseaseOccurrenceReviewManager(diseaseService);
        DateTime modelRunPrepDate = occurrence.getCreatedDate().plusDays(daysElapsed);

        // Act
        target.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);

        // Assert
        assertThat(occurrence.isValidated()).isEqualTo(expectedValue);
    }

    private DiseaseOccurrence createDiseaseOccurrence() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getAll().get(0);
        occurrence.setValidated(false);
        diseaseOccurrenceDao.save(occurrence);
        return occurrence;
    }

    private DiseaseService mockDiseaseService(int diseaseGroupId, DiseaseOccurrence occurrence) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId)).thenReturn(Arrays.asList(occurrence));
        return diseaseService;
    }
}
