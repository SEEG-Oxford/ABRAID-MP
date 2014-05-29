package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ModelRunDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Test
    public void saveAndReloadModelRunByName() {
        // Arrange
        String name = "test name";
        int diseaseGroupId = 87;
        DateTime requestDate = DateTime.now().minusHours(4);
        DateTime responseDate = DateTime.now();
        String outputText = "output text";
        String errorText = "error text";

        ModelRun modelRun = new ModelRun(name, diseaseGroupId, requestDate);
        modelRun.setResponseDate(responseDate);
        modelRun.setOutputText(outputText);
        modelRun.setErrorText(errorText);
        modelRunDao.save(modelRun);

        // Act
        flushAndClear();

        // Assert
        modelRun = modelRunDao.getByName(name);
        assertThat(modelRun).isNotNull();
        assertThat(modelRun.getName()).isEqualTo(name);
        assertThat(modelRun.getStatus()).isEqualTo(ModelRunStatus.IN_PROGRESS);
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(modelRun.getRequestDate()).isEqualTo(requestDate);
        assertThat(modelRun.getOutputText()).isEqualTo(outputText);
        assertThat(modelRun.getErrorText()).isEqualTo(errorText);
    }
}
