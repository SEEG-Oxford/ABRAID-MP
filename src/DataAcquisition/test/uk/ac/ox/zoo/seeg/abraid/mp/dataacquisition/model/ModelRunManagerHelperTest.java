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
 * Tests tge ModelRunManagerHelper
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerHelperTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    DiseaseOccurrenceDao diseaseOccurrenceDao;

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

    public void executeTest(int daysElapsed, boolean expectation) {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseOccurrence occurrence = createDiseaseOccurrence();
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId)).thenReturn(Arrays.asList(occurrence));

        ModelRunManagerHelper target = new ModelRunManagerHelper(diseaseService);
        DateTime modelRunPrepDate = occurrence.getCreatedDate().plusDays(daysElapsed);

        // Act
        target.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);

        // Assert
        assertThat(occurrence.isValidated()).isEqualTo(expectation);
    }

    private DiseaseOccurrence createDiseaseOccurrence() {
        DiseaseOccurrence occ = diseaseOccurrenceDao.getAll().get(0);
        occ.setValidated(false);
        diseaseOccurrenceDao.save(occ);
        return occ;
    }
}
