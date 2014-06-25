package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobal;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AdminUnitGlobalDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitGlobalDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Test
    public void getAll() {
        List<AdminUnitGlobal> adminUnitGlobals = adminUnitGlobalDao.getAll();
        assertThat(adminUnitGlobals).hasSize(559);
    }
}
