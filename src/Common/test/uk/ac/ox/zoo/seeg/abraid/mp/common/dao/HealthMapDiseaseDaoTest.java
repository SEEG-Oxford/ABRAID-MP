package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

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
        String healthMapDiseaseSubName = "pv";
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(16);
        int healthMapDiseaseId = 10000;

        HealthMapDisease disease = new HealthMapDisease();
        disease.setHealthMapDiseaseId(healthMapDiseaseId);
        disease.setDiseaseGroup(diseaseGroup);
        disease.setName(healthMapDiseaseName);
        disease.setSubName(healthMapDiseaseSubName);

        // Act
        healthMapDiseaseDao.save(disease);
        Integer id = disease.getId();
        flushAndClear();

        // Assert
        assertThat(id).isNotNull();
        disease = healthMapDiseaseDao.getById(id);
        assertThat(disease).isNotNull();
        assertThat(disease.getHealthMapDiseaseId()).isEqualTo(healthMapDiseaseId);
        assertThat(disease.getName()).isEqualTo(healthMapDiseaseName);
        assertThat(disease.getSubName()).isEqualTo(healthMapDiseaseSubName);
        assertThat(disease.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void cannotSaveSubNameContainingASpace() {
        testInvalidSubName("this is invalid");
    }

    @Test
    public void cannotSaveSubNameContainingAComma() {
        testInvalidSubName("this, is invalid");
    }

    @Test
    public void cannotSaveSubNameContainingUppercaseLetters() {
        testInvalidSubName("Invalid");
    }

    @Test
    public void saveAndReloadHealthMapDiseaseWithoutGroupOrSubName() {
        // Arrange
        String healthMapDiseaseName = "Test HealthMap disease";
        int healthMapDiseaseId = 10000;

        HealthMapDisease disease = new HealthMapDisease();
        disease.setHealthMapDiseaseId(healthMapDiseaseId);
        disease.setName(healthMapDiseaseName);

        // Act
        healthMapDiseaseDao.save(disease);
        Integer id = disease.getId();
        flushAndClear();

        // Assert
        assertThat(id).isNotNull();
        disease = healthMapDiseaseDao.getById(id);
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
        assertThat(healthMapDiseases).hasSize(293);
    }

    private void testInvalidSubName(String healthMapDiseaseSubName) {
        // Arrange
        String healthMapDiseaseName = "Test HealthMap disease";
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(16);
        int healthMapDiseaseId = 10000;

        HealthMapDisease disease = new HealthMapDisease();
        disease.setHealthMapDiseaseId(healthMapDiseaseId);
        disease.setDiseaseGroup(diseaseGroup);
        disease.setName(healthMapDiseaseName);
        disease.setSubName(healthMapDiseaseSubName);

        // Act
        catchException(healthMapDiseaseDao).save(disease);

        // Assert
        assertThat(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(caughtException()).hasMessageContaining("ck_healthmap_disease_sub_name");
    }
}
