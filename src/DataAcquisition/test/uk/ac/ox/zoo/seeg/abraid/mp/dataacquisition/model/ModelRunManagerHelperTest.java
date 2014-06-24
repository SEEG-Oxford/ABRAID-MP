package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ModelRunManagerHelper class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerHelperTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsed() {
        executeTest(0, false, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAWeekHasElapsed() {
        executeTest(7, false, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanAWeekHasElapsed() {
        executeTest(8, false, true);
    }

    @Test
    public void diseaseOccurrenceIsValidatedValueRemainsAsNullWhenAWeekHasNotElapsed() {
        executeTest(0, null, null);
    }

    @Test
    public void diseaseOccurrenceIsValidatedValueRemainsAsNullWhenAWeekHasElapsed() {
        executeTest(7, null, null);
    }

    @Test
    public void diseaseOccurrenceIsValidatedValueRemainsAsNullWhenMoreThanAWeekHasElapsed() {
        executeTest(8, null, null);
    }

    public void executeTest(int daysElapsed, Boolean initialIsValidatedValue, Boolean expectedValue) {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseOccurrence occurrence = createDiseaseOccurrence(initialIsValidatedValue);
        DiseaseService diseaseService = mockDiseaseService(diseaseGroupId, occurrence);

        ModelRunManagerHelper target = new ModelRunManagerHelper(diseaseService);
        DateTime modelRunPrepDate = occurrence.getCreatedDate().plusDays(daysElapsed);

        // Act
        target.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);

        // Assert
        assertThat(occurrence.isValidated()).isEqualTo(expectedValue);
    }

    private DiseaseOccurrence createDiseaseOccurrence(Boolean isValidated) {
        DiseaseOccurrence occ = diseaseOccurrenceDao.getAll().get(0);
        occ.setValidated(isValidated);
        diseaseOccurrenceDao.save(occ);
        return occ;
    }

    private DiseaseService mockDiseaseService(int diseaseGroupId, DiseaseOccurrence occurrence) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId)).thenReturn(Arrays.asList(occurrence));
        return diseaseService;
    }
}
