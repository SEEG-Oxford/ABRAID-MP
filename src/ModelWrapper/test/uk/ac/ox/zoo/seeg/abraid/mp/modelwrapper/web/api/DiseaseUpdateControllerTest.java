package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.api;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for DiseaseUpdateController.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseUpdateControllerTest {
    @Test
    public void updateDiseaseRejectsMismatchedDiseaseId() throws Exception {
        // Arrange
        DiseaseUpdateController target = new DiseaseUpdateController(mock(ConfigurationService.class));
        Integer diseaseIdParam = 21;
        JsonDisease requestDisease = mock(JsonDisease.class);
        when(requestDisease.getId()).thenReturn(20);
        when(requestDisease.isValid()).thenReturn(true);

        // Act
        ResponseEntity result = target.updateDisease(diseaseIdParam, requestDisease);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateDiseaseRejectInvalidDisease() throws Exception {
        // Arrange
        DiseaseUpdateController target = new DiseaseUpdateController(mock(ConfigurationService.class));
        Integer diseaseIdParam = 21;
        JsonDisease requestDisease = mock(JsonDisease.class);
        when(requestDisease.getId()).thenReturn(diseaseIdParam);
        when(requestDisease.isValid()).thenReturn(false);

        // Act
        ResponseEntity result = target.updateDisease(diseaseIdParam, requestDisease);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateDiseaseUpdatesStoredDisease() throws Exception {
        // Arrange
        Integer diseaseIdParam = 21;
        ConfigurationService configurationService = createMockConfigurationService(diseaseIdParam);
        DiseaseUpdateController target = new DiseaseUpdateController(configurationService);
        JsonDisease requestDisease = createMockDisease(diseaseIdParam, "new name", true);

        // Act
        ResponseEntity result = target.updateDisease(diseaseIdParam, requestDisease);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ArgumentCaptor<JsonCovariateConfiguration> covariateConfigurationCaptor = ArgumentCaptor.forClass(JsonCovariateConfiguration.class);
        verify(configurationService).setCovariateConfiguration(covariateConfigurationCaptor.capture());
        JsonCovariateConfiguration updatedConfig = covariateConfigurationCaptor.getValue();
        verify(updatedConfig.getDiseases().get(1)).setName("new name");
    }

    @Test
    public void updateDiseaseAddsMissingStoredDisease() throws Exception {
        // Arrange
        Integer diseaseIdParam = 21;
        ConfigurationService configurationService = createMockConfigurationService(31);
        DiseaseUpdateController target = new DiseaseUpdateController(configurationService);
        JsonDisease requestDisease = createMockDisease(diseaseIdParam, "new disease", true);

        // Act
        ResponseEntity result = target.updateDisease(diseaseIdParam, requestDisease);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ArgumentCaptor<JsonCovariateConfiguration> covariateConfigurationCaptor = ArgumentCaptor.forClass(JsonCovariateConfiguration.class);
        verify(configurationService).setCovariateConfiguration(covariateConfigurationCaptor.capture());
        JsonCovariateConfiguration updatedConfig = covariateConfigurationCaptor.getValue();
        assertThat(updatedConfig.getDiseases()).hasSize(4);
        assertThat(updatedConfig.getDiseases().get(3)).isEqualTo(requestDisease);
    }

    @Test
    public void updateAllRejectInvalidDisease() throws Exception {
        // Arrange
        DiseaseUpdateController target = new DiseaseUpdateController(mock(ConfigurationService.class));
        WrappedList<JsonDisease> diseases = createMockWrappedList(createMockInitialDiseaseList(21));
        when(diseases.getList().get(1).isValid()).thenReturn(false);

        // Act
        ResponseEntity result = target.updateAll(diseases);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void updateAllUpdatesStoredDiseases() throws Exception {
        // Arrange
        Integer diseaseIdParam = 21;
        ConfigurationService configurationService = createMockConfigurationService(diseaseIdParam);
        DiseaseUpdateController target = new DiseaseUpdateController(configurationService);
        WrappedList<JsonDisease> diseases = createMockWrappedList(createMockInitialDiseaseList(diseaseIdParam));
        when(diseases.getList().get(1).getName()).thenReturn("new name 1");
        when(diseases.getList().get(2).getName()).thenReturn("new name 2");

        // Act
        ResponseEntity result = target.updateAll(diseases);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ArgumentCaptor<JsonCovariateConfiguration> covariateConfigurationCaptor = ArgumentCaptor.forClass(JsonCovariateConfiguration.class);
        verify(configurationService).setCovariateConfiguration(covariateConfigurationCaptor.capture());
        JsonCovariateConfiguration updatedConfig = covariateConfigurationCaptor.getValue();
        verify(updatedConfig.getDiseases().get(1)).setName("new name 1");
        verify(updatedConfig.getDiseases().get(2)).setName("new name 2");
    }

    @Test
    public void updateAllAddsMissingStoredDiseases() throws Exception {
        // Arrange
        Integer diseaseIdParam = 21;
        ConfigurationService configurationService = createMockConfigurationService(diseaseIdParam);
        DiseaseUpdateController target = new DiseaseUpdateController(configurationService);
        WrappedList<JsonDisease> diseases = createMockWrappedList(createMockInitialDiseaseList(diseaseIdParam));
        JsonDisease new1 = createMockDisease(555, "new", true);
        diseases.getList().add(new1);
        JsonDisease new2 = createMockDisease(777, "newish", true);
        diseases.getList().add(new2);

        // Act
        ResponseEntity result = target.updateAll(diseases);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ArgumentCaptor<JsonCovariateConfiguration> covariateConfigurationCaptor = ArgumentCaptor.forClass(JsonCovariateConfiguration.class);
        verify(configurationService).setCovariateConfiguration(covariateConfigurationCaptor.capture());
        JsonCovariateConfiguration updatedConfig = covariateConfigurationCaptor.getValue();
        assertThat(updatedConfig.getDiseases()).hasSize(5);
        assertThat(updatedConfig.getDiseases().get(3)).isEqualTo(new1);
        assertThat(updatedConfig.getDiseases().get(4)).isEqualTo(new2);
    }

    private ConfigurationService createMockConfigurationService(Integer diseaseIdParam) throws IOException {
        ConfigurationService configurationService = mock(ConfigurationService.class);
        JsonCovariateConfiguration covariateConfiguration = mock(JsonCovariateConfiguration.class);
        List<JsonDisease> diseaseList = createMockInitialDiseaseList(diseaseIdParam);

        when(configurationService.getCovariateConfiguration()).thenReturn(covariateConfiguration);
        when(covariateConfiguration.getDiseases()).thenReturn(diseaseList);

        return configurationService;
    }

    private List<JsonDisease> createMockInitialDiseaseList(Integer diseaseIdParam) {
        List<JsonDisease> list = new ArrayList<>();
        list.addAll(Arrays.asList(
                createMockDisease(1213, "abc", true),
                createMockDisease(diseaseIdParam, "acsd", true),
                createMockDisease(623, "zxy", true)
        ));
        return list;
    }

    private JsonDisease createMockDisease(int id, String name, boolean validity) {
        JsonDisease mock = mock(JsonDisease.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        when(mock.isValid()).thenReturn(validity);
        return mock;
    }

    private WrappedList<JsonDisease> createMockWrappedList(List<JsonDisease> diseaseList) {
        WrappedList<JsonDisease> wrappedList = mock(WrappedList.class);
        when(wrappedList.getList()).thenReturn(diseaseList);
        return wrappedList;
    }
}
