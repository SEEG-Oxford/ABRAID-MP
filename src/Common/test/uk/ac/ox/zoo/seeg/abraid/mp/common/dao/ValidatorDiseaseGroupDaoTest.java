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
    public void getAllValidatorDiseaseGroups() {
        // Arrange
        int id = 1;
        String name = "ascariasis";

        // Act
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = validatorDiseaseGroupDao.getAll();
        ValidatorDiseaseGroup validatorDiseaseGroup = findById(validatorDiseaseGroups, id);

        assertThat(validatorDiseaseGroups).hasSize(20);
        assertThat(validatorDiseaseGroup).isNotNull();
        assertThat(validatorDiseaseGroup.getName()).isEqualTo(name);
    }

    private ValidatorDiseaseGroup findById(List<ValidatorDiseaseGroup> validatorDiseaseGroups, int id) {
        return selectUnique(validatorDiseaseGroups,
                having(on(ValidatorDiseaseGroup.class).getId(), IsEqual.equalTo(id)));
    }
}
