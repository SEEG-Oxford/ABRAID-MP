package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonCountry.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCountryTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Country country = mock(Country.class);
        when(country.getName()).thenReturn("NAME");
        when(country.getGaulCode()).thenReturn(54321);
        when(country.getGeom()).thenReturn(GeometryUtils.createMultiPolygon(GeometryUtils.createPolygon(0, 0, 1, 2, 2, 3, 0, 0)));

        // Act
        JsonCountry result = new JsonCountry(country);

        // Assert
        assertThat(result.getName()).isEqualTo("NAME");
        assertThat(result.getGaulCode()).isEqualTo(54321);
        assertThat(result.getMinX()).isEqualTo(0);
        assertThat(result.getMaxX()).isEqualTo(2);
        assertThat(result.getMinY()).isEqualTo(0);
        assertThat(result.getMaxY()).isEqualTo(3);
    }
}
