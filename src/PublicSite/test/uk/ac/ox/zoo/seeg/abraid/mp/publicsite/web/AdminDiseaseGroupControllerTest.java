package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonModelRunInformation;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the AdminDiseaseGroupController class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminDiseaseGroupControllerTest {
    private DiseaseService diseaseService;
    private GeoJsonObjectMapper geoJsonObjectMapper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private ModelRunService modelRunService;
    private AdminDiseaseGroupController controller;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        geoJsonObjectMapper = new GeoJsonObjectMapper();
        modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        modelRunService = mock(ModelRunService.class);
        controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper, modelRunWorkflowService,
                modelRunService);
    }

    @Test
    public void showPageAddsDiseaseGroupsAndValidatorDiseaseGroupsToModel() throws JsonProcessingException {
        // Arrange
        Model model = mock(Model.class);

        DiseaseGroup diseaseGroup1 = createDiseaseGroup(188, 5, "Leishmaniases", "leishmaniases", DiseaseGroupType.MICROCLUSTER,
                "leishmaniases", "leishmaniases", "leish", true, 9, 0.5);
        DiseaseGroup diseaseGroup2 = createDiseaseGroup(87, null, "Dengue", null, DiseaseGroupType.SINGLE,
                "dengue", "dengue", "deng", false, 4, 1);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2);
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);
        String expectedJson = "[" +
                "{\"id\":87,\"name\":\"Dengue\",\"publicName\":\"dengue\",\"shortName\":\"dengue\",\"abbreviation\":\"deng\",\"groupType\":\"SINGLE\",\"isGlobal\":false,\"validatorDiseaseGroup\":{\"id\":4},\"weighting\":1.0,\"automaticModelRuns\":false}," +
                "{\"id\":188,\"name\":\"Leishmaniases\",\"publicName\":\"leishmaniases\",\"shortName\":\"leishmaniases\",\"abbreviation\":\"leish\",\"groupType\":\"MICROCLUSTER\",\"isGlobal\":true,\"parentDiseaseGroup\":{\"id\":5,\"name\":\"leishmaniases\"},\"validatorDiseaseGroup\":{\"id\":9},\"weighting\":0.5,\"automaticModelRuns\":false}]";

        ValidatorDiseaseGroup validator1 = new ValidatorDiseaseGroup(2, "CCHF");
        ValidatorDiseaseGroup validator2 = new ValidatorDiseaseGroup(3, "cholera");
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(Arrays.asList(validator1, validator2));
        String expectedValidatorJson = "[{\"id\":2,\"name\":\"CCHF\"},{\"id\":3,\"name\":\"cholera\"}]";

        // Act
        String result = controller.showPage(model);

        // Assert
        assertThat(result).isEqualTo("admin/diseasegroup/index");
        verify(model, times(1)).addAttribute("diseaseGroups", expectedJson);
        verify(model, times(1)).addAttribute("validatorDiseaseGroups", expectedValidatorJson);
    }

    @Test
    public void getModelRunInformation() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun lastRequestedModelRun = new ModelRun("dengue 1", 87, new DateTime("2014-07-02T14:15:16"));
        ModelRun lastCompletedModelRun = new ModelRun();
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, null, null);
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);

        when(modelRunService.getLastRequestedModelRun(diseaseGroupId)).thenReturn(lastRequestedModelRun);
        when(modelRunService.getLastCompletedModelRun(diseaseGroupId)).thenReturn(lastCompletedModelRun);
        when(diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId)).thenReturn(statistics);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        ResponseEntity<JsonModelRunInformation> entity = controller.getModelRunInformation(diseaseGroupId);

        // Assert
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody().getLastModelRunText()).isEqualTo("requested on 2 Jul 2014 14:15:16");
        assertThat(entity.getBody().getDiseaseOccurrencesText()).isEqualTo("none");
        assertThat(entity.getBody().isHasModelBeenSuccessfullyRun()).isTrue();
        assertThat(entity.getBody().isCanRunModel()).isFalse();
        assertThat(entity.getBody().getCannotRunModelReason()).isEqualTo("the public name is missing");
    }

    @Test
    public void requestModelRun() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        controller.requestModelRun(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService, times(1)).prepareForAndRequestManualModelRun(eq(diseaseGroupId));
    }

    ///CHECKSTYLE:OFF ParameterNumber - constructor for tests
    private DiseaseGroup createDiseaseGroup(int id, Integer parentGroupId, String name, String parentName,
                                            DiseaseGroupType groupType, String publicName, String shortName,
                                            String abbreviation, boolean isGlobal, Integer validatorDiseaseGroupId,
                                            double weighting) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(id);
        if (parentGroupId != null) {
            DiseaseGroup parentGroup = createParentDiseaseGroup(parentGroupId, parentName);
            diseaseGroup.setParentGroup(parentGroup);
        }
        diseaseGroup.setName(name);
        diseaseGroup.setGroupType(groupType);
        diseaseGroup.setPublicName(publicName);
        diseaseGroup.setShortName(shortName);
        diseaseGroup.setAbbreviation(abbreviation);
        diseaseGroup.setGlobal(isGlobal);
        if (validatorDiseaseGroupId != null) {
            ValidatorDiseaseGroup validatorDiseaseGroup = new ValidatorDiseaseGroup(validatorDiseaseGroupId);
            diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        }
        diseaseGroup.setWeighting(weighting);
        return diseaseGroup;
    }
    ///CHECKSTYLE:ON ParameterNumber

    private DiseaseGroup createParentDiseaseGroup(int id, String name) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(id);
        diseaseGroup.setName(name);
        return diseaseGroup;
    }

    @Test
    public void saveSettingsCallsSaveForDiseaseGroup() throws Exception {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup(1, 87, "Name", "Parent Name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseService.getValidatorDiseaseGroupById(4)).thenReturn(new ValidatorDiseaseGroup());

        // Act
        ResponseEntity result = controller.saveSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(diseaseService, times(1)).saveDiseaseGroup(diseaseGroup);
    }

    @Test
    public void saveSettingsReturnsBadRequestForInvalidDiseaseGroup() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseGroupById(anyInt())).thenReturn(null);

        // Act
        ResponseEntity result = controller.saveSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveSettingsReturnsBadRequestForInvalidParentDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        int parentId = 87;
        DiseaseGroup diseaseGroup = createDiseaseGroup(diseaseGroupId, parentId, "Name", "Parent name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseGroupById(parentId)).thenReturn(null);

        // Act
        ResponseEntity result = controller.saveSettings(diseaseGroupId, "New name", "New public name", "New short name", "NEWABBREV", "MICROCLUSTER", false, parentId, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveSettingsReturnsBadRequestForInvalidValidatorDiseaseGroup() throws Exception {
        // Arrange
        int validatorId = 4;
        when(diseaseService.getValidatorDiseaseGroupById(validatorId)).thenReturn(null);

        // Act
        ResponseEntity result = controller.saveSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, validatorId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
