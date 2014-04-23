package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AdminUnitGlobalDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AdminUnitTropicalDao;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitDiseaseExtentClass class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Test
    public void getAdminUnitGlobalOrTropical() throws Exception {
        // Arrange
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass();
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2);
        adminUnitDiseaseExtentClass.setAdminUnitGlobal(adminUnitGlobal);

        // Act
        AdminUnitGlobalOrTropical adminUnitGlobalOrTropical = adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical();

        // Assert
        assertThat(adminUnitGlobalOrTropical).isEqualsToByComparingFields(adminUnitGlobal);
    }
}
