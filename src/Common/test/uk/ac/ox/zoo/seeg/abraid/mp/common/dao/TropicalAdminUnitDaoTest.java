package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.TropicalAdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the TropicalAdminUnitDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class TropicalAdminUnitDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private TropicalAdminUnitDao tropicalAdminUnitDao;

    @Test
    public void getAll() {
        List<TropicalAdminUnit> globalAdminUnits = tropicalAdminUnitDao.getAll();
        assertThat(globalAdminUnits).hasSize(459);
    }
}
