package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseDao diseaseDao;

    @Test
    public void saveAndReloadDisease() {
        String diseaseName = "Test diseaseGroup";

        // Creates and saves a diseaseGroup
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(diseaseName);
        diseaseDao.save(diseaseGroup);
        Integer id = diseaseGroup.getId();
        flushAndClear();

        // Reloads the same diseaseGroup and verifies its properties
        diseaseGroup = diseaseDao.getByName(diseaseName);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getId()).isNotNull();
        assertThat(diseaseGroup.getId()).isEqualTo(id);
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseName);
    }

    @Test
    public void loadNonExistentDisease() {
        String diseaseName = "This diseaseGroup does not exist";
        DiseaseGroup diseaseGroup = diseaseDao.getByName(diseaseName);
        assertThat(diseaseGroup).isNull();
    }

    @Test
    public void getAllDiseases() {
        List<DiseaseGroup> diseaseGroups = diseaseDao.getAll();
        assertThat(diseaseGroups).hasSize(261);
    }
}
