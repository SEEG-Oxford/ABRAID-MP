package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the CountryDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CountryDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private CountryDao countryDao;

    @Test
    public void getAllCountries() {
        List<Country> countries = countryDao.getAll();
        assertThat(countries).hasSize(250);
    }

    @Test
    public void getCountryByValidName() {
        String countryName = "Australia";
        int gaulCode = 17;
        Country country = countryDao.getByName(countryName);
        assertThat(country).isNotNull();
        assertThat(country.getGaulCode()).isEqualTo(gaulCode);
        assertThat(country.getName()).isEqualTo(countryName);
    }

    @Test
    public void getCountryByInvalidName() {
        String countryName = "This country does not exist";
        Country country = countryDao.getByName(countryName);
        assertThat(country).isNull();
    }
}
