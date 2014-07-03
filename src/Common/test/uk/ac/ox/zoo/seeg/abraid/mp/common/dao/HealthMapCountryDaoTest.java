package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapCountryDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapCountryDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private HealthMapCountryDao healthMapCountryDao;

    @Test
    public void getAllHealthMapCountries() {
        // Act
        List<HealthMapCountry> healthMapCountries = healthMapCountryDao.getAll();

        // Assert
        assertThat(healthMapCountries).hasSize(224);

        int totalAssociatedCountries = 0;
        for (HealthMapCountry healthMapCountry : healthMapCountries) {
            if (healthMapCountry.getCountries() != null) {
                totalAssociatedCountries += healthMapCountry.getCountries().size();
            }
        }
        assertThat(totalAssociatedCountries).isEqualTo(265);
    }

    @Test
    public void getHealthMapCountryWithNoAssociatedSEEGCountries() {
        // Arrange
        int id = 143;
        String healthMapCountryName = "Maldives";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getById(id);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        assertThat(healthMapCountry.getCountries()).isNotNull();
        assertThat(healthMapCountry.getCountries()).isEmpty();
    }

    @Test
    public void getHealthMapCountryWithOneAssociatedSEEGCountry() {
        // Arrange
        int id = 28;
        String healthMapCountryName = "Trinidad & Tobago";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getById(id);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        assertThat(healthMapCountry.getCountries()).isNotNull();
        assertThat(healthMapCountry.getCountries()).hasSize(1);
        for (Country country : healthMapCountry.getCountries()) {
            assertThat(country.getGaulCode()).isEqualTo(246);
            assertThat(country.getName()).isEqualTo("Trinidad and Tobago");
        }
    }

    @Test
    public void getHealthMapCountryWithTwoAssociatedSEEGCountries() {
        // Arrange
        int id = 104;
        String healthMapCountryName = "Netherlands";

        // Act
        HealthMapCountry healthMapCountry = healthMapCountryDao.getById(id);

        // Assert
        assertThat(healthMapCountry).isNotNull();
        assertThat(healthMapCountry.getId()).isEqualTo(id);
        assertThat(healthMapCountry.getName()).isEqualTo(healthMapCountryName);
        Set<Country> countries = healthMapCountry.getCountries();
        assertThat(countries).isNotNull();
        assertThat(countries).hasSize(2);

        Country country1 = findCountryByGaulCode(countries, 177);
        assertThat(country1).isNotNull();
        assertThat(country1.getName()).isEqualTo("Netherlands");

        Country country2 = findCountryByGaulCode(countries, 14);
        assertThat(country2).isNotNull();
        assertThat(country2.getName()).isEqualTo("Aruba");
    }

    @Test
    public void getHealthMapCountryByInvalidId() {
        int id = 5000;
        HealthMapCountry healthMapCountry = healthMapCountryDao.getById(id);
        assertThat(healthMapCountry).isNull();
    }

    private Country findCountryByGaulCode(Set<Country> countries, int gaulCode) {
        for (Country country : countries) {
            if (country.getGaulCode() == gaulCode) {
                return country;
            }
        }
        return null;
    }
}
