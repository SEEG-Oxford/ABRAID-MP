package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ValidatorDiseaseGroupDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidatorDiseaseGroupDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;

    @Test
    public void saveThenGetById() {
        // Arrange
        ValidatorDiseaseGroup group = new ValidatorDiseaseGroup();
        String name = "ascariasis";
        group.setName(name);

        // Act
        validatorDiseaseGroupDao.save(group);
        int id = group.getId();

        // Assert
        assertThat(group.getCreatedDate()).isNotNull();
        flushAndClear();
        group = validatorDiseaseGroupDao.getById(id);
        assertThat(group.getName()).isEqualTo(name);
    }

    @Test
    public void getAllValidatorDiseaseGroups() {
        // Arrange
        int id = 1;
        String name = "ascariasis";

        // Act
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = validatorDiseaseGroupDao.getAll();
        ValidatorDiseaseGroup validatorDiseaseGroup = findById(validatorDiseaseGroups, id);

        // Assert
        assertThat(validatorDiseaseGroups).hasSize(20);
        assertThat(validatorDiseaseGroup).isNotNull();
        assertThat(validatorDiseaseGroup.getName()).isEqualTo(name);
    }

    private ValidatorDiseaseGroup findById(List<ValidatorDiseaseGroup> validatorDiseaseGroups, int id) {
        return selectUnique(validatorDiseaseGroups,
                having(on(ValidatorDiseaseGroup.class).getId(), IsEqual.equalTo(id)));
    }

    @Test
    public void getByNameReturnsCorrectValidatorDiseaseGroup() {
        // Arrange
        String name = "VDG";
        ValidatorDiseaseGroup testGroup = new ValidatorDiseaseGroup();
        testGroup.setName(name);
        validatorDiseaseGroupDao.save(testGroup);

        // Act
        ValidatorDiseaseGroup group = validatorDiseaseGroupDao.getByName(name);

        // Assert
        assertThat(group).isEqualTo(testGroup);
    }

    @Test
    public void getByNameReturnsNullForInvalidValidatorDiseaseGroupName() {
        // Arrange
        String invalidName = "No VDG";

        // Act
        ValidatorDiseaseGroup group = validatorDiseaseGroupDao.getByName(invalidName);

        // Assert
        assertThat(group).isNull();
    }
}
