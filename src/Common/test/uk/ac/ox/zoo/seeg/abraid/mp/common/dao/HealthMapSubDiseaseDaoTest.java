package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
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
    private HealthMapSubDiseaseDao healthMapSubDiseaseDao;

    @Test
    public void getAllHealthMapSubDiseases() {
        List<HealthMapSubDisease> subDiseases = healthMapSubDiseaseDao.getAll();
        assertThat(subDiseases).hasSize(35);
    }

    @Test
    public void getHealthMapSubDiseaseByName() {
        String name = "pf";
        HealthMapSubDisease subDisease = healthMapSubDiseaseDao.getByName(name);
        assertThat(subDisease).isNotNull();
        assertThat(subDisease.getName()).isEqualTo(name);
        assertThat(subDisease.getHealthMapDisease()).isNotNull();
        assertThat(subDisease.getHealthMapDisease().getId()).isEqualTo(80);
        assertThat(subDisease.getDiseaseGroup()).isNotNull();
        assertThat(subDisease.getDiseaseGroup().getId()).isEqualTo(249);
    }
}
