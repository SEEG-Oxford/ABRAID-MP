package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseDao diseaseDao;

    @Test
    public void saveAndReloadDisease() {
        String diseaseName = "Test disease";

        // Creates and saves a disease
        Disease disease = new Disease();
        disease.setName(diseaseName);
        diseaseDao.save(disease);
        Integer id = disease.getId();
        flushAndClear();

        // Reloads the same disease and verifies its properties
        disease = diseaseDao.getByName(diseaseName);
        assertThat(disease).isNotNull();
        assertThat(disease.getId()).isNotNull();
        assertThat(disease.getId()).isEqualTo(id);
        assertThat(disease.getName()).isEqualTo(diseaseName);
    }

    @Test
    public void loadNonExistentDisease() {
        String diseaseName = "This disease does not exist";
        Disease disease = diseaseDao.getByName(diseaseName);
        assertThat(disease).isNull();
    }

    @Test
    public void getAllDiseases() {
        List<Disease> diseases = diseaseDao.getAll();
        assertThat(diseases).hasSize(261);
    }
}
