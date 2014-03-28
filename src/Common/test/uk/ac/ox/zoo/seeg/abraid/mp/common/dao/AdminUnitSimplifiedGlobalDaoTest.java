package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedGlobal;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitSimplifiedGlobalDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitSimplifiedGlobalDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AdminUnitSimplifiedGlobalDao adminUnitSimplifiedGlobalDao;

    @Test
    public void getAll() {
        List<AdminUnitSimplifiedGlobal> adminUnits = adminUnitSimplifiedGlobalDao.getAll();
        assertThat(adminUnits).hasSize(559);
    }
}
