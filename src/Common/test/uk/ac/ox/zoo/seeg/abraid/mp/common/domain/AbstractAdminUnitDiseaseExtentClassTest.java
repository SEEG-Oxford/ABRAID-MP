package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AdminUnitGlobalDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AdminUnitTropicalDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the AdminUnitDiseaseExtentClass class.
 * Copyright (c) 2014 University of Oxford
 */
public class AbstractAdminUnitDiseaseExtentClassTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Test
    public void getAdminUnitGlobalOrTropical() throws Exception {
        // Arrange (
        AbstractAdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass();
        AbstractAdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass2 = new ModelRunAdminUnitDiseaseExtentClass();
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2);
        adminUnitDiseaseExtentClass.setAdminUnitGlobal(adminUnitGlobal);
        AdminUnitTropical adminUnitTropical = adminUnitTropicalDao.getByGaulCode(7);
        adminUnitDiseaseExtentClass2.setAdminUnitTropical(adminUnitTropical);

        // Act
        AdminUnitGlobalOrTropical adminUnitGlobalOrTropical = adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical();
        AdminUnitGlobalOrTropical adminUnitGlobalOrTropical2 = adminUnitDiseaseExtentClass2.getAdminUnitGlobalOrTropical();

        // Assert
        assertThat(adminUnitGlobalOrTropical).isEqualToComparingFieldByField(adminUnitGlobal);
        assertThat(adminUnitGlobalOrTropical2).isEqualToComparingFieldByField(adminUnitTropical);
    }
}
