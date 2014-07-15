package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the AdminDiseaseGroupController.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminDiseaseGroupControllerTest {
    @Test
    public void showPageAddsDiseaseGroupsAndValidatorDiseaseGroupsToModel() throws JsonProcessingException {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        GeoJsonObjectMapper geoJsonObjectMapper = new GeoJsonObjectMapper();
        AdminDiseaseGroupController controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper);
        Model model = mock(Model.class);

        DiseaseGroup diseaseGroup1 = createDiseaseGroup(188, 5, "Leishmaniases", "leishmaniases", DiseaseGroupType.MICROCLUSTER,
                "leishmaniases", "leishmaniases", "leish", true, 9, 0.5);
        DiseaseGroup diseaseGroup2 = createDiseaseGroup(87, null, "Dengue", null, DiseaseGroupType.SINGLE,
                "dengue", "dengue", "deng", false, 4, 1);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2);
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);
        String expectedJson = "[" +
                "{\"id\":188,\"name\":\"Leishmaniases\",\"publicName\":\"leishmaniases\",\"shortName\":\"leishmaniases\",\"abbreviation\":\"leish\",\"groupType\":\"MICROCLUSTER\",\"isGlobal\":true,\"parentDiseaseGroup\":{\"id\":5,\"name\":\"leishmaniases\"},\"validatorDiseaseGroup\":{\"id\":9},\"weighting\":0.5,\"automaticModelRuns\":false}," +
                "{\"id\":87,\"name\":\"Dengue\",\"publicName\":\"dengue\",\"shortName\":\"dengue\",\"abbreviation\":\"deng\",\"groupType\":\"SINGLE\",\"isGlobal\":false,\"validatorDiseaseGroup\":{\"id\":4},\"weighting\":1.0,\"automaticModelRuns\":false}]";

        ValidatorDiseaseGroup validator1 = new ValidatorDiseaseGroup(2, "CCHF");
        ValidatorDiseaseGroup validator2 = new ValidatorDiseaseGroup(3, "cholera");
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(Arrays.asList(validator1, validator2));
        String expectedValidatorJson = "[{\"id\":2,\"name\":\"CCHF\"},{\"id\":3,\"name\":\"cholera\"}]";

        // Act
        String result = controller.showPage(model);

        // Assert
        assertThat(result).isEqualTo("admindiseasegroup");
        verify(model, times(1)).addAttribute("diseaseGroups", expectedJson);
        verify(model, times(1)).addAttribute("validatorDiseaseGroups", expectedValidatorJson);
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
    public void saveMainSettingsCallsSaveForDiseaseGroup() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        GeoJsonObjectMapper geoJsonObjectMapper = new GeoJsonObjectMapper();
        AdminDiseaseGroupController controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper);
        DiseaseGroup diseaseGroup = createDiseaseGroup(1, 87, "Name", "Parent Name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);

        // Act
        ResponseEntity result = controller.saveMainSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(diseaseService, times(1)).saveDiseaseGroup(diseaseGroup);
    }

    @Test
    public void saveMainSettingsReturnsBadRequestForInvalidDiseaseGroup() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        GeoJsonObjectMapper geoJsonObjectMapper = new GeoJsonObjectMapper();
        AdminDiseaseGroupController controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper);
        when(diseaseService.getDiseaseGroupById(anyInt())).thenThrow(new IllegalArgumentException());

        // Act
        ResponseEntity result = controller.saveMainSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveMainSettingsReturnsBadRequestForInvalidParentDiseaseGroup() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        GeoJsonObjectMapper geoJsonObjectMapper = new GeoJsonObjectMapper();
        AdminDiseaseGroupController controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper);

        int diseaseGroupId = 1;
        int parentId = 87;
        DiseaseGroup diseaseGroup = createDiseaseGroup(diseaseGroupId, parentId, "Name", "Parent name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseGroupById(parentId)).thenThrow(new IllegalArgumentException());

        // Act
        ResponseEntity result = controller.saveMainSettings(diseaseGroupId, "New name", "New public name", "New short name", "NEWABBREV", "MICROCLUSTER", false, parentId, 4);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveMainSettingsReturnsBadRequestForInvalidValidatorDiseaseGroup() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        GeoJsonObjectMapper geoJsonObjectMapper = new GeoJsonObjectMapper();
        AdminDiseaseGroupController controller = new AdminDiseaseGroupController(diseaseService, geoJsonObjectMapper);
        int validatorId = 4;
        when(diseaseService.getDiseaseGroupById(validatorId)).thenThrow(new IllegalArgumentException());

        // Act
        ResponseEntity result = controller.saveMainSettings(1, "New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, validatorId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
