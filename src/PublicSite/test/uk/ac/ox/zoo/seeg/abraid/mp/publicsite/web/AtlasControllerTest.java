package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Test;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the Atlas controller.
 * Copyright (c) 2014 University of Oxford
 */
public class AtlasControllerTest {

    @Test
    public void showPageReturnsAtlasPageWithExpectedModelData() {
        // Arrange
        Model model = mock(Model.class);
        LocationService locationService = mock(LocationService.class);
        List<Country> allCountries = mockAllCountries(locationService);
        AtlasController target = new AtlasController(locationService);

        // Act
        String result = target.showPage(model);

        // Assert
        assertThat(result).isEqualTo("atlas");
        verify(model, times(1)).addAttribute("countries", allCountries);
    }

    private List<Country> mockAllCountries(LocationService locationService) {
        List<Country> allCountries = new ArrayList<>();
        allCountries.add(new Country());
        when(locationService.getAllCountries()).thenReturn(allCountries);
        return allCountries;
    }
}
