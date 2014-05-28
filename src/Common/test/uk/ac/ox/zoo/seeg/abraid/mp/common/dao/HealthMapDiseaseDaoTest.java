package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the HealthMapDiseaseDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDiseaseDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private HealthMapDiseaseDao healthMapDiseaseDao;

    @Test
    public void saveAndReloadHealthMapDisease() {
        // Arrange
        String healthMapDiseaseName = "Test HealthMap disease";
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(16);
        int healthMapDiseaseId = 10000;

        HealthMapDisease disease = new HealthMapDisease();
        disease.setId(healthMapDiseaseId);
        disease.setDiseaseGroup(diseaseGroup);
        disease.setName(healthMapDiseaseName);

        // Act
        healthMapDiseaseDao.save(disease);
        flushAndClear();

        // Assert
        disease = healthMapDiseaseDao.getById(healthMapDiseaseId);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(healthMapDiseaseName);
        assertThat(disease.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadHealthMapDiseaseWithoutGroup() {
        // Arrange
        String healthMapDiseaseName = "Test HealthMap disease";
        int healthMapDiseaseId = 10000;

        HealthMapDisease disease = new HealthMapDisease();
        disease.setId(healthMapDiseaseId);
        disease.setName(healthMapDiseaseName);

        // Act
        healthMapDiseaseDao.save(disease);
        flushAndClear();

        // Assert
        disease = healthMapDiseaseDao.getById(healthMapDiseaseId);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(healthMapDiseaseName);
        assertThat(disease.getDiseaseGroup()).isNull();
    }

    @Test
    public void loadNonExistentHealthMapDisease() {
        HealthMapDisease healthMapDisease = healthMapDiseaseDao.getById(-1);
        assertThat(healthMapDisease).isNull();
    }

    @Test
    public void getAllHealthMapDiseases() {
        List<HealthMapDisease> healthMapDiseases = healthMapDiseaseDao.getAll();
        assertThat(healthMapDiseases).hasSize(279);
    }
}
