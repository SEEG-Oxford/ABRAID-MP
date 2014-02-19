package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the HealthMapCountryDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapCountryDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private HealthMapCountryDao healthMapCountryDao;

    @Test
    public void getAllDiseaseGroups() {
        List<HealthMapCountry> countries = healthMapCountryDao.getAll();
        assertThat(countries).hasSize(224);
    }
}
