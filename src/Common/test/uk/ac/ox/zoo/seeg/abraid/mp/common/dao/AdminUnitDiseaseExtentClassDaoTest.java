package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitDiseaseExtentClassDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void globalAdminUnitDiseaseExtentClassHasNullTropicalAdminUnit() {
        // Arrange - NB. Disease Group 22 in DB is a GLOBAL admin unit
        Integer diseaseGroupId = 22;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);

        // Act
        List<AdminUnitDiseaseExtentClass> list = adminUnitDiseaseExtentClassDao.getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(list).hasSize(5);
        for (AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass : list) {
            assertThat(adminUnitDiseaseExtentClass.getAdminUnitGlobal()).isNotNull();
            assertThat(adminUnitDiseaseExtentClass.getAdminUnitTropical()).isNull();
            assertThat(adminUnitDiseaseExtentClass.getDiseaseGroup()).isEqualTo(diseaseGroup);
        }
    }
}
