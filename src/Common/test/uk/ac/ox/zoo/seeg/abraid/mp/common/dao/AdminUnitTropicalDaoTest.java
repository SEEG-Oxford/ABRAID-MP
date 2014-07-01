package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the AdminUnitTropicalDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitTropicalDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Test
    public void getAll() {
        List<AdminUnitTropical> globalAdminUnits = adminUnitTropicalDao.getAll();
        assertThat(globalAdminUnits).hasSize(460);
    }
}
