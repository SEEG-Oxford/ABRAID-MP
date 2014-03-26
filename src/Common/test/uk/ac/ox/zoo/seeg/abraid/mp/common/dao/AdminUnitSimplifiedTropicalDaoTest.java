package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedTropical;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitSimplifiedTropicalDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitSimplifiedTropicalDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitSimplifiedTropicalDao adminUnitSimplifiedTropicalDao;

    @Test
    public void getAll() {
        List<AdminUnitSimplifiedTropical> adminUnits = adminUnitSimplifiedTropicalDao.getAll();
        assertThat(adminUnits).hasSize(460);
    }
}
