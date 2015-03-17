package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests the ValidatorDiseaseGroupDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidatorDiseaseGroupDaoTest extends AbstractCommonSpringIntegrationTests {
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
        int id = 2;
        String name = "CCHF";

        // Act
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = validatorDiseaseGroupDao.getAll();
        ValidatorDiseaseGroup validatorDiseaseGroup = findById(validatorDiseaseGroups, id);

        // Assert
        assertThat(validatorDiseaseGroups).hasSize(19);
        assertThat(validatorDiseaseGroup).isNotNull();
        assertThat(validatorDiseaseGroup.getName()).isEqualTo(name);
    }

    private ValidatorDiseaseGroup findById(List<ValidatorDiseaseGroup> validatorDiseaseGroups, int id) {
        return selectUnique(validatorDiseaseGroups,
                having(on(ValidatorDiseaseGroup.class).getId(), equalTo(id)));
    }
}
