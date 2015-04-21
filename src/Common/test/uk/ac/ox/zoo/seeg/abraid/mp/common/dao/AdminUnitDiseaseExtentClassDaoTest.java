package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the AdminUnitDiseaseExtentClassDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void getLatestDiseaseExtentClassChangeDateByDiseaseGroupId() {
        // Arrange
        Integer diseaseGroupId = 87;

        // Act
        DateTime result = adminUnitDiseaseExtentClassDao.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(result).isEqualTo(new DateTime("2014-06-10T17:45:25"));
    }

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

    @Test
    public void saveAndReloadGlobalAdminUnitDiseaseExtentClass() {
        // Arrange
        int gaulCode = 2510;
        int diseaseGroupId = 22;
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(gaulCode);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseExtentClass extentClass = diseaseExtentClassDao.getByName("PRESENCE");
        DiseaseExtentClass validatorExtentClass = diseaseExtentClassDao.getByName("POSSIBLE_ABSENCE");
        int occurrenceCount = 25;
        DateTime createdDate = DateTime.now();

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                adminUnitGlobal, diseaseGroup, extentClass, validatorExtentClass, occurrenceCount, createdDate);

        // Act
        adminUnitDiseaseExtentClassDao.save(adminUnitDiseaseExtentClass);
        Integer id = adminUnitDiseaseExtentClass.getId();

        // Assert
        assertThat(adminUnitDiseaseExtentClass.getClassChangedDate()).isNotNull();
        flushAndClear();
        adminUnitDiseaseExtentClass = adminUnitDiseaseExtentClassDao.getById(id);
        assertThat(adminUnitDiseaseExtentClass).isNotNull();
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitGlobal()).isNotNull();
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitGlobal().getGaulCode()).isEqualTo(gaulCode);
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitTropical()).isNull();
        assertThat(adminUnitDiseaseExtentClass.getDiseaseExtentClass()).isEqualTo(extentClass);
        assertThat(adminUnitDiseaseExtentClass.getValidatorOccurrenceCount()).isEqualTo(occurrenceCount);
        assertThat(adminUnitDiseaseExtentClass.getValidatorDiseaseExtentClass()).isEqualTo(validatorExtentClass);
        assertThat(adminUnitDiseaseExtentClass.getClassChangedDate()).isEqualTo(createdDate);
    }

    @Test
    public void saveAndReloadTropicalAdminUnitDiseaseExtentClass() {
        // Arrange
        int gaulCode = 204001;
        int diseaseGroupId = 96;
        AdminUnitTropical adminUnitTropical = adminUnitTropicalDao.getByGaulCode(gaulCode);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseExtentClass extentClass = diseaseExtentClassDao.getByName("ABSENCE");
        DiseaseExtentClass validatorExtentClass = diseaseExtentClassDao.getByName("POSSIBLE_ABSENCE");
        int occurrenceCount = 0;
        DateTime changedDate = DateTime.now();

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                adminUnitTropical, diseaseGroup, extentClass, validatorExtentClass, occurrenceCount);
        adminUnitDiseaseExtentClass.setClassChangedDate(changedDate);

        // Act
        adminUnitDiseaseExtentClassDao.save(adminUnitDiseaseExtentClass);
        Integer id = adminUnitDiseaseExtentClass.getId();

        // Assert
        assertThat(adminUnitDiseaseExtentClass.getClassChangedDate()).isNotNull();
        flushAndClear();
        adminUnitDiseaseExtentClass = adminUnitDiseaseExtentClassDao.getById(id);
        assertThat(adminUnitDiseaseExtentClass).isNotNull();
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitTropical()).isNotNull();
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitTropical().getGaulCode()).isEqualTo(gaulCode);
        assertThat(adminUnitDiseaseExtentClass.getAdminUnitGlobal()).isNull();
        assertThat(adminUnitDiseaseExtentClass.getDiseaseExtentClass()).isEqualTo(extentClass);
        assertThat(adminUnitDiseaseExtentClass.getValidatorDiseaseExtentClass()).isEqualTo(validatorExtentClass);
        assertThat(adminUnitDiseaseExtentClass.getValidatorOccurrenceCount()).isEqualTo(occurrenceCount);
    }

    @Test(expected = ConstraintViolationException.class)
    public void cannotSpecifyBothGlobalAndTropicalAdminUnit() {
        // Arrange
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2510);
        AdminUnitTropical adminUnitTropical = adminUnitTropicalDao.getByGaulCode(204001);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(96);
        DiseaseExtentClass extentClass = diseaseExtentClassDao.getByName(DiseaseExtentClass.ABSENCE);
        DiseaseExtentClass validatorExtentClass = diseaseExtentClassDao.getByName(DiseaseExtentClass.POSSIBLE_ABSENCE);
        int occurrenceCount = 0;

        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                adminUnitTropical, diseaseGroup, extentClass, validatorExtentClass, occurrenceCount);
        adminUnitDiseaseExtentClass.setAdminUnitGlobal(adminUnitGlobal);

        // Act
        // Cannot use catchException() because the sessionFactory becomes null for some reason -
        // expected exception is specified in the @Test annotation
        adminUnitDiseaseExtentClassDao.save(adminUnitDiseaseExtentClass);
    }
}
