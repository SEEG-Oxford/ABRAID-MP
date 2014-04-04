package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;

/**
 * Tests the AdminUnitDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitDao adminUnitDao;

    @Test
    public void getAllReturnsAllAdminUnits() {
        List<AdminUnit> adminUnits = adminUnitDao.getAll();
        assertThat(adminUnits).hasSize(41724);
    }

    @Test
    public void getByGaulCodeReturnsAdminUnitIfItExists() {
        // Arrange
        int gaulCode = 1633;

        // Act
        AdminUnit adminUnit = adminUnitDao.getByGaulCode(gaulCode);

        // Assert
        assertThat(adminUnit).isNotNull();
        assertThat(adminUnit.getGaulCode()).isEqualTo(gaulCode);
        assertThat(adminUnit.getName()).isEqualTo("Umbria");
        assertThat(adminUnit.getAdminLevel()).isEqualTo('1');
        assertThat(adminUnit.getCentroidLatitude()).isEqualTo(42.96577);
        assertThat(adminUnit.getCentroidLongitude()).isEqualTo(12.49014);
        assertThat(adminUnit.getArea()).isEqualTo(8455.63701766);
        assertThat(adminUnit.getMaxDistanceFromCentroid()).isEqualTo(101.149991553972, offset(0.0000000005));
    }

    @Test
    public void getByGaulCodeReturnsNullIfAdminUnitDoesNotExist() {
        AdminUnit adminUnit = adminUnitDao.getByGaulCode(300);
        assertThat(adminUnit).isNull();
    }
}
