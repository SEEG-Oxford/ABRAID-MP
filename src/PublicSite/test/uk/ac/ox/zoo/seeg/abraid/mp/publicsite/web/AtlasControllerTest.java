package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the Atlas controller.
 * Copyright (c) 2014 University of Oxford
 */
public class AtlasControllerTest {

    @Test
    public void showPageReturnsAtlasPage() {
        // Arrange
        AtlasController target = new AtlasController(null, null, null);

        // Act
        String result = target.showPage();

        // Assert
        assertThat(result).isEqualTo("atlas/index");
    }

    @Test
    public void showAtlasReturnsAtlasContent() throws JsonProcessingException {
        // Arrange
        AtlasController target = new AtlasController(mock(ModelRunService.class), mock(DiseaseService.class), mock(AbraidJsonObjectMapper.class));

        // Act
        String result = target.showAtlas(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("atlas/content");
    }

    @Test
    public void showAtlasTemplatesTheCorrectData() throws JsonProcessingException {
        // Arrange
        ModelRunService modelRunService = mock(ModelRunService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        stubLayerRelatedServices(modelRunService, diseaseService);
        AtlasController target = new AtlasController(modelRunService, diseaseService, new AbraidJsonObjectMapper());

        // Act
        Model model = mock(Model.class);
        target.showAtlas(model);

        // Assert
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("layers"), argumentCaptor.capture());
        String expectation =
            "[" +
                "{\"disease\":\"Disease Group 1\",\"runs\":[" +
                    "{\"date\":\"2014-10-13\",\"id\":\"Model Run 1\"}," +
                    "{\"date\":\"2036-12-18\",\"id\":\"Model Run 3\"}" +
                "]}," +
                "{\"disease\":\"Disease Group 2\",\"runs\":[" +
                    "{\"date\":\"1995-10-09\",\"id\":\"Model Run 2\"}" +
                "]}" +
            "]";
        String value = argumentCaptor.getValue();
        assertThat(value).contains(expectation);
    }

    private void stubLayerRelatedServices(ModelRunService modelRunService, DiseaseService diseaseService) {
        ModelRun modelRun1 = mock(ModelRun.class);
        ModelRun modelRun2 = mock(ModelRun.class);
        ModelRun modelRun3 = mock(ModelRun.class);
        DiseaseGroup diseaseGroup1 = mock(DiseaseGroup.class);
        DiseaseGroup diseaseGroup2 = mock(DiseaseGroup.class);

        when(modelRun1.getDiseaseGroupId()).thenReturn(1);
        when(modelRun2.getDiseaseGroupId()).thenReturn(2);
        when(modelRun3.getDiseaseGroupId()).thenReturn(1);
        when(modelRun1.getRequestDate()).thenReturn(new DateTime(2014, 10, 13, 12, 0));
        when(modelRun2.getRequestDate()).thenReturn(new DateTime(1995, 10, 9, 12, 0));
        when(modelRun3.getRequestDate()).thenReturn(new DateTime(2036, 12, 18, 12, 0));
        when(modelRun1.getName()).thenReturn("Model Run 1");
        when(modelRun2.getName()).thenReturn("Model Run 2");
        when(modelRun3.getName()).thenReturn("Model Run 3");

        when(diseaseGroup1.getPublicName()).thenReturn("Disease Group 1");
        when(diseaseGroup2.getPublicName()).thenReturn("Disease Group 2");

        when(modelRunService.getCompletedModelRuns()).thenReturn(Arrays.asList(modelRun1, modelRun2, modelRun3));

        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup1);
        when(diseaseService.getDiseaseGroupById(2)).thenReturn(diseaseGroup2);
    }
}
