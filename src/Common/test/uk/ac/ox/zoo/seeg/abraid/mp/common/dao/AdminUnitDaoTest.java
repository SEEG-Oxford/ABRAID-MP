package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitDao adminUnitDao;

    @Test
    public void getAll() {
        List<AdminUnit> adminUnits = adminUnitDao.getAll();
        assertThat(adminUnits).hasSize(41724);
    }
}
