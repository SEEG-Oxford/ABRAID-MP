package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the LandSeaBorderDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LandSeaBorderDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private LandSeaBorderDao landSeaBorderDao;

    @Test
    public void getAll() {
        List<LandSeaBorder> landSeaBorders = landSeaBorderDao.getAll();
        assertThat(landSeaBorders).hasSize(4357);
    }
}
