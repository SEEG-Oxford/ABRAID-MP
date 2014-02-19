package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ExpertDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private ExpertDao expertDao;

    @Test
    public void saveAndReloadExpert() {
        // Arrange
        String expertName = "Test Expert";
        String expertEmail = "hello@world.com";
        String expertPassword = "password";

        Expert expert = new Expert();
        expert.setName(expertName);
        expert.setEmail(expertEmail);
        expert.setPassword(expertPassword);

        // Act
        expertDao.save(expert);
        Integer id = expert.getId();
        flushAndClear();

        // Assert
        expert = expertDao.getByEmail(expertEmail);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getId()).isEqualTo(id);
        assertThat(expert.getName()).isEqualTo(expertName);
    }

    @Test
    public void loadNonExistentExpert() {
        String expertEmail = "This expert does not exist";
        Expert expert = expertDao.getByEmail(expertEmail);
        assertThat(expert).isNull();
    }
}
