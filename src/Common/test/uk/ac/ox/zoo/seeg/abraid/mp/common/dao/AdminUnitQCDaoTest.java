package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Tests the AdminUnitQCDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitQCDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitQCDao adminUnitQCDao;

    @Test
    public void getAllReturnsAllAdminUnits() {
        List<AdminUnitQC> adminUnits = adminUnitQCDao.getAll();
        assertThat(adminUnits).hasSize(41720);
    }

    @Test
    public void getByGaulCodeReturnsAdminUnitIfItExists() {
        // Arrange
        int gaulCode = 1633;

        // Act
        AdminUnitQC adminUnit = adminUnitQCDao.getByGaulCode(gaulCode);

        // Assert
        assertThat(adminUnit).isNotNull();
        assertThat(adminUnit.getGaulCode()).isEqualTo(gaulCode);
        assertThat(adminUnit.getName()).isEqualTo("Umbria");
        assertThat(adminUnit.getAdminLevel()).isEqualTo('1');
        assertThat(adminUnit.getCentroidLatitude()).isEqualTo(42.96577, offset(0.00005));
        assertThat(adminUnit.getCentroidLongitude()).isEqualTo(12.49014, offset(0.00005));
        assertThat(adminUnit.getArea()).isEqualTo(8455.63701766);
    }

    @Test
    public void getByGaulCodeReturnsNullIfAdminUnitDoesNotExist() {
        AdminUnitQC adminUnit = adminUnitQCDao.getByGaulCode(300);
        assertThat(adminUnit).isNull();
    }
}
