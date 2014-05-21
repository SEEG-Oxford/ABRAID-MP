package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

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
        DateTime requestDate = DateTime.now();
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, requestDate);
        modelRunDao.save(modelRun);

        // Act
        flushAndClear();

        // Assert
        modelRun = modelRunDao.getByName(name);
        assertThat(modelRun).isNotNull();
        assertThat(modelRun.getName()).isEqualTo(name);
        assertThat(modelRun.getRequestDate()).isEqualTo(requestDate);
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
    }
}
