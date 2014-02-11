package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

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
        String expertName = "Test Expert";
        String expertEmail = "hello@world.com";
        String expertPassword = "password";

        // Creates and saves an expert
        Expert expert = new Expert();
        expert.setName(expertName);
        expert.setEmail(expertEmail);
        expert.setPassword(expertPassword);
        expertDao.save(expert);
        Integer id = expert.getId();
        flushAndClear();

        // Reloads the same expert and verifies its properties
        expert = expertDao.getByName(expertName);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getId()).isEqualTo(id);
        assertThat(expert.getName()).isEqualTo(expertName);
    }

    @Test
    public void loadNonExistentExpert() {
        String expertName = "This expert does not exist";
        Expert expert = expertDao.getByName(expertName);
        assertThat(expert).isNull();
    }
}
