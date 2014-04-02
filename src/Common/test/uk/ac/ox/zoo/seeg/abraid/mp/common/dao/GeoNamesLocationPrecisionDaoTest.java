package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoNamesLocationPrecision;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the GeoNamesLocationPrecisionDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNamesLocationPrecisionDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;

    @Test
    public void getAllGeoNamesLocationPrecisions() {
        List<GeoNamesLocationPrecision> list = geoNamesLocationPrecisionDao.getAll();
        assertThat(list).hasSize(197);
    }
}
