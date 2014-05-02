package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ExpertDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertDaoTest extends AbstractCommonSpringIntegrationTests {
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

        // Assert
        assertThat(expert.getCreatedDate()).isNotNull();

        Integer id = expert.getId();
        flushAndClear();
        expert = expertDao.getByEmail(expertEmail);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getId()).isEqualTo(id);
        assertThat(expert.getName()).isEqualTo(expertName);
        assertThat(expert.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadExpertOnId() {
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

        // Assert
        assertThat(expert.getCreatedDate()).isNotNull();

        Integer id = expert.getId();
        flushAndClear();

        expert = expertDao.getById(id);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getEmail()).isEqualTo(expertEmail);
        assertThat(expert.getName()).isEqualTo(expertName);
        assertThat(expert.getCreatedDate()).isNotNull();
    }

    @Test
    public void loadNonExistentExpert() {
        // Arrange
        String expertEmail = "This expert does not exist";

        // Act
        Expert expert = expertDao.getByEmail(expertEmail);

        // Assert
        assertThat(expert).isNull();
    }

    @Test
    public void getAllExperts() {
        // Act
        List<Expert> experts = expertDao.getAll();

        // Assert
        assertThat(experts).hasSize(2);
    }

    @Test
    public void getExpertByIdReturnsExpertIfItExists() {
        // Act
        Expert expert = expertDao.getById(1);

        // Assert
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isEqualTo(1);
        assertThat(expert.getName()).isEqualTo("Helena Patching");
        // Upon execution of the next line, the lazily-loaded validatorDiseaseGroups set is actually loaded
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = expert.getValidatorDiseaseGroups();
        assertThat(validatorDiseaseGroups).hasSize(2);
    }

    @Test
    public void getExpertByIdReturnsNullIfItDoesNotExist() {
        // Act
        Expert expert = expertDao.getById(-1);

        // Assert
        assertThat(expert).isNull();
    }
}
