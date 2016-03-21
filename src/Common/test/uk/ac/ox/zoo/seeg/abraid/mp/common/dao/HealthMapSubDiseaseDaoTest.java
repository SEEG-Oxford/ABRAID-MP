package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapSubDiseaseDaoTest class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapSubDiseaseDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private HealthMapDiseaseDao healthMapDiseaseDao;

    @Autowired
    private HealthMapSubDiseaseDao healthMapSubDiseaseDao;

    @Test
    public void saveAndReloadHealthMapDisease() {
        // Arrange
        String name = "subdisease";
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(16);
        HealthMapDisease healthMapDisease = healthMapDiseaseDao.getById(1);

        HealthMapSubDisease disease = new HealthMapSubDisease(
                healthMapDisease,
                name,
                diseaseGroup
        );

        // Act
        healthMapSubDiseaseDao.save(disease);
        int id = disease.getId();
        flushAndClear();

        // Assert
        disease = healthMapSubDiseaseDao.getById(id);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(name);
        assertThat(disease.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(disease.getHealthMapDisease()).isEqualTo(healthMapDisease);
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadHealthMapDiseaseWithoutLinked() {
        // Arrange
        String name = "subdisease";

        HealthMapSubDisease disease = new HealthMapSubDisease(
                null,
                name,
                null
        );

        // Act
        healthMapSubDiseaseDao.save(disease);
        int id = disease.getId();
        flushAndClear();

        // Assert
        disease = healthMapSubDiseaseDao.getById(id);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(name);
        assertThat(disease.getDiseaseGroup()).isNull();
        assertThat(disease.getHealthMapDisease()).isNull();
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadExistingHealthMapDisease() {
        // Arrange
        HealthMapSubDisease disease = healthMapSubDiseaseDao.getById(1);
        disease.setHealthMapDisease(healthMapDiseaseDao.getById(2));
        disease.setDiseaseGroup(diseaseGroupDao.getById(3));

        // Act
        healthMapSubDiseaseDao.save(disease);
        flushAndClear();

        // Assert
        disease = healthMapSubDiseaseDao.getById(1);
        assertThat(disease).isNotNull();
        assertThat(disease.getDiseaseGroup().getId()).isEqualTo(3);
        assertThat(disease.getHealthMapDisease().getId()).isEqualTo(2);
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void getAllHealthMapSubDiseases() {
        List<HealthMapSubDisease> subDiseases = healthMapSubDiseaseDao.getAll();
        assertThat(subDiseases).hasSize(45);
    }
}
