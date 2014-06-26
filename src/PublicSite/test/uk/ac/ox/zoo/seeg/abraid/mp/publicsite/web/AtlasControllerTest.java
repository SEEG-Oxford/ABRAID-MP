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
    public void showPageReturnsAtlasPage() {
        // Arrange
        Model model = mock(Model.class);
        AtlasController target = new AtlasController();

        // Act
        String result = target.showPage();

        // Assert
        assertThat(result).isEqualTo("atlas");
    }

}
