package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;

import java.io.IOException;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for CovariatesController.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerTest {
    @Test
    public void showCovariatesPageReturnsCorrectModelData() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        when(configurationService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model, times(1)).addAttribute("initialData", "{\"diseases\":null,\"files\":null}");
    }

    @Test
    public void showCovariatesPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        when(configurationService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        Model model = mock(Model.class);

        // Act
        String result = target.showCovariatesPage(model);

        // Assert
        assertThat(result).isEqualTo("covariates");
    }

    @Test
    public void showCovariatesPageThrowsForInvalidCovariateConfig() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        when(configurationService.getCovariateConfiguration()).thenThrow(new IOException());
        Model model = mock(Model.class);

        // Act
        catchException(target).showCovariatesPage(model);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Existing covariate configuration is invalid.");
    }

    @Test
    public void updateCovariatesRejectsInvalidInputs() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        JsonCovariateConfiguration invalidConf = new JsonCovariateConfiguration();

        for (JsonCovariateConfiguration conf : Arrays.asList(invalidConf, null)) {
            // Act
            ResponseEntity result = target.updateCovariates(conf);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateCovariatesThrowsIfConfigurationCanNotBeSaved() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(conf.isValid()).thenReturn(true);
        doThrow(new IOException()).when(configurationService).setCovariateConfiguration(conf);

        // Act
        catchException(target).updateCovariates(conf);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Covariate configuration update failed.");
    }

    @Test
    public void updateCovariatesSavedConfiguration() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService);
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(conf.isValid()).thenReturn(true);

        // Act
        ResponseEntity result = target.updateCovariates(conf);

        // Assert
        verify(configurationService, times(1)).setCovariateConfiguration(conf);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
