package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the GlobalAdminUnitDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class GlobalAdminUnitDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private GlobalAdminUnitDao globalAdminUnitDao;

    @Test
    public void getAll() {
        List<GlobalAdminUnit> globalAdminUnits = globalAdminUnitDao.getAll();
        assertThat(globalAdminUnits).hasSize(558);
    }
}
