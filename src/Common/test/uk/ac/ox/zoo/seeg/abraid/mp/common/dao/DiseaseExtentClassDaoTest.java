package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseExtentClassDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentClassDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;

    public void getAllReturnsAllDiseaseExtentClasses() {
        assertThat(diseaseExtentClassDao.getAll()).hasSize(5);
    }

    @Test
    public void diseaseExtentClassesExistForEachConstant() {
        assertThat(diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE)).isNotNull();
        assertThat(diseaseExtentClassDao.getByName(DiseaseExtentClass.POSSIBLE_PRESENCE)).isNotNull();
        assertThat(diseaseExtentClassDao.getByName(DiseaseExtentClass.UNCERTAIN)).isNotNull();
        assertThat(diseaseExtentClassDao.getByName(DiseaseExtentClass.POSSIBLE_ABSENCE)).isNotNull();
        assertThat(diseaseExtentClassDao.getByName(DiseaseExtentClass.ABSENCE)).isNotNull();
    }

    @Test
    public void getByNameReturnsNullIfNonExistent() {
        assertThat(diseaseExtentClassDao.getByName("non-existent name")).isNull();
    }
}
