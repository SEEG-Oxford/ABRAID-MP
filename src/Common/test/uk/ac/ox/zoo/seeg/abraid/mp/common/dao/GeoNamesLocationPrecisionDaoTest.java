package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoNamesLocationPrecision;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the GeoNamesLocationPrecisionDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNamesLocationPrecisionDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;

    @Test
    public void getAllGeoNamesLocationPrecisions() {
        List<GeoNamesLocationPrecision> list = geoNamesLocationPrecisionDao.getAll();
        assertThat(list).hasSize(214);
    }
}
