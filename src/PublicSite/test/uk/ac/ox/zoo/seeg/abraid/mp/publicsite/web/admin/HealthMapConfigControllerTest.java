package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonHealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonHealthMapSubDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonNamedEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.HealthMapService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapConfigController class.
 * Copyright (c) 2015 University of Oxford
 */
public class HealthMapConfigControllerTest {
    @Test
    public void getHealthMapConfigPageReturnsCorrectTemplateAndData() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        ObjectWriter objectWriter = (new ObjectMapper()).writer();
        when(objectMapper.writer()).thenReturn(objectWriter);

        List<HealthMapDisease> healthMapDiseases = Arrays.asList(createMockHealthMapDisease(1, "A", 2, "B"));
        when(healthMapService.getAllHealthMapDiseases()).thenReturn(healthMapDiseases);

        List<HealthMapSubDisease> healthMapSubDiseases = Arrays.asList(createMockHealthMapSubDisease(3, "C", 4, "D", 5, "E"));
        when(healthMapService.getAllHealthMapSubDiseases()).thenReturn(healthMapSubDiseases);

        List<DiseaseGroup> diseaseGroups = Arrays.asList(createMockAbraidDisease(6, "F"));
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);
        Model model = mock(Model.class);

        // Act
        String result = target.getHealthMapConfigPage(model);

        // Assert
        assertThat(result).isEqualTo("admin/healthMapConfig");
        verify(model).addAttribute("healthMapDiseases", "[{\"id\":1,\"name\":\"A\",\"abraidDisease\":{\"id\":2,\"name\":\"B\"}}]");
        verify(model).addAttribute("healthMapSubDiseases", "[{\"id\":3,\"name\":\"C\",\"abraidDisease\":{\"id\":4,\"name\":\"D\"},\"parent\":{\"id\":5,\"name\":\"E\"}}]");
        verify(model).addAttribute("abraidDiseases", "[{\"id\":6,\"name\":\"F\"}]");
    }

    @Test
    public void getHealthMapConfigPageReturnsCorrectTemplateAndDataWithoutLinkedDiseases() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        ObjectWriter objectWriter = (new ObjectMapper()).writer();
        when(objectMapper.writer()).thenReturn(objectWriter);

        List<HealthMapDisease> healthMapDiseases = Arrays.asList(createMockHealthMapDisease(1, "A", null, null));
        when(healthMapService.getAllHealthMapDiseases()).thenReturn(healthMapDiseases);

        List<HealthMapSubDisease> healthMapSubDiseases = Arrays.asList(createMockHealthMapSubDisease(3, "C", null, null, null, null));
        when(healthMapService.getAllHealthMapSubDiseases()).thenReturn(healthMapSubDiseases);

        List<DiseaseGroup> diseaseGroups = Arrays.asList(createMockAbraidDisease(6, "F"));
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);
        Model model = mock(Model.class);

        // Act
        String result = target.getHealthMapConfigPage(model);

        // Assert - Note: nulls will not be serialized (undefined) using the real AbraidJsonObjectMapper.
        assertThat(result).isEqualTo("admin/healthMapConfig");
        verify(model).addAttribute("healthMapDiseases", "[{\"id\":1,\"name\":\"A\",\"abraidDisease\":null}]");
        verify(model).addAttribute("healthMapSubDiseases", "[{\"id\":3,\"name\":\"C\",\"abraidDisease\":null,\"parent\":null}]");
        verify(model).addAttribute("abraidDiseases", "[{\"id\":6,\"name\":\"F\"}]");
    }

    @Test
    public void updateHealthMapDiseaseRejectsInvalidJson() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(1, "2", 3, "4");
        when(healthMapService.getHealthMapDiseasesById(1)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(3, "4");
        when(diseaseService.getDiseaseGroupById(3)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act/Assert
        assertThat(target.updateHealthMapDisease(null).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(null, "2", 3, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, null, 3, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "", 3, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, " ", 3, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "2", null, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "2", 3, null)).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "2", 3, "")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "2", 3, " ")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(null, null, null, null)).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateHealthMapDiseaseRejectsJsonWithInvalidIds() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(1, "2", 3, "4");
        when(healthMapService.getHealthMapDiseasesById(1)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(3, "4");
        when(diseaseService.getDiseaseGroupById(3)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act/Assert
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(5, "2", 3, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapDisease(createMockJsonHealthMapDisease(1, "2", 5, "4")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateHealthMapDiseaseUpdatesDatabaseCorrectly() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(1, "2", 3, "4");
        when(healthMapService.getHealthMapDiseasesById(1)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(5, "6");
        when(diseaseService.getDiseaseGroupById(5)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapDisease json = createMockJsonHealthMapDisease(1, "2", 5, "6");
        ResponseEntity result = target.updateHealthMapDisease(json);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapDisease, healthMapService);
        inOrder.verify(healthMapDisease).setDiseaseGroup(abraidDisease);
        inOrder.verify(healthMapService).saveHealthMapDisease(healthMapDisease);
    }

    @Test
    public void updateHealthMapDiseaseUpdatesDatabaseCorrectlyWithNullLinkedAbraidDisease() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(1, "2", 3, "4");
        when(healthMapService.getHealthMapDiseasesById(1)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapDisease json = createMockJsonHealthMapDisease(1, "2", null, null);
        ResponseEntity result = target.updateHealthMapDisease(json);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapDisease, healthMapService);
        inOrder.verify(healthMapDisease).setDiseaseGroup(null);
        inOrder.verify(healthMapService).saveHealthMapDisease(healthMapDisease);
    }

    @Test
    public void updateHealthMapSubDiseaseRejectsInvalidJson() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(5, "6", 7, "8");
        when(healthMapService.getHealthMapDiseasesById(5)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(3, "4");
        when(diseaseService.getDiseaseGroupById(3)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act/Assert
        assertThat(target.updateHealthMapSubDisease(null).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(null, "2", 3, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, null, 3, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "", 3, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, " ", 3, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", null, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, null, 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, "", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, " ", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, "4", null, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, "4", 5, "")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, "4", 5, " ")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(null, null, null, null, null, null)).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateHealthMapSubDiseaseRejectsJsonWithInvalidIds() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(5, "6", 7, "8");
        when(healthMapService.getHealthMapDiseasesById(5)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(3, "4");
        when(diseaseService.getDiseaseGroupById(3)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act/Assert
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(9, "2", 3, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 9, "4", 5, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(target.updateHealthMapSubDisease(createMockJsonHealthMapSubDisease(1, "2", 3, "4", 9, "6")).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateHealthMapSubDiseaseUpdatesDatabaseCorrectly() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(9, "10", 11, "12");
        when(healthMapService.getHealthMapDiseasesById(9)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(7, "8");
        when(diseaseService.getDiseaseGroupById(7)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapSubDisease json = createMockJsonHealthMapSubDisease(1, "2", 7, "8", 9, "10");
        ResponseEntity result = target.updateHealthMapSubDisease(json);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapSubDisease, healthMapService);
        inOrder.verify(healthMapSubDisease).setDiseaseGroup(abraidDisease);
        inOrder.verify(healthMapSubDisease).setHealthMapDisease(healthMapDisease);
        inOrder.verify(healthMapService).saveHealthMapSubDisease(healthMapSubDisease);
    }

    @Test
    public void updateHealthMapSubDiseaseUpdatesDatabaseCorrectlyWithNullLinkedAbraidDisease() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
        HealthMapDisease healthMapDisease = createMockHealthMapDisease(5, "6", 7, "8");
        when(healthMapService.getHealthMapDiseasesById(5)).thenReturn(healthMapDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapSubDisease json = createMockJsonHealthMapSubDisease(1, "2", null, null, 5, "6");
        ResponseEntity result = target.updateHealthMapSubDisease(json);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapSubDisease, healthMapService);
        inOrder.verify(healthMapSubDisease).setDiseaseGroup(null);
        inOrder.verify(healthMapSubDisease).setHealthMapDisease(healthMapDisease);
        inOrder.verify(healthMapService).saveHealthMapSubDisease(healthMapSubDisease);
    }

    @Test
    public void updateHealthMapSubDiseaseUpdatesDatabaseCorrectlyWithNullLinkedParentDisease() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
         DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup abraidDisease = createMockAbraidDisease(3, "4");
        when(diseaseService.getDiseaseGroupById(3)).thenReturn(abraidDisease);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapSubDisease json = createMockJsonHealthMapSubDisease(1, "2", 3, "4", null, null);
        ResponseEntity result = target.updateHealthMapSubDisease(json);

        // Act/Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapSubDisease, healthMapService);
        inOrder.verify(healthMapSubDisease).setDiseaseGroup(abraidDisease);
        inOrder.verify(healthMapSubDisease).setHealthMapDisease(null);
        inOrder.verify(healthMapService).saveHealthMapSubDisease(healthMapSubDisease);
    }

    @Test
    public void updateHealthMapSubDiseaseUpdatesDatabaseCorrectlyWithNullLinkedBothDiseases() throws Exception {
        // Arrange
        HealthMapService healthMapService = mock(HealthMapService.class);
        HealthMapSubDisease healthMapSubDisease = createMockHealthMapSubDisease(1, "2", 3, "4", 5, "6");
        when(healthMapService.getHealthMapSubDiseasesById(1)).thenReturn(healthMapSubDisease);
        DiseaseService diseaseService = mock(DiseaseService.class);
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);

        HealthMapConfigController target = new HealthMapConfigController(healthMapService, diseaseService, objectMapper);

        // Act
        JsonHealthMapSubDisease json = createMockJsonHealthMapSubDisease(1, "2", null, null, null, null);
        ResponseEntity result = target.updateHealthMapSubDisease(json);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        InOrder inOrder = inOrder(healthMapSubDisease, healthMapService);
        inOrder.verify(healthMapSubDisease).setDiseaseGroup(null);
        inOrder.verify(healthMapSubDisease).setHealthMapDisease(null);
        inOrder.verify(healthMapService).saveHealthMapSubDisease(healthMapSubDisease);
    }

    private HealthMapDisease createMockHealthMapDisease(Integer id, String name, Integer abraidId, String abraidName) {
        HealthMapDisease mock = mock(HealthMapDisease.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        if (abraidId != null || abraidName != null) {
            DiseaseGroup diseaseGroup = createMockAbraidDisease(abraidId, abraidName);
            when(mock.getDiseaseGroup()).thenReturn(diseaseGroup);
        }
        return mock;
    }

    private HealthMapSubDisease createMockHealthMapSubDisease(Integer id, String name, Integer abraidId, String abraidName, Integer parentId, String parentName) {
        HealthMapSubDisease mock = mock(HealthMapSubDisease.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        if (abraidId != null || abraidName != null) {
            DiseaseGroup diseaseGroup = createMockAbraidDisease(abraidId, abraidName);
            when(mock.getDiseaseGroup()).thenReturn(diseaseGroup);
        }
        if (parentId != null || parentName != null) {
            HealthMapDisease parent = mock(HealthMapDisease.class);
            when(parent.getId()).thenReturn(parentId);
            when(parent.getName()).thenReturn(parentName);
            when(mock.getHealthMapDisease()).thenReturn(parent);
        }
        return mock;
    }

    private DiseaseGroup createMockAbraidDisease(Integer id, String name) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

    private JsonHealthMapDisease createMockJsonHealthMapDisease(Integer id, String name, Integer abraidId, String abraidName) {
        JsonHealthMapDisease mock = mock(JsonHealthMapDisease.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        if (abraidId != null || abraidName != null) {
            JsonNamedEntry diseaseGroup = createMockJsonNamedEntry(abraidId, abraidName);
            when(mock.getAbraidDisease()).thenReturn(diseaseGroup);
        }
        return mock;
    }

    private JsonHealthMapSubDisease createMockJsonHealthMapSubDisease(Integer id, String name, Integer abraidId, String abraidName, Integer parentId, String parentName) {
        JsonHealthMapSubDisease mock = mock(JsonHealthMapSubDisease.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        if (abraidId != null || abraidName != null) {
            JsonNamedEntry diseaseGroup = createMockJsonNamedEntry(abraidId, abraidName);
            when(mock.getAbraidDisease()).thenReturn(diseaseGroup);
        }
        if (parentId != null || parentName != null) {
            JsonNamedEntry parent = createMockJsonNamedEntry(parentId, parentName);
            when(mock.getParent()).thenReturn(parent);
        }
        return mock;
    }

    private JsonNamedEntry createMockJsonNamedEntry(Integer id, String name) {
        JsonNamedEntry mock = mock(JsonNamedEntry.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        return mock;
    }
}
