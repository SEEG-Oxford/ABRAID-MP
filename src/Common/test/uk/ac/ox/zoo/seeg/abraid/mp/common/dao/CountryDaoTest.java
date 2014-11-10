package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CountryDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CountryDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private CountryDao countryDao;

    @Test
    public void getAllCountries() {
        List<Country> countries = countryDao.getAll();
        assertThat(countries).hasSize(249);
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

    @Test
    public void getCountriesForMinDataSpreadCalculationReturnsExpectedNumber() {
        // Act
        List<Integer> gaulCodes = countryDao.getCountriesForMinDataSpreadCalculation();

        // Assert
        assertThat(gaulCodes.size()).isEqualTo(58);
        assertThat(gaulCodes).containsOnly(4, 6, 8, 29, 35, 42, 43, 45, 47, 49, 50, 58, 59, 66, 68, 70, 74, 76, 77,
                79, 89, 90, 94, 102, 105, 106, 133, 142, 144, 145, 150, 152, 155, 159, 161, 169, 170, 172, 181, 182,
                205, 214, 217, 221, 226, 235, 243, 248, 253, 257, 268, 270, 271, 40760, 40762, 40765, 61013, 1013965);
    }
}
