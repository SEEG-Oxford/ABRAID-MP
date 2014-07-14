package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
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
    public void showPageAddsAllDiseaseGroupsToInitialData() throws JsonProcessingException {
        // Arrange
        Model model = mock(Model.class);

        DiseaseGroup diseaseGroup1 = createDiseaseGroup(188, 5, "Leishmaniases", DiseaseGroupType.MICROCLUSTER,
                "leishmaniases", "leishmaniases", "leish", true, 9, 0.5);
        DiseaseGroup diseaseGroup2 = createDiseaseGroup(87, null, "Dengue", DiseaseGroupType.SINGLE,
                "dengue", "dengue", "deng", false, 4, 1);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2);
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);
        String expectedJson = "[" +
                "{\"id\":87,\"name\":\"Dengue\",\"groupType\":\"SINGLE\",\"publicName\":\"dengue\",\"shortName\":\"dengue\",\"abbreviation\":\"deng\",\"isGlobal\":false,\"validatorDiseaseGroupId\":4,\"weighting\":1.0,\"automaticModelRuns\":false}," +
                "{\"id\":188,\"parentId\":5,\"name\":\"Leishmaniases\",\"groupType\":\"MICROCLUSTER\",\"publicName\":\"leishmaniases\",\"shortName\":\"leishmaniases\",\"abbreviation\":\"leish\",\"isGlobal\":true,\"validatorDiseaseGroupId\":9,\"weighting\":0.5,\"automaticModelRuns\":false}" +
                "]";

        // Act
        String result = controller.showPage(model);

        // Assert
        assertThat(result).isEqualTo("admindiseasegroup");
        verify(model, times(1)).addAttribute("initialData", expectedJson);
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
        verify(modelRunWorkflowService, times(1)).prepareForAndRequestManuallyTriggeredModelRun(eq(diseaseGroupId));
    }

    ///CHECKSTYLE:OFF ParameterNumber - constructor for tests
    private DiseaseGroup createDiseaseGroup(int id, Integer parentGroupId, String name,
                                            DiseaseGroupType groupType, String publicName, String shortName,
                                            String abbreviation, boolean isGlobal, Integer validatorDiseaseGroupId,
                                            double weighting) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(id);
        if (parentGroupId != null) {
            DiseaseGroup parentGroup = new DiseaseGroup(parentGroupId);
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
}
